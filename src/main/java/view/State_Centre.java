package view;

/**
 * Holds the x and y position for a state on the frame
 * 
 * @author Jaydene Green-Stevens
 */
public class State_Centre {

	private int xpos;
	private int ypos;

	public State_Centre(int xpos, int ypos) {
		this.xpos = xpos;
		this.ypos = ypos;
	}

	/**
	 * @return the x position of the state
	 */
	public int getXPos() {
		return xpos;
	}

	/**
	 * @return the y position of the state
	 */
	public int getYPos() {
		return ypos;
	}

}
