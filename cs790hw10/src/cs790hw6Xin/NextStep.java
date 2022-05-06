/**
 * @title:		NextStep
 * @use:		Object class that stores next direction and current fuel amount 
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/
package cs790hw6Xin;

/**
 * NextStep object, for instructions to be used by RandomWalk (i.e. the next step of the random walk)
 * All team members worked together on every component of this project.
 */
public class NextStep {
	SAS.Direction direction;  // direction in which the walker will move
	int fuel;  // current fuel
	
	/**
	 * Constructor
	 * @param intendedDirection
	 * @param fuel amount
	 */
	public NextStep(SAS.Direction direction, int fuel) {
		this.direction = direction;
		this.fuel = fuel;
	}
}
