/**
 * @title:		PGS
 * @use:		PGS (plan generation service) skeleton code
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/
package cs790hw6Xin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

/**
 * This class is a plan generation service that generates a plan based on a situation assessment from SAS, and forwards plans to PES. Extends CSO, implements Runnable.
 * All team members worked together on every component of this project.
 */
public class PGS extends CSO implements Runnable{
	private final BlockingQueue<Assessment> SASQueue;  // BlockingQueue for receiving data from SAS
	private final BlockingQueue<POA> PESQueue;  // BlockingQueue for sending data to PES
	private Assessment currentAssessment;  // Assessment from SAS
	private boolean[][] map = new boolean[2*RandomWalk.n+1][2*RandomWalk.n+1];  // 2D map of space random walk is traversing, to track squares already visited
	
	/**
	 * Initializer
	 * @param BlockingQueue<Assessment>
	 * @param BlockingQueue<POA>
	 */
	public PGS(BlockingQueue<Assessment> sasQueue, BlockingQueue<POA> pesQueue) {
		this.SASQueue = sasQueue;
		this.PESQueue = pesQueue;
	}
	
	/**
	 * Gets situation assessment from SAS.
	 */
	public void getSitrep() {
		try {
			/** 
			 * Concurrency: Use BlockingQueue to get Assessment from SAS
			 */
			currentAssessment = SASQueue.take();  // get Assessment from SAS
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Fuel: " + currentAssessment.fuel);  // print current fuel
		map[currentAssessment.x+RandomWalk.n][currentAssessment.y+RandomWalk.n] = true;  // mark in map that the current position has been visited
	}
	
	/**
	 * Calculates POA: plan of action.
	 */
	public void calculatePOA() {
		Collections.shuffle(currentAssessment.possibleDirections);  // shuffle the directions (so we don't only move in the first direction)
		SAS.Direction intendedDirection = null;  // the result of our decision making: the direction the walker should move in
		ArrayList<SAS.Direction> fuelDirections = new ArrayList<SAS.Direction>();  // directions in which there is fuel

		int[] vision = currentAssessment.vision;  // holds the type of space (nothing/fuel/obstacle) in each immediate direction, in the order: North, East, South, West
		
		if(vision[0] == 1) {  // if there is fuel immediately north
			fuelDirections.add(SAS.Direction.NORTH);
		}
		if(vision[1] == 1) {  // if there is fuel immediately east
			fuelDirections.add(SAS.Direction.EAST);
		}
		if(vision[2] == 1) {  // if there is fuel immediately south
			fuelDirections.add(SAS.Direction.SOUTH);
		}
		if(vision[3] == 1) {  // if there is fuel immediately west
			fuelDirections.add(SAS.Direction.WEST);
		}
		Collections.shuffle(fuelDirections);  // shuffle the fuel directions (so we pick a random one)
		if(!fuelDirections.isEmpty())  // if there is fuel adjacent, plan to move to one of those squares
			intendedDirection = fuelDirections.get(0);
		else {
			for (SAS.Direction dir : currentAssessment.possibleDirections){
				//plan to go north if north is possible and the north square has not been visited
				if(dir == SAS.Direction.NORTH && !map[currentAssessment.x +RandomWalk.n][currentAssessment.y+1 +RandomWalk.n]) {
					intendedDirection = SAS.Direction.NORTH;
					break;
				}
				//plan to go east if east is possible and the east square has not been visited
				else if(dir == SAS.Direction.EAST && !map[currentAssessment.x+1 +RandomWalk.n][currentAssessment.y +RandomWalk.n]) {
					intendedDirection = SAS.Direction.EAST;
					break;
				}
				//plan to go south if south is possible and the south square has not been visited
				else if(dir == SAS.Direction.SOUTH && !map[currentAssessment.x +RandomWalk.n][currentAssessment.y-1 +RandomWalk.n]) {
					intendedDirection = SAS.Direction.SOUTH;
					break;
				}
				//plan to go west if west is possible and the west square has not been visited
				else if(dir == SAS.Direction.WEST && !map[currentAssessment.x-1 +RandomWalk.n][currentAssessment.y +RandomWalk.n]) {
					intendedDirection = SAS.Direction.WEST;
					break;
				}
			}
		}
		// if all the squares around are visited, pick a random possible direction
		if(intendedDirection == null) {
			if(!currentAssessment.possibleDirections.isEmpty()) {  // if there are any possible directions to move in
				intendedDirection = currentAssessment.possibleDirections.get(0);  // this gets a random possible direction rather than the first, since it was shuffled
				System.out.println("All possible adjacent squares visited");
			} else {
				System.out.println("Cannot move in any direction");  // this only happens if the walker starts surrounded by obstacles in all four directions
			}
		}
		
		boolean getFuel = false;  // whether to get fuel
		// get fuel if current position has fuel and the fuel in the tank is less than fuelMax
		if(currentAssessment.fuelHere && currentAssessment.fuel < fuelMax) {
			getFuel = true;
		}
		
		// create a new object that stores decisions (intended direction, whether to get fuel) and other info (current fuel)
		POA poa = new POA(intendedDirection, currentAssessment.fuel, getFuel);
		
		try {
			/** 
			 * Concurrency: Use BlockingQueue to send POA to PES
			 */
			PESQueue.put(poa);  // send poa to PES
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Run method.
	 * Repeatedly makes calls to getSitrep and calculatePOA.
	 */
	public void run() {
		while(true) {
			getSitrep();
			calculatePOA();
		}
	}
}
