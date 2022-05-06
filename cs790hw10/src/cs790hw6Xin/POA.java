/**
 * @title:		POA
 * @use:		POA (plan of action) object
 * @author:		carpe227@uwm.edu
 * @author:     xxqui@uwm.edu
 * @author:     schro626@uwm.edu
 * @date:       12.15.2021
 * @version:    1.0
*/
package cs790hw6Xin;

/**
 * POA (plan of action) object
 * All team members worked together on every component of this project.
 */
public class POA {
	SAS.Direction intendedDirection;  // direction in which the walker should move
	int fuel;  // current fuel
	boolean getFuel;  // whether to get fuel
	
	/**
	 * Constructor
	 * @param intendedDirection
	 */
	public POA(SAS.Direction intendedDirection, int fuel, boolean getFuel) {
		this.intendedDirection = intendedDirection;
		this.fuel = fuel;
		this.getFuel = getFuel;
	}
}
