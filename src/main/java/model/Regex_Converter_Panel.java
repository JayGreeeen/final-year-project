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
import toolbox.Finite_Automata;
import toolbox.Tree_Node;
import toolbox.Tree_Node.LeafNode;
import toolbox.Tree_Node.StarNode;
import view.FA_Drawer;

public class Regex_Converter_Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Finite_Automata fa = null;
	private Regex_to_FA converter;

	private Tree_Node root;
	private Tree_Node currentParent;

	private Finite_Automata left;
	private Finite_Automata right;

	private FA_Drawer drawer;

	private JButton btnNext;

	private int frameWidth = 2000;
	private int frameHeight = 500;

	private int centrex = 100;
	private int leftCentrey = 150;
	private int rightCentrey = 300;
	private int faCentrey = 300;

	private Map<Finite_Automata, Integer[]> faMap;

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

	private void paintFA() {
		if (left != null) {

			if (currentLowestPoint > 0) {
				// System.out.println("current lowest pos: " +
				// currentLowestPoint);
				leftCentrey = currentLowestPoint;
			}

			drawer.setCentre(centrex, leftCentrey);
			drawer.drawFA(left);
			// System.out.println("Drawing left at position: " + centrex + ", "
			// + leftCentrey);

			FA_Dimension leftDimension = drawer.getDimension();
			int leftForwardArrowPos = leftDimension.getTransitionHeight();
			int leftBackArrowPos = leftDimension.getBackTransitionHeight();

			// System.out.println("\tInitial left: forward arrow pos: " +
			// leftDimension.getTransitionHeight() + " , back arrow pos: " +
			// leftBackArrowPos);

			if (leftForwardArrowPos < 0) {
				int difference = 0 - leftForwardArrowPos;
				leftCentrey += difference + 50;
				leftForwardArrowPos += difference + 50;
				// System.out.println("left goes off the page. leftcentrey: " +
				// leftCentrey);
			}

			// System.out.println("\tCurrent lowest point: " +
			// currentLowestPoint);

			if (currentLowestPoint != 0 && leftForwardArrowPos < currentLowestPoint) {
				// left overlaps previous

				int forwardHeight = leftCentrey - leftForwardArrowPos;
				int backHeight = leftBackArrowPos - leftCentrey;

				leftCentrey = currentLowestPoint + forwardHeight + 50;
				leftBackArrowPos = leftCentrey + backHeight;
				// System.out.println("moving down. leftCentrey: " +
				// leftCentrey);
			}

			currentLowestPoint = leftBackArrowPos;

			if (right != null) {
				rightCentrey = leftCentrey + 150;
				drawer.setCentre(centrex, rightCentrey);
				drawer.drawFA(right);
				// System.out.println("Drawing right at position: " + centrex +
				// ", " + rightCentrey);

				FA_Dimension rightDimension = drawer.getDimension();
				int rightForwardArrowPos = rightDimension.getTransitionHeight();
				int rightBackArrowPos = rightDimension.getBackTransitionHeight();

				if (leftBackArrowPos >= rightForwardArrowPos) {
					// System.out.println("Right overlaps left ");
					int forwardHeight = rightCentrey - rightForwardArrowPos;
					int backHeight = rightBackArrowPos - rightCentrey;

					rightCentrey = leftBackArrowPos + forwardHeight + 50;
					rightBackArrowPos = rightCentrey + backHeight + 50;
				}

				faCentrey = rightCentrey + 150;
				drawer.setCentre(centrex, faCentrey);
				drawer.drawFA(fa);
				// System.out.println("Drawing fa at position: " + centrex + ",
				// " + faCentrey);

				FA_Dimension faDimension = drawer.getDimension();
				int faForwardArrowPos = faDimension.getTransitionHeight();
				int faBackArrowPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();

				if (rightBackArrowPos >= faForwardArrowPos) {
					// System.out.println("fa overlaps right ");

					int forwardHeight = faCentrey - faForwardArrowPos;
					int backHeight = faBackArrowPos - faCentrey;

					faCentrey = rightBackArrowPos + forwardHeight + 50;
					faBackArrowPos = faCentrey + backHeight;
				}

				// System.out.println("frame height: " + frameHeight + ". Fa
				// back arrow pos: " + faBackArrowPos);
				if (faBackArrowPos >= frameHeight - 100) {
					setFrameSize(frameWidth, faBackArrowPos + 200);
				}

				if (faLength >= frameWidth - 100) {
					setFrameSize(faLength + 200, frameHeight);
				}
				currentLowestPoint = faBackArrowPos;

			} else {
				// no right child
				faCentrey = leftCentrey + 150;
				drawer.setCentre(centrex, faCentrey);
				drawer.drawFA(fa);
				// System.out.println("Drawing fa at position: " + centrex + ","
				// + faCentrey);

				FA_Dimension faDimension = drawer.getDimension();
				int faForwardArrowPos = faDimension.getTransitionHeight();
				int faBackArrowPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();

				if (leftBackArrowPos >= faForwardArrowPos) {
					// System.out.println("fa overlaps left");
					int forwardHeight = faCentrey - faForwardArrowPos;
					int backHeight = faBackArrowPos - faCentrey;

					faCentrey = leftBackArrowPos + forwardHeight + 50;
					faBackArrowPos = faCentrey + backHeight;
				}

				// System.out.println("frame height: " + frameHeight + ". Fa
				// back arrow pos: " + faBackArrowPos);

				if (faBackArrowPos >= frameHeight - 100) {
					setFrameSize(frameWidth, faBackArrowPos + 200);
				}

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

				// System.out.println("frame height: " + frameHeight + ". Fa
				// back arrow pos: " + faBackArrowPos);

				if (faBackArrowPos >= frameHeight - 100) {
					setFrameSize(frameWidth, faBackArrowPos + 100);
				}

				if (faLength + 100 >= frameWidth) {
					setFrameSize(faLength + 200, frameHeight);
				}

				if (currentLowestPoint != 0 && faForwardArrowPos < currentLowestPoint) {
					// System.out.println("moving down");
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

		if (leftAdded == false) {
			// System.out.println("adding left to map. position: " + centrex +
			// ", " + leftCentrey);
			Integer[] posValues = { centrex, leftCentrey };
			faMap.put(left.copy(), posValues);
			leftAdded = true;
		}

		if (rightAdded == false) {
			// System.out.println("adding right to map. position: " + centrex +
			// ", " + rightCentrey);
			Integer[] posValues = { centrex, rightCentrey };
			faMap.put(right.copy(), posValues);
			rightAdded = true;
		}

		if (faAdded == false) {
			// System.out.println("adding fa to map. position: " + centrex + ",
			// " + faCentrey);
			Integer[] posValues = { centrex, faCentrey };
			faMap.put(fa.copy(), posValues);
			faAdded = true;
		}

		repaint();
	}

	private void redraw() {
		System.out.println("redrawing. map size: " + faMap.size());
		Finite_Automata fa;
		Integer[] values;

		for (Entry<Finite_Automata, Integer[]> entry : faMap.entrySet()) {
			fa = (Finite_Automata) entry.getKey();
			values = entry.getValue();

			drawer.setCentre(values[0], values[1]);
			drawer.drawFA(fa);
		}

	}

	private void setFrameSize(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		frameWidth = width;
		frameHeight = height;
	}

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

			currentParent = parent(root);

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

	public void convertToFa(Tree_Node node) {
		if (node instanceof LeafNode) {
			this.left = null;
			this.right = null;
			this.fa = converter.generateFA(node).copy();

		} else {
			Tree_Node left = node.getLeftChild();
			this.left = converter.generateFA(left).copy();
			leftAdded = false;

			if (node instanceof StarNode) {
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

	public Tree_Node parent(Tree_Node node) {
		if (node instanceof LeafNode) {
			return node;

		} else {
			Tree_Node leaf = getLeftmostLeaf(node);
			Tree_Node currentParent = leaf.getParent();

			if (currentParent instanceof StarNode) {
				return currentParent;

			} else {
				Tree_Node right = currentParent.getRightChild();
				if (right instanceof LeafNode) {
					return currentParent;

				} else {
					return parent(right);
				}
			}
		}
	}

	public Tree_Node getLeftmostLeaf(Tree_Node node) {
		if (node instanceof LeafNode) {
			return node;
		}
		return getLeftmostLeaf(node.getLeftChild());
	}

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
			System.out.println("currrent parent is null");
		}
	}

	public void setNextButton(JButton next) {
		this.btnNext = next;
	}

}
