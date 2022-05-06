/**
 * @title:		SAS
 * @use:		SAS (situation assessment service) assesses situation, sends courses of action to PGS.
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/
package cs790hw6Xin;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * This class is a situation assessment service that assesses a situation and forwards its assessment to PGS. Extends CSO, implements Runnable.
 * All team members worked together on every component of this project.
 */
public class SAS extends CSO implements Runnable{
	private final BlockingQueue<Assessment> PGSQueue;  // BlockingQueue for sending data to PGS
	private int[] currentPosition;  // (geospatial) position of walker in random walk
	private Result currentResult;  // Result received from RandomWalk
	private boolean[][] map = new boolean[2*RandomWalk.n+1][2*RandomWalk.n+1];  // 2D map of space random walk is traversing, to track squares already visited
	private BlockingQueue<Result> rwQueue;  // BlockingQueue for receiving data (Result) from random walk
	
	private boolean hitNewSquare = false;  // event: walker reaches a square not already visited
	private boolean hitWall = false;  // event: walker is adjacent to at least one wall
	private boolean hitLastSquare = false;  // event: walker has visited every square
	private int visited = 0;  // number of squares that the walker has visited
	private int totalSquares = (int)Math.pow(2*RandomWalk.n+1, 2);  // total number of squares
	
	private int fuel = startingFuel;  // remaining fuel (walk ends if fuel runs out)
	
	/**
	 * Directions walker can move in (equivalent to up, right, down, left)
	 */
	public enum Direction {
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	/**
	 * Initializer
	 * @param BlockingQueue<Result>
	 * @param BlockingQueue<Assessment>
	 */
	public SAS(BlockingQueue<Result> rwQueue, BlockingQueue<Assessment> pgsQueue) {
		this.PGSQueue = pgsQueue;
		this.rwQueue = rwQueue;
	}
	
	/**
	 * Reads state of random walk and forwards assessment to PGS.
	 */
	public void read() {
		try {
			/** 
			 * Concurrency: Use BlockingQueue to get Result from RandomWalk
			 */
			currentResult = rwQueue.take();  // get current position of walker, from the random walk
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		currentPosition[0] = currentResult.x;
		currentPosition[1] = currentResult.y;
		
		System.out.println("Current position: "+currentPosition[0] + ", " + currentPosition[1]);

		// sense for events
		determineHitNewSquare();  // check if walker has reached an unvisited square
		determineHitWall();  // check if walker is adjacent to a wall
		determineHitLastSquare();  // check if walker has visited every square
		
		if(hitNewSquare)
			System.out.println("  Hit new square!");
		if(hitWall)
			System.out.println("  Adjacent to wall!");
		if(hitLastSquare)
			System.out.println("  Hit all squares!");
		if(currentResult.fuelHere)
			System.out.println("  Fuel here!");
		
		map[currentPosition[0]+RandomWalk.n][currentPosition[1]+RandomWalk.n] = true;  // mark in map that the current position has been visited
		ArrayList<Direction> possibleDirections = generateCOAs(currentResult.vision);  // generate candidate courses of action (directions in which to move)
		Assessment assessment = new Assessment(currentPosition[0], currentPosition[1], possibleDirections, hitNewSquare, hitWall, hitLastSquare, fuel, currentResult.fuelHere, currentResult.vision);
			try {
				/** 
				 * Concurrency: Use BlockingQueue to send Assessment to PGS
				 */
				PGSQueue.put(assessment);  // send assessment to PGS
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Generate candidate courses of action. Adds a direction to list of candidate directions
	 * if there is not a wall immediately in that direction.
	 * @return ArrayList<Direction>
	 */
	private ArrayList<Direction> generateCOAs(int[] vision){
		ArrayList<Direction> coa = new ArrayList<Direction>();  // courses of action
		
		if(currentPosition[0] > RandomWalk.n * -1 + 1  &&  vision[3] != 2)  // if x position is not westmost, and there is not an obstacle to the west, walker can move west
			coa.add(Direction.WEST);
		if(currentPosition[0] < RandomWalk.n - 1  &&  vision[1] != 2)  // if x position is not eastmost, and there is not an obstacle to the east, walker can move east
			coa.add(Direction.EAST);
		if(currentPosition[1] > RandomWalk.n * -1 + 1  &&  vision[2] != 2)  // if y position is not southmost, and there is not an obstacle to the south, walker can move south
			coa.add(Direction.SOUTH);
		if(currentPosition[1] < RandomWalk.n - 1  &&  vision[0] != 2)  // if y position is not northmost, and there is not an obstacle to the north, walker can move north
			coa.add(Direction.NORTH);
		
		return coa;
	}
	
	/**
	 * Determine if walker has reached an unvisited square. Sets hitNewSquare.
	 * Also increments visited if so.
	 */
	private void determineHitNewSquare() {
		if(!map[currentPosition[0]+RandomWalk.n][currentPosition[1]+RandomWalk.n]) {  // if this square has not been visited, walker has visited a new square
			hitNewSquare = true;
			visited++;
		}
		else
			hitNewSquare = false;
	}
	
	/**
	 * Determine if walker is adjacent to a wall. Sets hitWall.
	 */
	private void determineHitWall() {
		hitWall = (Math.abs(currentPosition[0]) >= RandomWalk.n - 1 || Math.abs(currentPosition[1]) >= RandomWalk.n - 1);
	}
	
	/**
	 * Determine if walker has visited every square. Sets hitLastSquare.
	 */
	private void determineHitLastSquare() {
		hitLastSquare = visited >= totalSquares;
	}
	
	/**
	 * Run method. Repeatedly calls read.
	 */
	@Override
	public void run() {
		currentPosition = new int[2];  // initializes currentPosition
		
		determineHitNewSquare();  // we do these once at the start to account for the starting square
		determineHitWall();
		determineHitLastSquare();
		
		while(true) {
			read();
			fuel = currentResult.fuel;
		}
	}
}
