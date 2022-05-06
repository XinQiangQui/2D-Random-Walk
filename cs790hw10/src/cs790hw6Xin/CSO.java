/**
 * @title:		CSO
 * @use:		CSO (cyberspatial object) maps an area.
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/
package cs790hw6Xin;

import java.util.concurrent.*;

/**
 * This class acts as a supervisory CSO and starts the random walk and the three services: SAS, PGS, and PES. Extends Thread.
 * The goal of the CSO is to map the entirety of an area (currently, the area of the random walk).
 * All team members worked together on every component of this project.
 */
public class CSO extends Thread{
	/** 
	 * Concurrency: Below we prepare BlockingQueues to safely send forward various data objects from service to service and to/from the RandomWalk
	 */
	private final BlockingQueue<Assessment> SAStoPGSQueue = new LinkedBlockingQueue<Assessment>();  // BlockingQueue for transferring data from SAS to PGS
	private final BlockingQueue<POA> PGStoPESQueue = new LinkedBlockingQueue<POA>();  // BlockingQueue for transferring data from PGS to PES
	private final BlockingQueue<Result> RWtoSASQueue = new LinkedBlockingQueue<Result>();  // BlockingQueue for transferring data from random walk to SAS
	private final BlockingQueue<NextStep> PEStoRWQueue = new LinkedBlockingQueue<NextStep>();  // BlockingQueue for transferring data from PES to RandomWalk (i.e., executing plan)
	public final int poolSize = 4;  // thread pool size
	
	// fuel tank 
	final int refuelAmount = 60;  // The amount of fuel gained by picking up fuel
	final int startingFuel = 250;  // The amount of fuel the walker starts with
	final int fuelMax = startingFuel;  // The maximum amount of fuel the walker can carry at one time
	
	/** 
	 * Concurrency: Below we prepare threads on which the RandomWalk and three services will run concurrently
	 */
	// threads and services
	Thread RandomThread, SASThread, PGSThread, PESThread;
	SAS sas;
	PGS pgs;
	PES pes;
	RandomWalk rw;

	/**
	 * Creates ExecutorService and uses it to start random walk, SAS, PGS, and PES
	 */
	public void start() {
		// creating instances of random walk and services, and threads on which to execute them
		rw = new RandomWalk(PEStoRWQueue,RWtoSASQueue);
		RandomThread = new Thread (rw);
		sas = new SAS(RWtoSASQueue, SAStoPGSQueue);
		SASThread = new Thread(sas);
		pgs = new PGS(SAStoPGSQueue,PGStoPESQueue);
		PGSThread = new Thread(pgs);
		pes = new PES(PGStoPESQueue,PEStoRWQueue);
		PESThread = new Thread(pes);
		
		/** 
		 * Concurrency: ExecutorService facilitates execution of the 4 threads, which will run concurrently
		 */
		ExecutorService taskList = Executors.newFixedThreadPool(poolSize);  // ExecutorService to manage our threads
		
		// make tasks available for execution
		taskList.execute(RandomThread);
		taskList.execute(SASThread);
		taskList.execute(PGSThread);
		taskList.execute(PESThread);
		taskList.shutdown();
	}
	
	/**
	 * Main method, simply calls start().
	 * @param args (unused)
	 */
	public static void main(String[] args)
	{
		new CSO().start();
	}
}
