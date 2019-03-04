package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

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

	private boolean redraw;

	public Regex_Converter_Panel() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		converter = new Regex_to_FA(this);
		redraw = false;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawer = new FA_Drawer();
		drawer.setGraphics(g);
		drawer.setCentre(centrex, leftCentrey);

		if (left != null) {
//			System.out.println("Drawing left at position: " + centrex + ", " + leftCentrey);
			drawer.drawFA(left);

			FA_Dimension leftDimension = drawer.getDimension();

			int leftLowestPos = leftDimension.getTransitionHeight();
			int leftHighestPos = leftDimension.getBackTransitionHeight();

//			g.setColor(Color.RED);
//			g.drawLine(centrex, leftLowestPos, centrex + 200, leftLowestPos);
//			g.drawLine(centrex, leftHighestPos, centrex + 200, leftHighestPos);
//			g.setColor(Color.black);

			// TODO
			// int leftLength = leftArea.getLength();
			
			if (leftLowestPos < 0){
				System.out.println("left goes off the page");
				redraw = true;
				int difference = 0 - leftLowestPos;
				leftCentrey += difference + 20;
			}

			if (right != null) {
				drawer.setCentre(centrex, rightCentrey);

//				System.out.println("Drawing right at position: " + centrex + ", " + rightCentrey);
				drawer.drawFA(right);

				FA_Dimension rightDimension = drawer.getDimension();

				int rightLowestPos = rightDimension.getTransitionHeight();
				int rightHighestPos = rightDimension.getBackTransitionHeight();

//				g.setColor(Color.BLUE);
//				g.drawLine(centrex, rightLowestPos, centrex + 200, rightLowestPos);
//				g.drawLine(centrex, rightHighestPos, centrex + 200, rightHighestPos);
//				g.setColor(Color.black);
//				System.out.println("\tLeft lowest: " + leftHighestPos + ". Right highest: " + rightLowestPos);

				if (leftHighestPos >= rightLowestPos) {
					System.out.println("Right overlaps left ");

					int difference = leftHighestPos - rightLowestPos;
					int spacing = 20;

					rightCentrey += difference + spacing;
					redraw = true;
				}

				drawer.setCentre(centrex, faCentrey);
//				System.out.println("Drawing fa at position: " + centrex + ", " + faCentrey);
				drawer.drawFA(fa);

				FA_Dimension faDimension = drawer.getDimension();

				int faLowestPos = faDimension.getTransitionHeight();
				int faHighestPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();
//
//				g.setColor(Color.ORANGE);
//				g.drawLine(centrex, faLowestPos, centrex + 200, faLowestPos);
//				g.drawLine(centrex, faHighestPos, centrex + 200, faHighestPos);
//				g.setColor(Color.black);
//				System.out.println("\tRight lowest: " + rightHighestPos + ". fa highest: " + faLowestPos);

				if (rightHighestPos >= faLowestPos) {
					System.out.println("fa overlaps right ");

					int difference = rightHighestPos - faLowestPos;
					int spacing = 20;

					faCentrey += difference + spacing;
					redraw = true;
				}

				if (faHighestPos >= this.getHeight()) {
					setFrameSize(frameWidth, faHighestPos + 100);
				}

				if (faLength + 100 >= frameWidth) {
					setFrameSize(faLength + 200, frameHeight);
				}

			} else {
				// no right child

				drawer.setCentre(centrex, faCentrey);
//				System.out.println("Drawing fa at position: " + centrex + ", " + faCentrey);
				drawer.drawFA(fa);

				FA_Dimension faDimension = drawer.getDimension();

				int faLowestPos = faDimension.getTransitionHeight();
				int faHighestPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();

//				System.out.println("\tLeft lowest: " + leftHighestPos + ". fa highest: " + faLowestPos);
//				g.setColor(Color.green);
//				g.drawLine(centrex, faLowestPos, centrex + 200, faLowestPos);
//				g.setColor(Color.black);

				if (leftHighestPos >= faLowestPos) {
					System.out.println("fa overlaps left");
					
					int difference = leftHighestPos - faLowestPos;
					int spacing = 20;
					faCentrey = difference + spacing;
					redraw = true;
				}
				
				if (faHighestPos >= this.getHeight()) {
					setFrameSize(frameWidth, faHighestPos + 100);
				}

				if (faLength + 100 >= frameWidth) {
					setFrameSize(faLength + 200, frameHeight);
				}
			}
		} else {
			if (fa != null) {
				drawer.drawFA(fa);
				
				FA_Dimension faDimension = drawer.getDimension();
				
				int faLowestPos = faDimension.getTransitionHeight();
				int faHighestPos = faDimension.getBackTransitionHeight();
				int faLength = faDimension.getLength();
				
				if (faLowestPos < 0){
					redraw = true;
					int difference = 0 - faLowestPos;
					leftCentrey += difference + 20;
				}
				
				if (faHighestPos >= this.getHeight()) {
					setFrameSize(frameWidth, faHighestPos + 100);
				}

				if (faLength + 100 >= frameWidth) {
					setFrameSize(faLength + 200, frameHeight);
				}
				
			}
		}
		if (redraw == true) {
			System.out.println("redrawing");
			redraw = false;
			repaint();
		}
	}

	private void setFrameSize(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		frameWidth = width;
		frameHeight = height;
	}

	public void convert(String regex) {
		setFrameSize(2000, 500);
		leftCentrey = 150;
		rightCentrey = 300;
		faCentrey = 450;

		String errorMessage = converter.validate(regex);

		if (errorMessage == "") {

			root = converter.convertToTree(regex);

			// Print_Tree.printSideways(root);
			// Print_Tree.print(root);

			currentParent = parent(root);
			// System.out.println("current parent is " + currentParent.getText()
			// + ". Generating FA");

			if (currentParent.getParent() == null) {
				btnNext.setEnabled(false);
			} else {
				btnNext.setEnabled(true);
			}

			convertToFa(currentParent);

			// getCurrentParent(root);
			// maybe display the tree ?

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

			if (node instanceof StarNode) {
				this.right = null;

			} else {
				Tree_Node right = node.getRightChild();
				this.right = converter.generateFA(right).copy();
			}
			this.fa = converter.generateFA(node).copy();
		}
	}

	private void checkOverlap() {

		// rectangle.intersects(rectange)

	}

	public Tree_Node parent(Tree_Node node) {
		if (node instanceof LeafNode) {
			return node;

		} else {

			Tree_Node leaf = getLeftmostLeaf(node);
			Tree_Node currentParent = leaf.getParent();

			if (currentParent instanceof StarNode) {
				// dont get the right child
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
		// System.out.println("node is : " + node.getText());
		return getLeftmostLeaf(node.getLeftChild());
	}

	public void drawNext() {
		if (currentParent.getParent() != null) {
			Tree_Node parent = currentParent.getParent();
			// System.out.println("parent of parent: " + parent.getText());

			currentParent = parent;
			if (currentParent.getParent() == null) {
				// no more steps
				btnNext.setEnabled(false);
			}
			// System.out.println("***** parent : " + currentParent.getText());

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
