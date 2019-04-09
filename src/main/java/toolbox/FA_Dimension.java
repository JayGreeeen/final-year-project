package toolbox;

/**
 * Keeps track of the dimensions of an automaton such as the highest transition,
 * lowest transition, and width on the page. Useful for checking if automata
 * overlap.
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class FA_Dimension {

	private int transitionHeight = 0;
	private int backTransitionHeight = 0;
	private int length = 0;
	private int ypos;
	private final int defaultSpacing = 30;

	/**
	 * Set the centre y position for the location of the automaton
	 * 
	 * @param y
	 *            position
	 */
	public void setYPos(int y) {
		ypos = y;
	}

	/**
	 * Sets the value of highest forwards transition. Transitions go up the
	 * page, so wont the smallest value.
	 * 
	 * @param height
	 */
	public void setTransitionHeight(int height) {
		if (transitionHeight == 0) {
			transitionHeight = height;
		} else if (height < transitionHeight) {
			transitionHeight = height;
		}
	}

	/**
	 * Sets the value of the lowest backwards transition. Transitions go down
	 * the page so want the largest value.
	 * 
	 * @param height
	 */
	public void setBackTransitionHeight(int height) {
		if (height > backTransitionHeight) {
			backTransitionHeight = height;
		}
	}

	/**
	 * Sets the length of the automaton
	 * 
	 * @param length
	 */
	public void setLength(int length) {
		if (length > this.length) {
			this.length = length;
		}
	}

	/**
	 * @return the highest point of the automaton - smallest y value
	 */
	public int getTransitionHeight() {
		if (transitionHeight == 0) {
			return ypos - defaultSpacing;
		}
		return transitionHeight;
	}

	/**
	 * @return the lowest point of the automaton - largest y value
	 */
	public int getBackTransitionHeight() {
		if (backTransitionHeight == 0) {
			return ypos + defaultSpacing;
		}
		return backTransitionHeight;
	}

	/**
	 * @return the length of the automaton
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the centre position for the automaton
	 */
	public int getYPos() {
		return ypos;
	}

}
