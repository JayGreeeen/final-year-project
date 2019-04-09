package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import regex_to_fa.Regex_to_FA;
import toolbox.FA_Dimension;
import toolbox.Finite_Automaton;
import toolbox.Tree_Node;
import toolbox.Tree_Node.Leaf_Node;
import toolbox.Tree_Node.Star_Node;
import view.FA_Drawer;

/**
 * The panel which handles the display of the FA on the Regex to FA page. Takes
 * in regex and draws the stages of conversion into an FA
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class Regex_Converter_Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Finite_Automaton fa = null;
	private Regex_to_FA converter;

	private Tree_Node root;
	private Tree_Node currentParent;

	private Finite_Automaton left;
	private Finite_Automaton right;

	private FA_Drawer drawer;

	private JButton btnNext;

	private int frameWidth = 2000;
	private int frameHeight = 500;

	private int centrex = 100;
	private int leftCentrey = 150;
	private int rightCentrey = 300;
	private int faCentrey = 300;

	private Map<Finite_Automaton, Integer[]> faMap;

	private boolean leftAdded = true;
	private boolean rightAdded = true;
	private boolean faAdded = true;

	private int currentLowestPoint = 0;

	public Regex_Converter_Panel() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		converter = new Regex_to_FA(this);

	}

	/**
	 * Paint method - draws the automata to the screen. This is called
	 * recursively, so has checks to keep the number of calls to a minimum
	 */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawer = new FA_Drawer();
		drawer.setGraphics(g);
		drawer.setCentre(centrex, leftCentrey);

		if (left != null) {
			redraw();

			if (leftAdded == false) {
				paintFA();
			}
		} else if (fa != null) {
			redraw();

			if (faAdded == false) {
				paintFA();
			}
		}
	}

	/**
	 * Takes the automaton of the left and right child nodes and the automaton
	 * for how they are combined using the parent operator, and draws them to
	 * the screen. Checks ensure that the automata do not overlap on the screen.
	 * If they do, they are repositioned and redrawn.
	 */
	private void paintFA() {
		if (left != null) {

			if (currentLowestPoint > 0) {
				leftCentrey = currentLowestPoint;
			}

			// set the centre position of where to start drawing the automaton
			drawer.setCentre(centrex, leftCentrey);
			drawer.drawFA(left);

			// used to check if there is any overlap with another automaton
			FA_Dimension leftDimension = drawer.getDimension();
			int leftForwardArrowPos = leftDimension.getTransitionHeight();
			int leftBackArrowPos = leftDimension.getBackTransitionHeight();

			// checks that the highest forward arrow does not go off the screen.
			// Otherwise it is repositioned.
			if (leftForwardArrowPos < 0) {
				int difference = 0 - leftForwardArrowPos;
				leftCentrey += difference + 50;
				leftForwardArrowPos += difference + 50;
			}

			// checks to see if it overlaps with anything previously drawn on
			// the screen.
			if (currentLowestPoint != 0 && leftForwardArrowPos < currentLowestPoint) {
				// left overlaps previous

				int forwardHeight = leftCentrey - leftForwardArrowPos;
				int backHeight = leftBackArrowPos - leftCentrey;

				leftCentrey = currentLowestPoint + forwardHeight + 50;
				leftBackArrowPos = leftCentrey + backHeight;
			}

			// lowest drawn point on the screen - nothing should overlap this
			currentLowestPoint = leftBackArrowPos;

			// if the current parent is a star node there will be no right child
			if (right != null) {
				rightCentrey = leftCentrey + 150;
				drawer.setCentre(centrex, rightCentrey);
				drawer.drawFA(right);

				// used to check for overlap
				FA_Dimension rightDimension = drawer.getDimension();
				int rightForwardArrowPos = rightDimension.getTransitionHeight();
				int rightBackArrowPos = rightDimension.getBackTransitionHeight();

				// checks to see if the right child automaton overlaps the left
				// child
				if (leftBackArrowPos >= rightForwardArrowPos) {
					int forwardHeight = rightCentrey - rightForwardArrowPos;
					int backHeight = rightBackArrowPos - rightCentrey;

					rightCentrey = leftBackArrowPos + forwardHeight + 50;
					rightBackArrowPos = rightCentrey + backHeight + 50;
				}

				faCentrey = rightCentrey + 150;
				drawer.setCentre(centrex, faCentrey);
				drawer.drawFA(fa);

				// used to check for overlap
				FA_Dimension faDimension = drawer.getDimension();
				int faForwardArrowPos = faDimension.getTransitionHeight();
				int faBackArrowPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();

				// checks to see if this overlaps the previously drawn automaton
				if (rightBackArrowPos >= faForwardArrowPos) {
					int forwardHeight = faCentrey - faForwardArrowPos;
					int backHeight = faBackArrowPos - faCentrey;

					faCentrey = rightBackArrowPos + forwardHeight + 50;
					faBackArrowPos = faCentrey + backHeight;
				}

				// checks to see if the automaton goes off the bottom of the
				// panel - resizes if necessary
				if (faBackArrowPos >= frameHeight - 100) {
					setFrameSize(frameWidth, faBackArrowPos + 200);
				}

				// checks to see if the automaton goes off the edge of the panel
				// - resizes if necessary
				if (faLength >= frameWidth - 100) {
					setFrameSize(faLength + 200, frameHeight);
				}
				currentLowestPoint = faBackArrowPos;

			} else {
				// no right child
				faCentrey = leftCentrey + 150;
				drawer.setCentre(centrex, faCentrey);
				drawer.drawFA(fa);

				FA_Dimension faDimension = drawer.getDimension();
				int faForwardArrowPos = faDimension.getTransitionHeight();
				int faBackArrowPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();

				// check for overlap
				if (leftBackArrowPos >= faForwardArrowPos) {
					int forwardHeight = faCentrey - faForwardArrowPos;
					int backHeight = faBackArrowPos - faCentrey;

					faCentrey = leftBackArrowPos + forwardHeight + 50;
					faBackArrowPos = faCentrey + backHeight;
				}

				// checks to see if the automaton goes off the bottom of the
				// panel
				if (faBackArrowPos >= frameHeight - 100) {
					setFrameSize(frameWidth, faBackArrowPos + 200);
				}

				// checks to see if the automaton goes off the edge of the panel
				if (faLength + 100 >= frameWidth) {
					setFrameSize(faLength + 200, frameHeight);
				}
				currentLowestPoint = faBackArrowPos;
			}
		} else {
			if (fa != null) {
				drawer.drawFA(fa);

				FA_Dimension faDimension = drawer.getDimension();

				int faForwardArrowPos = faDimension.getTransitionHeight();
				int faBackArrowPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();

				if (faForwardArrowPos < 0) {
					int difference = 0 - faForwardArrowPos;
					leftCentrey += difference + 20;
				}

				if (faBackArrowPos >= frameHeight - 100) {
					setFrameSize(frameWidth, faBackArrowPos + 100);
				}

				if (faLength + 100 >= frameWidth) {
					setFrameSize(faLength + 200, frameHeight);
				}

				if (currentLowestPoint != 0 && faForwardArrowPos < currentLowestPoint) {
					int difference;
					if (faForwardArrowPos < 0) {
						difference = currentLowestPoint - (0 - faForwardArrowPos);
					} else {
						difference = currentLowestPoint - faForwardArrowPos;
					}

					faCentrey += difference + 50;
					faBackArrowPos += difference + 50;
				}
				currentLowestPoint = faBackArrowPos;
			}
		}

		// the map keeps track of the positions of all drawn automata, allowing
		// them to all be redrawn and remain on the screen each time the frame
		// is repainted
		if (leftAdded == false) {
			Integer[] posValues = { centrex, leftCentrey };
			faMap.put(left.copy(), posValues);
			leftAdded = true;
		}

		if (rightAdded == false) {
			Integer[] posValues = { centrex, rightCentrey };
			faMap.put(right.copy(), posValues);
			rightAdded = true;
		}

		if (faAdded == false) {
			Integer[] posValues = { centrex, faCentrey };
			faMap.put(fa.copy(), posValues);
			faAdded = true;
		}

		repaint();
	}

	/**
	 * Redraws all automata to the screen at their set positions
	 */

	private void redraw() {
		Finite_Automaton fa;
		Integer[] values;

		for (Entry<Finite_Automaton, Integer[]> entry : faMap.entrySet()) {
			fa = (Finite_Automaton) entry.getKey();
			values = entry.getValue();

			drawer.setCentre(values[0], values[1]);
			drawer.drawFA(fa);
		}

	}

	/**
	 * Resizes the frame
	 * 
	 * @param width
	 *            of the frame
	 * @param height
	 *            of the frame
	 */

	private void setFrameSize(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		frameWidth = width;
		frameHeight = height;
	}

	/**
	 * Takes in the initial regex input, validates it and builds the binary tree
	 * 
	 * @param regex
	 */

	public void convert(String regex) {
		leftCentrey = 150;
		rightCentrey = 300;
		faCentrey = 450;
		currentLowestPoint = 0;

		faMap = new HashMap<>();
		String errorMessage = converter.validate(regex);

		if (errorMessage == "") {
			root = converter.convertToTree(regex);
			// Print_Tree.printSideways(root);
			// Print_Tree.print(root);

			currentParent = getLeftmostParent(root);

			if (currentParent.getParent() == null) {
				btnNext.setEnabled(false);
			} else {
				btnNext.setEnabled(true);
			}

			convertToFa(currentParent);
			repaint();

		} else {
			String message = "Please enter a valid regex. " + errorMessage;
			JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Creates the automaton for a node in the tree
	 * 
	 * @param node
	 */
	private void convertToFa(Tree_Node node) {
		if (node instanceof Leaf_Node) {
			this.left = null;
			this.right = null;
			this.fa = converter.generateFA(node).copy();

		} else {
			Tree_Node left = node.getLeftChild();
			this.left = converter.generateFA(left).copy();
			leftAdded = false;

			if (node instanceof Star_Node) {
				this.right = null;

			} else {
				Tree_Node right = node.getRightChild();
				this.right = converter.generateFA(right).copy();
				rightAdded = false;
			}
			this.fa = converter.generateFA(node).copy();
			faAdded = false;
		}
	}

	/**
	 * Gets the leftmost parent node in the tree to convert. This is a node
	 * containing an operator, •, *, or |
	 * 
	 * @param node
	 *            in tree to recursively search
	 * @return the leftmost parent node
	 */

	private Tree_Node getLeftmostParent(Tree_Node node) {
		if (node instanceof Leaf_Node) {
			return node;

		} else {
			Tree_Node leaf = getLeftmostLeaf(node);
			Tree_Node currentParent = leaf.getParent();

			if (currentParent instanceof Star_Node) {
				return currentParent;

			} else {
				Tree_Node right = currentParent.getRightChild();
				if (right instanceof Leaf_Node) {
					return currentParent;

				} else {
					return getLeftmostParent(right);
				}
			}
		}
	}

	/**
	 * Returns the leftmost leaf in the tree. This is a node containing a symbol
	 * of the input alphabet
	 * 
	 * @param node
	 *            in tree to recursively search
	 * @return the leftmost leaf node
	 */
	private Tree_Node getLeftmostLeaf(Tree_Node node) {
		if (node instanceof Leaf_Node) {
			return node;
		}
		return getLeftmostLeaf(node.getLeftChild());
	}

	/**
	 * Carries out the next conversion step. Activated by pushing the 'next'
	 * button on the interface.
	 */
	public void drawNext() {
		if (currentParent.getParent() != null) {
			Tree_Node parent = currentParent.getParent();

			currentParent = parent;
			if (currentParent.getParent() == null) {
				// no more steps
				btnNext.setEnabled(false);
			}

			convertToFa(currentParent);
			repaint();
		} else {
			btnNext.setEnabled(false);
		}
	}

	/**
	 * Sets the 'next' button object, links it to the converter class
	 * 
	 * @param next
	 *            button
	 */
	public void setNextButton(JButton next) {
		this.btnNext = next;
	}

	/**
	 * Removes all user input and drawings from the page
	 */
	public void resetPage() {
		left = null;
		fa = null;

		setFrameSize(2000, 500);
	}

}
