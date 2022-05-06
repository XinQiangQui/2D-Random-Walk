/**
 * @title:		PES
 * @use:		PES (plan execution service) skeleton code
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/
package cs790hw6Xin;

import java.util.concurrent.*;

/**
 * This class is a plan execution service that executes a plan received from PGS. Extends CSO, implements Runnable.
 * All team members worked together on every component of this project.
 */
public class PES extends CSO implements Runnable{
	private final BlockingQueue<POA> PGSQueue;  // BlockingQueue for receiving data from PGS
	private final BlockingQueue<NextStep> rwQueue;  // BlockingQueue for sending instructions (executing action) to RandomWalk
	private POA currentPOA;  // current POA
	private int fuel;  // current fuel
	
	/**
	 * Initializer
	 * @param BlockingQueue<POA>
	 * @param BlockingQueue<NextStep>
	 */
	public PES(BlockingQueue<POA> pgsQueue, BlockingQueue<NextStep> rwQueue) {
		this.PGSQueue = pgsQueue;
		this.rwQueue = rwQueue;
	}
	
	/**
	 * Gets POA from PGS.
	 */
	public void getPOA() {
		try {
			/** 
			 * Concurrency: Use BlockingQueue to get POA from PGS
			 */
			currentPOA = PGSQueue.take();  // get POA from PGS
			fuel = currentPOA.fuel;  // update current fuel from POA
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes plan to alter course of walker.
	 */
	public void nudge() {		
		try {
			NextStep nextStep = new NextStep(currentPOA.intendedDirection, fuel);
			/** 
			 * Concurrency: Use BlockingQueue to send NextStep to RandomWalk
			 */
			rwQueue.put(nextStep);  // send (execute) NextStep to RandomWalk
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pick up fuel.
	 */
	private void pickUpFuel() {
		fuel += refuelAmount; // add fuel found to tank
		fuel = Math.min(fuel, fuelMax); // if fuel exceeds limit, fuel = tank max
	}
	
	/**
	 * Run method.
	 * Currently this prints statements to demonstrate concurrency: ascending numbers
	 * are received from PGS and printed.
	 */
	public void run() {
		while(true){
			getPOA();
			if(currentPOA.getFuel) {
				pickUpFuel();
			}
			fuel--; // cost of moving one square
			nudge();
		}
	}
}
