package frontend;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import toolbox.Finite_Automata;
import toolbox.State;

public class FA_Drawer {

	private final int circle_radius = 20;

	public void drawFA(Graphics g, Finite_Automata fa) {
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

				if (startIndex > endIndex){
					backwardsTransition = true;
				}
				if (startIndex == endIndex - 1){
					nextTo = true;
				}
				
				drawTransitions(g, startPos, endPos, labels, nextTo, backwardsTransition);
			}
		}
	}

	private void drawTransitions(Graphics g, State_Centre start, State_Centre end, ArrayList<String> labels,
			boolean nextTo, boolean backwardsTransition) {
		int numLabels = labels.size();
		int height = 50;
		int curve = -1;

		if (nextTo) {

			if (backwardsTransition) {

				// want it to curve down and under

				// need to consider that there may be transitions in the
				// forwards direction
				// dont want the arrows to overlap

			}

			if (numLabels == 1) {

				drawStraightTransition(g, start, end, labels.get(0));
			} else if (numLabels == 2) {

				drawSlightCurveTransition(g, start, end, labels.get(0), true);
				drawSlightCurveTransition(g, start, end, labels.get(1), false);
			} else {
				// multiple labels
				boolean curveUp = true;

				for (int i = 0; i < numLabels; i++) {
					if (i == 0 || i == 1) {

						drawSlightCurveTransition(g, start, end, labels.get(i), curveUp);
						curveUp = false;
					} else {
						// for the rest of them, curve alternating above and
						// below with increasing height

						drawTransition(g, start, end, curve * height, labels.get(i));
						curve = -curve;

						if (i % 2 != 0)
							height += 40;
					}
				}
			}

		} else {
			height = 50;
			
			// find out how far apart they are
			// use this as a factor for the distance
			// e.g. height = 50 + distance x20
			// 1 apart = next to 
			// so if theyre 2 apart - 50 + (2 x 20) = 70
			// if theyre 3 apart - 50 + (3 x 20) = 80
			// might need a smaller value than 20
			
			int startX = start.getXPos();
			int endX = end.getXPos();
			
			
			int dist = endX - startX;
//			System.out.println("distance between states: " + dist);
//			height += ((dist / 100) * 15);
			height += ((dist / 100) * 20);
//			System.out.println("Height: " + height);
			
			if (backwardsTransition) {
				
				// want it to curve down and under
				
				// need to consider that there may be transitions in the
				// forwards direction
				// dont want the arrows to overlap
				
				dist = startX - endX;
				height += (dist/ 100)*15;
				
				height = -height;
			}
			

			// states are not next to each other alternate above and below
			// increase height after every 2 arrows
			for (int i = 0; i < numLabels; i++) {
				drawTransition(g, start, end, curve * height, labels.get(i));
				curve = -curve;

				if (i % 2 != 0)
					height += 40;
			}
		}
	}

	private Map<State, State_Centre> drawStates(Graphics g, ArrayList<State> states) {
		int centrex = 100;
		int centrey = 100;
		int spacing = 100;

		Map<State, State_Centre> statePositions = new HashMap<State, State_Centre>();

		for (State state : states) {
			drawState(g, centrex, centrey, state);
			statePositions.put(state, new State_Centre(centrex, centrey));
			centrex = centrex + spacing;
		}
		return statePositions;
	}

	private void drawState(Graphics g, int xCentre, int yCentre, State state) {
		// TODO
		// check the length of the string - depending on the length, changes the
		// width of the circle
		// the wider it gets, we would want it to be slightly higher as well
		// ***************************

		g.drawOval(xCentre - circle_radius, yCentre - circle_radius, 2 * circle_radius, 2 * circle_radius);
		g.drawString(state.getLabel(), xCentre - 3, yCentre + 3);

		if (state.isFinalState()) {
			int radius = circle_radius - 2;
			g.drawOval(xCentre - radius, yCentre - radius, 2 * radius, 2 * radius);
		}
		if (state.isInitialState()) {
			drawInitialArrow(g, xCentre, yCentre, state.getLabel());
		}
	}

	private void drawInitialArrow(Graphics g, int xCentre, int yCentre, String label) {
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

	private void drawSlightCurveTransition(Graphics g, State_Centre start, State_Centre end, String label,
			boolean curveUp) {
		double startx = start.getXPos() + circle_radius + 2;
		double starty = start.getYPos();

		double endx = end.getXPos() - circle_radius - 2;
		double endy = end.getYPos();

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

	private void drawStraightTransition(Graphics g, State_Centre start, State_Centre end, String label) {
		double startx = start.getXPos() + circle_radius + 2;
		double starty = start.getYPos();

		double endx = end.getXPos() - circle_radius - 2;
		double endy = end.getYPos();

		double midpoint = (startx + endx) / 2;
		double controlx = midpoint;
		double controly = starty;
		// *************** previously set to the value of midpoint

		Point2D control = new Point2D.Double(controlx, controly);
		createArrow(g, startx, starty, endx, endy, control, label);
	}

	private void drawTransition(Graphics g, State_Centre start, State_Centre end, int curve, String label) {
		double startx = start.getXPos();
		double starty = start.getYPos();
		double endx = end.getXPos();
		double endy = end.getYPos();

		if (start == end) {
			startx -= 10;
			endx += 10;
			curve = -50;
		}

		if (curve > 0) {
			// means the curve is going down the page
			starty += circle_radius + 5;
			endy += circle_radius + 5;
		} else {
			starty -= (circle_radius + 5);
			endy -= (circle_radius + 5);
		}

		double midpoint = (startx + endx) / 2;
		double controlx = midpoint;
		double controly = starty + curve;

		Point2D control = new Point2D.Double(controlx, controly);
		createArrow(g, startx, starty, endx, endy, control, label);
	}

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

	private void drawLabel(Graphics g, QuadCurve2D arrow, String label) {
		int x = (int) arrow.getCtrlX();
		int y = (int) (arrow.getY1() + arrow.getCtrlY() - 5) / 2;
		g.drawString(label, x, (int) y);
	}

}
