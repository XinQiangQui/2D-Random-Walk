/**
 * @title:		Result
 * @use:		Object class that stores fuel related variables
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/

package cs790hw6Xin;

/**
 * Result object, for the SAS to understand what happened in the RandomWalk
 * All team members worked together on every component of this project.
 */
public class Result {
	int x, y, fuel;  // x position, y position, current fuel
	boolean fuelHere;  // whether there is fuel at the current position
	int[] vision = new int[4];  // holds the type of space (nothing/fuel/obstacle) in each immediate direction, in the order: North, East, South, West

	/**
	 * Constructor
	 * @param x coordinate
	 * @param y coordinate
	 * @param current fuel
	 * @param availabilty of fuel in current position
	 */
	public Result(int x, int y, int fuel, boolean fuelHere, int[] vision) {
		this.x = x;
		this.y = y;
		this.fuel = fuel;
		this.fuelHere = fuelHere;
		this.vision = vision;
	}
}
