package view;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import toolbox.FA_Dimension;
import toolbox.Finite_Automaton;
import toolbox.State;

/**
 * Class to draw the finite automata to the screen. Based on and inspired by the
 * 'CurvedArrow' class from the software JFLAP, http://www.jflap.org/
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class FA_Drawer {

	private Graphics g;
	private final int circle_radius = 20;

	private int centrex = 200;
	private int centrey = 200;
	private int spacing = 150;

	private FA_Dimension faDimension;

	public FA_Drawer() {
	}

	public void setGraphics(Graphics g) {
		this.g = g;
	}

	/**
	 * Sets the centre position of the first state in the FA
	 * 
	 * @param xpos
	 * @param ypos
	 */
	public void setCentre(int xpos, int ypos) {
		centrex = xpos;
		centrey = ypos;
	}

	/**
	 * Sets the spacing between states
	 * 
	 * @param spacing
	 */
	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	/**
	 * Draws the FA based on the transitions between states
	 * 
	 * @param fa
	 */
	public void drawFA(Finite_Automaton fa) {
		faDimension = new FA_Dimension();
		faDimension.setYPos(centrey);

		ArrayList<State> stateList = fa.getStates();
		Map<State, State_Centre> statePositions = drawStates(g, stateList);

		for (State start : statePositions.keySet()) {

			State_Centre startPos = statePositions.get(start);
			Map<State, ArrayList<String>> transitions = start.getTransitions();

			for (Entry<State, ArrayList<String>> entry : transitions.entrySet()) {

				State end = entry.getKey();
				State_Centre endPos = statePositions.get(end);
				ArrayList<String> labels = entry.getValue();

				int startIndex = stateList.indexOf(start);
				int endIndex = stateList.indexOf(end);

				boolean nextTo = false;
				boolean backwardsTransition = false;

				if (startIndex > endIndex) {
					backwardsTransition = true;
				}
				if (startIndex == endIndex - 1 || startIndex == endIndex + 1) {
					nextTo = true;
				}

				drawTransitions(startPos, endPos, labels, nextTo, backwardsTransition);
			}
		}
	}

	/**
	 * Takes the list of states and draws them to the screen
	 * 
	 * @param g
	 *            - graphics
	 * @param states
	 *            - list of states to draw
	 * @return the map of the state object and its centre position on the screen
	 */
	private Map<State, State_Centre> drawStates(Graphics g, ArrayList<State> states) {
		Map<State, State_Centre> statePositions = new HashMap<State, State_Centre>();

		for (State state : states) {
			drawState(g, centrex, centrey, state);
			statePositions.put(state, new State_Centre(centrex, centrey));
			centrex = centrex + spacing;

			faDimension.setLength(centrex + circle_radius);
		}
		return statePositions;
	}

	/**
	 * Draws each individual state on the screen with its label
	 * 
	 * @param g
	 * @param xCentre
	 * @param yCentre
	 * @param state
	 */
	private void drawState(Graphics g, int xCentre, int yCentre, State state) {
		String label = state.getLabel();

		int width = g.getFontMetrics().stringWidth(label);

		int fontsize;
		int xpos;
		int ypos;

		if (label.length() >= 5) {
			fontsize = 10;

			xpos = xCentre - 3 * (width / 10);
			ypos = yCentre + 3;

			g.drawOval(xCentre - circle_radius, yCentre - circle_radius, 2 * circle_radius, 2 * circle_radius);

		} else {
			fontsize = 15;

			xpos = xCentre - (width / 2);
			ypos = yCentre + 3;

			g.drawOval(xCentre - circle_radius, yCentre - circle_radius, 2 * circle_radius, 2 * circle_radius);
		}

		g.setFont(new Font("TimesRoman", Font.PLAIN, fontsize));

		g.drawString(label, xpos, ypos);

		if (state.isFinalState()) {
			int radius = circle_radius - 2;
			g.drawOval(xCentre - radius, yCentre - radius, 2 * radius, 2 * radius);
		}
		if (state.isInitialState()) {
			drawInitialArrow(g, xCentre, yCentre);
		}
		g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
	}

	/**
	 * Draws an arrow pointing to the initial state of the FA
	 * 
	 * @param g
	 * @param xCentre
	 * @param yCentre
	 */
	private void drawInitialArrow(Graphics g, int xCentre, int yCentre) {
		int endx, endy;
		int startx = xCentre - 25;
		int starty = yCentre;

		g.drawLine(xCentre - 60, yCentre, xCentre - 25, yCentre);

		double angle = 300;
		double arrowAngle = Math.PI / 10;
		double arrowLength = 15;

		angle += arrowAngle;
		endx = (((int) (Math.sin(angle) * arrowLength)) + (int) startx);
		endy = ((int) (Math.cos(angle) * arrowLength)) + (int) starty;
		g.drawLine((int) startx, (int) starty, endx, endy);
		angle -= 2 * arrowAngle;
		endx = ((int) (Math.sin(angle) * arrowLength)) + (int) startx;
		endy = ((int) (Math.cos(angle) * arrowLength)) + (int) starty;
		g.drawLine((int) startx, (int) starty, endx, endy);
	}

	/**
	 * Takes the list of transitions between two states and draws them to the
	 * screen. Will have different height and curve depending on the number of
	 * transitions
	 * 
	 * @param start
	 *            - state the transition leaves
	 * @param end
	 *            - state the transition points to
	 * @param labels
	 *            - labels on the transitions
	 * @param nextTo
	 *            - boolean stating whether the states are next to each other
	 * @param backwardsTransition
	 *            - boolean stating whether the transition goes to a previously
	 *            seen state
	 */
	private void drawTransitions(State_Centre start, State_Centre end, ArrayList<String> labels, boolean nextTo,
			boolean backwardsTransition) {
		int numLabels = labels.size();
		int height = 20;
		int curve = -1;

		if (nextTo) {
			boolean curveUp = true;

			if (backwardsTransition) {
				curve = 1;
				curveUp = false;
			}

			if (numLabels == 1 && backwardsTransition == false) {
				drawStraightTransition(g, start, end, labels.get(0));
			} else {
				// multiple labels
				for (int i = 0; i < numLabels; i++) {
					if (i == 0) {
						drawSlightCurveTransition(g, start, end, labels.get(i), curveUp);
					} else {

						drawTransition(g, start, end, curve * height, labels.get(i));
						height += 30;
					}
				}
			}

		} else {
			height = 40;

			int startX = start.getXPos();
			int endX = end.getXPos();
			int dist;

			if (backwardsTransition) {
				curve = 1; // curve down the page
				dist = startX - endX;
			} else {
				dist = endX - startX;
			}

			height += ((dist / 100) * 30);

			for (int i = 0; i < numLabels; i++) {
				drawTransition(g, start, end, curve * height, labels.get(i));
				height += 40;
			}
		}
	}

	/**
	 * Draws a slightly curved transition between two states. Used if states are
	 * next to each other
	 * 
	 * @param g
	 * @param start
	 *            - state transition leaves from
	 * @param end
	 *            - state transition points to
	 * @param label
	 *            - label on the transition
	 * @param curveUp
	 *            - whether it should curve up the page (forwards arrow) or down
	 *            the page (backwards arrow)
	 */
	private void drawSlightCurveTransition(Graphics g, State_Centre start, State_Centre end, String label,
			boolean curveUp) {

		double startx;
		double starty;
		double endx;
		double endy;

		if (start.getXPos() > end.getXPos()) {
			// backwards arrow
			startx = start.getXPos() - circle_radius - 2;
			starty = start.getYPos();

			endx = end.getXPos() + circle_radius + 2;
			endy = end.getYPos();
		} else {

			startx = start.getXPos() + circle_radius + 2;
			starty = start.getYPos();

			endx = end.getXPos() - circle_radius - 2;
			endy = end.getYPos();
		}

		double midpoint = (startx + endx) / 2;
		double controlx = midpoint;

		double controly;

		if (curveUp == true) {
			starty -= 10;
			endy -= 10;
			controly = starty - 10;
		} else {
			starty += 10;
			endy += 10;
			controly = starty + 10;
		}

		Point2D control = new Point2D.Double(controlx, controly);

		createArrow(g, startx, starty, endx, endy, control, label);
	}

	/**
	 * Draws a straight transition between states
	 * 
	 * @param g
	 * @param start
	 *            - state transition leaves from
	 * @param end
	 *            - state transition points to
	 * @param label
	 *            - label on the transition
	 */
	private void drawStraightTransition(Graphics g, State_Centre start, State_Centre end, String label) {
		double startx;
		double starty;
		double endx;
		double endy;

		if (start.getXPos() > end.getXPos()) {
			// backwards arrow
			startx = start.getXPos() - circle_radius - 2;
			starty = start.getYPos();

			endx = end.getXPos() + circle_radius + 2;
			endy = end.getYPos();
		} else {
			startx = start.getXPos() + circle_radius + 2;
			starty = start.getYPos();

			endx = end.getXPos() - circle_radius - 2;
			endy = end.getYPos();
		}

		double midpoint = (startx + endx) / 2;
		double controlx = midpoint;
		double controly = starty;

		Point2D control = new Point2D.Double(controlx, controly);
		createArrow(g, startx, starty, endx, endy, control, label);
	}

	/**
	 * Determines the positioning of the transition on the screen
	 * 
	 * @param g
	 * @param start
	 *            - state the transition leaves from
	 * @param end
	 *            - state the transition points to
	 * @param curve
	 *            - whether the transition goes up or down the page
	 * @param label
	 *            - label on the transition
	 */
	private void drawTransition(Graphics g, State_Centre start, State_Centre end, int curve, String label) {
		double startx = start.getXPos();
		double starty = start.getYPos();
		double endx = end.getXPos();
		double endy = end.getYPos();

		if (start == end) {
			startx -= 15;
			endx += 15;
		}

		if (curve > 0) {
			// means the curve is going down the page
			starty += circle_radius + 5;
			endy += circle_radius + 5;

			int h = (int) ((starty + starty + curve - 5) / 2);
			faDimension.setBackTransitionHeight(h);

		} else {
			starty -= (circle_radius + 5);
			endy -= (circle_radius + 5);

			int h = (int) ((starty + starty + curve - 5) / 2);
			faDimension.setTransitionHeight(h);

		}

		double midpoint = (startx + endx) / 2;
		double controlx = midpoint;
		double controly = starty + curve;

		Point2D control = new Point2D.Double(controlx, controly);
		createArrow(g, startx, starty, endx, endy, control, label);
	}

	/**
	 * Draws the transition to the screen
	 * 
	 * @param g
	 * @param startx
	 *            - start x position
	 * @param starty
	 *            - start y position
	 * @param endx
	 *            - end x position
	 * @param endy
	 *            - end y position
	 * @param control
	 *            - control point of the curve. Determines the curvey-ness of
	 *            the transition and is the point the arrow head faces away from
	 * @param label
	 *            - the label on the transition
	 */
	private void createArrow(Graphics g, double startx, double starty, double endx, double endy, Point2D control,
			String label) {
		Point2D endPosArrow = new Point2D.Double(endx, endy);
		QuadCurve2D arrow = new QuadCurve2D.Float();
		arrow.setCurve(startx, starty, control.getX(), control.getY(), endx, endy);

		Graphics2D g2d = (Graphics2D) g;

		g2d.draw(arrow);
		drawArrow(g2d, endPosArrow, control);
		drawLabel(g, arrow, label);
	}

	/**
	 * Draws the arrow-head lines to the screen
	 * 
	 * @param g
	 * @param head
	 *            - position of the head of the arrow
	 * @param control
	 *            - the control point of the curve. Arrow head faces away from
	 *            this
	 */
	private void drawArrow(Graphics g, Point2D head, Point2D control) {
		int endX, endY;
		double angle = Math.atan2((double) (control.getX() - head.getX()), (double) (control.getY() - head.getY()));

		double arrowAngle = Math.PI / 10;
		double arrowLength = 15;

		angle += arrowAngle;
		endX = (((int) (Math.sin(angle) * arrowLength)) + (int) head.getX());
		endY = ((int) (Math.cos(angle) * arrowLength)) + (int) head.getY();
		g.drawLine((int) head.getX(), (int) head.getY(), endX, endY);
		angle -= 2 * arrowAngle;
		endX = ((int) (Math.sin(angle) * arrowLength)) + (int) head.getX();
		endY = ((int) (Math.cos(angle) * arrowLength)) + (int) head.getY();
		g.drawLine((int) head.getX(), (int) head.getY(), endX, endY);
	}

	/**
	 * Draws the label on the transition arrow
	 * @param g
	 * @param arrow - the transition to draw the label on
	 * @param label - the label on the transition
	 */
	private void drawLabel(Graphics g, QuadCurve2D arrow, String label) {
		int width = g.getFontMetrics().stringWidth(label);

		int x = (int) arrow.getCtrlX();
		int y = (int) (arrow.getY1() + arrow.getCtrlY() - 5) / 2;

		int pos = x - (width / 2);

		g.drawString(label, pos, (int) y);
	}

	/**
	 *
	 * @return the dimensions of the FA - the height, width, etc
	 */
	public FA_Dimension getDimension() {
		return faDimension;
	}

}
