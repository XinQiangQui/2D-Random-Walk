/**
 * @title:		Assessment
 * @use:		Object class that includes all the needed variables to determine the movement of the random walk
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/

package cs790hw6Xin;

import java.util.ArrayList;

/**
 * Object that includes all the needed variables to determine the movement of the random walk
 * All team members worked together on every component of this project.
 */
public class Assessment {
	ArrayList<SAS.Direction> possibleDirections;  // ArrayList of directions that do not lead into a wall
	boolean hitNewSquare, hitWall, hitLastSquare, fuelHere;  // events
	int x, y, fuel;  // position, fuel
	int[] vision = new int[4];  // holds the type of space (nothing/fuel/obstacle) in each immediate direction, in the order: North, East, South, West

	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param possibleDirections
	 * @param hitNewSquare
	 * @param hitWall
	 * @param hitLastSquare
	 * @param fuel
	 */
	public Assessment(int x, int y, ArrayList<SAS.Direction> possibleDirections, boolean hitNewSquare, boolean hitWall, boolean hitLastSquare, int fuel, boolean fuelHere, int[] vision) {
		this.possibleDirections = possibleDirections;
		this.hitNewSquare = hitNewSquare;
		this.hitWall = hitWall;
		this.hitLastSquare = hitLastSquare;
		this.fuel = fuel;
		this.x = x;
		this.y = y;
		this.fuelHere = fuelHere;
		this.vision = vision;
	}	
}
