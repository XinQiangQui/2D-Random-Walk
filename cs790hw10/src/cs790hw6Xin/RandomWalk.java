/**
 * @title:		RandomWalk
 * @use:		Simulates a 2D random walk and plots the trajectory. Works best for an n that divides 600.
 * @author:     Robert Sedgewick
 * @author:     Kevin Wayne
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/

package cs790hw6Xin;

import java.util.concurrent.BlockingQueue;

/**
 * Class for random walk. Implements Runnable.
 * All team members worked together on every component of this project.
 * 
 * Adapted from original RandomWalk by Robert Sedgewick and Kevin Wayne, Copyright © 2000–2019.
 * Originally retrieved from https://introcs.cs.princeton.edu/java/15inout/RandomWalk.java.html
 * 
 * Dependent on StdDraw.java
 * 
 * All team members worked together on every component of this project.
 */
public class RandomWalk extends CSO implements Runnable{ 
	public static int x = 0;  // x position
	public static int y = 0;  // y position
	public static int n = 50;  // informs width of space for walker to traverse (width and height is 2n+1)
	public static double r = 0;  // random variable
	private BlockingQueue<NextStep> pesQueue;  // Blocking queue to receive instructions (NextStep) from PES
	private BlockingQueue<Result> sasQueue;  // Blocking queue to send position data to SAS
	public static boolean fuelHere;  // whether there is fuel at the current position
	public static boolean obstacleHere;  // whether there is an obstacle at the current position
	
	private NextStep nextStep;
	final double fuelProbability = 0.15;  // likelihood of fuel to be present at any given square
	final double obstacleProbability = 0.08;  // likelihood of obstacle at a particular square
	
	/**
	 * Initializer
	 * @param BlockingQueue<NextStep>
	 * @param BlockingQueue<Result>
	 */
	public RandomWalk(BlockingQueue<NextStep> pesQueue, BlockingQueue<Result> sasQueue) {
		this.sasQueue = sasQueue;
		this.pesQueue = pesQueue;
	}
	
	/**
	 * Run method. Operates random walk.
	 */
    public void run() {
        StdDraw.setScale(-n - 0.5, n + 0.5);
        StdDraw.clear(StdDraw.GRAY);
        StdDraw.enableDoubleBuffering();
        
        int[][] thingLocations = new int[2*n+1][2*n+1];  // tracks if there is fuel or an obstacle at each position. 0 for nothing, 1 for fuel, 2 for obstacle
        int[] vision = new int[4]; // holds the type of space (nothing/fuel/obstacle) in each immediate direction, in the order: North, East, South, West
        
        //paint the fuel squares green and obstacle squares orange, and record their positions
        for(int i = 1; i < thingLocations.length -1; i++) {
        	for(int j = 1; j < thingLocations[0].length -1; j++) {
        		if(Math.random() < fuelProbability) {
        			thingLocations[i][j] = 1;
        			StdDraw.setPenColor(StdDraw.GREEN);
                    StdDraw.filledSquare(i -n, j -n, 0.45);
        		} else if(Math.random() < fuelProbability + obstacleProbability){
        			thingLocations[i][j] = 2;
        			StdDraw.setPenColor(StdDraw.ORANGE);
                    StdDraw.filledSquare(i -n, j -n, 0.45);
        		}
        	}
        }
        
        int steps = 0; // counter for steps
        while (Math.abs(x) < n && Math.abs(y) < n) {  // while the walker does not enter an outer barrier (which it cannot)
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledSquare(x, y, 0.45);
            
            if(steps > 0) {  // if this is not the first step
	            try {
	            	/** 
	    			 * Concurrency: Use BlockingQueue to get NextStep from PES
	    			 */
	    			nextStep = pesQueue.take();  // get next step from PES
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
	            // if fuel tank is not empty, move one square
	            if(nextStep.fuel > 0  &&  nextStep.direction != null) {
	            	if (nextStep.direction == SAS.Direction.EAST) x++;
		            if (nextStep.direction == SAS.Direction.WEST) x--;
		            if (nextStep.direction == SAS.Direction.SOUTH) y--;
		            if (nextStep.direction == SAS.Direction.NORTH) y++;
	            }
	            else
	            	break;  // here, fuel is empty so we stop
            } else {  // this is the first step so we pick a purely random direction to move in
            	r = Math.random();
	            if      (r < 0.25) x--; //west
	            else if (r < 0.50) x++; //east
	            else if (r < 0.75) y--; //south
	            else if (r < 1.00) y++; //north
            }

	        steps++;
            
	        fuelHere = false;
    		if(thingLocations[x+n][y+n] == 1) { // boolean map for fuel 
    			fuelHere = true;
    			thingLocations[x+n][y+n] = 0; // turn the fuel square into normal square after visited
    		}
    		
    		// recording vision: the type of square immediately in each direction
    		if(y < n)
    			vision[0] = thingLocations[x+n][y+n+1]; // north
    		if(x < n)
    			vision[1] = thingLocations[x+n+1][y+n]; // east
    		if(y > n * -1)
    			vision[2] = thingLocations[x+n][y+n-1]; // south
    		if(x > n * -1)
    			vision[3] = thingLocations[x+n-1][y+n]; // west
            
            Result result;
            if(nextStep != null) {  // if we have a Result to work with
            	result = new Result(x, y, nextStep.fuel, fuelHere, vision);
            }
            else  // if this is the first step
            	result = new Result(x, y, startingFuel, fuelHere, vision);
            
            try {
            	/** 
    			 * Concurrency: Use BlockingQueue to send result (new state) to SAS
    			 */
				sasQueue.put(result);  // send result to SAS
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.filledSquare(x, y, 0.45);
            StdDraw.show();
            StdDraw.pause(40);  // pause between steps, in milliseconds
        }
        System.out.println("Total steps = " + steps);
    }
}