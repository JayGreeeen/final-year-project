package toolbox;

public class FA_Dimension {

	private int transitionHeight = 0;
	private int backTransitionHeight = 0;
	private int length = 0;
	private int ypos;
	private final int defaultSpacing = 30;

	public void setYPos(int y) {
		ypos = y;
	}

	public void setTransitionHeight(int height) {
		if (transitionHeight == 0) {
			transitionHeight = height;
		} else if (height < transitionHeight) {
			transitionHeight = height;
		}
	}

	public void setBackTransitionHeight(int height) {
		if (height > backTransitionHeight) {
			backTransitionHeight = height;
		}
	}

	public void setLength(int length) {
		if (length > this.length) {
			this.length = length;
		}
	}

	public int getTransitionHeight() {
		if (transitionHeight == 0) {
			return ypos - defaultSpacing;
		}

		return transitionHeight;
		// return -transitionHeight;
	}

	public int getBackTransitionHeight() {
		if (backTransitionHeight == 0) {
			return ypos + defaultSpacing;
		}
		return backTransitionHeight;
	}

	public int getLength() {
		// last x position of states
		return length;
	}

	public int getYPos() {
		return ypos;
	}

}
