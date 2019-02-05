package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import regex_to_fa.Regex_to_FA;
import toolbox.Finite_Automata;
import toolbox.State;

public class Converter {

	final static String REGEX_PANEL = "Convert Regex to FA";
	final static String FA_PANEL = "Convert FA to Regex";
	final static int extraWindowWidth = 100;

	private JPanel regexCard;
	private JPanel faCard;

	private JLabel lblRegex;
	private JButton btnConvert;
	private JTextField txtField;
	private RegexPanel2 faDiagramPanel;

	private boolean convertPressed = false;

	public static void main(String[] args) {
		createAndShowGUI();
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Converter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		Converter converter = new Converter();
		converter.addComponentsToPane(frame.getContentPane());

		// Display the window.
		frame.pack();

		frame.setSize(1000, 800);
		frame.setVisible(true);
	}

	public void addComponentsToPane(Container pane) {
		JTabbedPane tabbedPane = new JTabbedPane();

		// Create the "cards".
		regexCard = new JPanel() {
			// Make the panel wider than it really needs, so
			// the window's wide enough for the tabs to stay
			// in one row.
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width += extraWindowWidth;
				return size;
			}
		};

		faCard = new JPanel();

		setUpRegexPanel();
		setUpFAPanel();

		tabbedPane.addTab(REGEX_PANEL, regexCard);
		tabbedPane.addTab(FA_PANEL, faCard);

		pane.add(tabbedPane, BorderLayout.CENTER);
	}

	private void setUpRegexPanel() {
		regexCard.setLayout(new BorderLayout());

		JPanel panel = new JPanel(new FlowLayout());

		lblRegex = new JLabel("Enter regex:");
		txtField = new JTextField();
		txtField.setPreferredSize(new Dimension(600, 20));
		btnConvert = new JButton("Convert");
		btnConvert.addActionListener(new ConvertAction());

		panel.add(lblRegex);
		panel.add(txtField);
		panel.add(btnConvert);

		regexCard.add(panel, BorderLayout.NORTH);

		faDiagramPanel = new RegexPanel2();

		faDiagramPanel.repaint();
		regexCard.add(faDiagramPanel, BorderLayout.CENTER);
	}

	private void setUpFAPanel() {
		faCard.add(new JTextField("TextField", 20));

	}

	private class ConvertAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnConvert) {
				String regex = txtField.getText();
				faDiagramPanel.convert(regex);

				faDiagramPanel.repaint();
				regexCard.repaint();
			}
		}

	}

}

class RegexPanel2 extends JPanel {

//	private String regex = "";
	private Finite_Automata fa = null;
	private final int circle_radius = 20;

	public RegexPanel2() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// paint the fa

		if (fa != null) {
			drawFA(g, fa);
		}

	}

	public void convert(String regex) {
//		this.regex = regex;

		Regex_to_FA converter = new Regex_to_FA();
		Finite_Automata fa = converter.convertToFA(regex);

		this.fa = fa;

		repaint();
	}

	private void drawFA(Graphics g, Finite_Automata fa) {
		Map<State, StateCentre> statePositions = drawStates(g, fa.getStates());
		System.out.println(statePositions.size());

		for (State state : statePositions.keySet()) {
			System.out.println("state " + state.getLabel());

			StateCentre start = statePositions.get(state);

			Map<State, ArrayList<String>> transitions = state.getTransitions();
			System.out.println("transitions: " + transitions);

			for (Entry<State, ArrayList<String>> entry : transitions.entrySet()) {

				StateCentre end = statePositions.get(entry.getKey());
				ArrayList<String> labels = entry.getValue();

				System.out.println("drawing transitions - " + labels);
				drawMultiTransitions(g, start, end, labels);
			}
		}
	}

	private Map<State, StateCentre> drawStates(Graphics g, ArrayList<State> states) {
		int centrex = 100;
		int centrey = 100;
		int spacing = 100;

		Map<State, StateCentre> statePositions = new HashMap<State, StateCentre>();

		for (State state : states) {
			drawState(g, centrex, centrey, state);

			statePositions.put(state, new StateCentre(centrex, centrey));

			centrex = centrex + spacing;
		}
		return statePositions;
	}

	private void drawState(Graphics g, int xCentre, int yCentre, State state) {
		g.drawOval(xCentre - circle_radius, yCentre - circle_radius, 2 * circle_radius, 2 * circle_radius);
		g.drawString(state.getLabel(), xCentre - 3, yCentre + 3);

		if (state.isFinalState()) {
			int radius = circle_radius - 2;
			g.drawOval(xCentre - radius, yCentre - radius, 2 * radius, 2 * radius);
		} else if (state.isInitialState()) {
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

	private void drawMultiTransitions(Graphics g, StateCentre start, StateCentre end,
			ArrayList<String> labels) {

		int curve = -1;

		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			//
			// if (i == 0){
			// curve = 0;
			// }

			drawTransition(g, start, end, curve, label);

		}

	}

	private void drawTransition(Graphics g, StateCentre start, StateCentre end, int curve,
			String label) {

		double startx = start.getXPos();
		double starty = start.getYPos() - circle_radius -5;
//		g.drawString("•", (int) startx, (int) starty);
		
		double endx = end.getXPos();
		double endy = end.getYPos() - circle_radius - 5;
//		g.drawString("•", (int) endx, (int) endy);
		
		// ********
		// need to fix the arrows
		// for some reason its bending
		
		// ********

//		System.out.println("drawing transition - " + label);
		double midpoint = (startx + endx) / 2;
		
//		g.drawString("m", (int) midpoint, (int) starty);
		double controlx = midpoint;
		
		double controly = starty - 50*1;	// *************** previously set to the value of midpoint
		
		System.out.println("midpoint: " + midpoint);
//		g.drawString("c", (int) controlx, (int) controly);

		// if (curve < 0) { 
		// // arrow needs to go below
		// controly = controly / 3;
		// }

		// g.drawString("•", (int) controlx, (int) controly);
		Point2D control = new Point2D.Double(controlx, controly);

		Point2D endPosArrow = new Point2D.Double(endx, endy);
		QuadCurve2D arrow = new QuadCurve2D.Float();
		arrow.setCurve(startx, starty, controlx, controly, endx, endy);

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

class StateCentre{
	
	private int xpos;
	private int ypos;
	
	public StateCentre(int xpos, int ypos){
		this.xpos = xpos;
		this.ypos = ypos;
	}
	
	public int getXPos(){
		return xpos;
	}
	
	public int getYPos(){
		return ypos;
	}
	
}
