package regex_to_fa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import toolbox.Tree_Node;

import java.util.Stack;

/*
 * Builds a binary tree based on regex input
 */
public class Tree_Builder {

	private String regex;
	private Map<Integer, Integer> bracketMap;
	private Tree_Node root;
	private Stack<Tree_Node> parentStack;

	public Tree_Builder(String regex) {
		this.regex = regex;
		bracketMap = generateBracketMap(regex);
		root = null;
		parentStack = new Stack<Tree_Node>();
	}

	/**
	 * Builds the binary tree
	 * 
	 * @return the root node of the tree
	 */
	public Tree_Node buildTree() {
		char[] chars = regex.toCharArray();

		for (int i = 0; i < regex.length(); i++) {
			char c = chars[i];

			if (c == '(') {

				int closingBracketPosition = bracketMap.get(i);
				int lastCharPosition = regex.length() - 1;

				if (closingBracketPosition != lastCharPosition) {
					// ) is NOT the last char - (..)..

					int nextCharPosition = closingBracketPosition + 1;
					char nextChar = chars[nextCharPosition];

					if (nextChar == '*') {
						// (...)*

						if (nextCharPosition != lastCharPosition) {
							addConcatNode();
						}
						addStarNode();

						// generate subtree for substring between brackets
						String substr = regex.substring(i + 1, closingBracketPosition);
						Tree_Node subtree = generateSubtree(substr);
						addChildToParent(subtree);

						i = nextCharPosition;

					} else {
						// ) is NOT the last char, and it is NOT followed by * -
						// (..)...
						// add a concat node
						addConcatNode();

						// generate the subtree for the substring between the
						// brackets
						String substr = regex.substring(i + 1, closingBracketPosition);

						Tree_Node subtree = generateSubtree(substr);
						addChildToParent(subtree);

						i = closingBracketPosition;
					}

				} else {
					// otherwise regex: (...) - brackets are irrelevant
					String substr = regex.substring(i + 1, closingBracketPosition);

					Tree_Node subtree = generateSubtree(substr);
					addChildToParent(subtree);
					i = closingBracketPosition;
				}

			} else if (c == '|') {

				// will return the closing bracket position, or -1 if not inside
				// brackets
				int closingBracketPosition = insideBrackets(i);
				if (closingBracketPosition != -1) {

					Tree_Node parent = getCurrentParent();
					if (parent instanceof Tree_Node.Concat_Node) {
						/*-
						 * put a new union node between the concat node and its left child
						 * 	 • 		  •
						 * 	/		/	
						 * b  ->   |
						 *  	  /
						 *   	 b
						 *
						 */

						Tree_Node leftChild = parent.getLeftChild();
						Tree_Node unionNode = new Tree_Node.Union_Node();
						parentStack.push(unionNode);
						unionNode.addChild(leftChild);
						leftChild.setParent(unionNode);
						parent.setLeftChild(unionNode);

						// generate subtree between |..)
						String substr = regex.substring(i + 1, closingBracketPosition);
						Tree_Node subtree = generateSubtree(substr);
						addChildToParent(subtree);

						i = closingBracketPosition; // skip to position after
													// the brackets
					}
				} else { // node inside brackets
					// add union node as the new root
					// replace the current parent with its left child

					/*-
					 * 		•			|
					 * 	   /	->	   /
					 * 	  b			  b
					 * 
					 * 		*			|
					 * 	   /		   /
					 * 	  •		->	  *
					 * 	 /			 /
					 *  b			b
					 */

					Tree_Node unionNode = new Tree_Node.Union_Node();
					unionNode.addChild(root);
					root.setParent(unionNode);
					root = unionNode;

					Tree_Node currentParent = getCurrentParent();
					Tree_Node leftChild = currentParent.getLeftChild();

					if (currentParent != root) {
						Tree_Node ancestor = currentParent.getParent();

						ancestor.replaceChild(currentParent, leftChild);

						leftChild.setParent(ancestor);
					}
					parentStack.pop();
					parentStack.insertElementAt(unionNode, 0);
				}

			} else if (Character.isLetterOrDigit(c)) {
				int nextCharPosition = i + 1;

				if (nextCharPosition == regex.length() || chars[nextCharPosition] == ')') {
					addLeafToParent(c);
				} else if (chars[nextCharPosition] == '*') {

					if (nextCharPosition < regex.length() - 1) {
						// if * is NOT the last char
						addConcatNode();
					}
					addStarNode();
					addLeafToParent(c);
					i = nextCharPosition;
				} else {
					addConcatNode();
					addLeafToParent(c);
				}
			}
		}
		return root;
	}

	/**
	 * Generates a subtree for regex between brackets
	 * 
	 * @param regex
	 *            to build the subtree for
	 * @return the root of the subtree
	 */
	private Tree_Node generateSubtree(String regex) {
		Tree_Builder builder = new Tree_Builder(regex);
		Tree_Node subtree = builder.buildTree();
		return subtree;
	}

	/**
	 * Creates a map of the brackets in the regex
	 * 
	 * @param substring
	 * @return
	 */
	private Map<Integer, Integer> generateBracketMap(String substring) {
		Map<Integer, Integer> bracketMap = new HashMap<Integer, Integer>();

		Stack<Integer> stack = new Stack<Integer>();
		char[] chars = substring.toCharArray();

		for (int i = 0; i < substring.length(); i++) {
			char c = chars[i];

			if (c == '(') {
				stack.push(i);
			} else if (c == ')') {
				if (!stack.isEmpty()) {
					int key = stack.pop();
					bracketMap.put(key, i); // add the positions of the bracket
											// pair to the map
				}
			}
		}
		return bracketMap;
	}

	/**
	 * Adds a concatenation node, •, to the binary tree
	 */
	private void addConcatNode() {
		Tree_Node concatNode;
		if (root == null) {
			concatNode = new Tree_Node.Concat_Node(null);
			root = concatNode;
		} else {
			Tree_Node parent = getCurrentParent();
			concatNode = new Tree_Node.Concat_Node(parent);

			addChildToParent(concatNode);
		}
		parentStack.push(concatNode);
	}

	/**
	 * Adds a Kleene star node, *, to the binary tree
	 */
	private void addStarNode() {
		Tree_Node starNode;
		if (root == null) {
			starNode = new Tree_Node.Star_Node(null);
			root = starNode;
		} else {
			Tree_Node parent = getCurrentParent();
			starNode = new Tree_Node.Star_Node(parent);

			addChildToParent(starNode);
		}
		parentStack.push(starNode);
	}

	/**
	 * 
	 * @return the current parent which has a child node space available
	 */
	private Tree_Node getCurrentParent() {
		if (!parentStack.isEmpty()) {
			return parentStack.peek();
		}
		return null;
	}

	/**
	 * adds a leaf node to the current parent node
	 * 
	 * @param c
	 *            - the char of the child node
	 */
	private void addLeafToParent(char c) {
		addChildToParent(new Tree_Node.Leaf_Node(Character.toString(c)));
	}

	/**
	 * Handles how the child is added to the current parent
	 * 
	 * @param child
	 */
	private void addChildToParent(Tree_Node child) {
		Tree_Node parent = getCurrentParent();

		if (parent == null) {
			root = child;
			child.setParent(null);
		} else {
			parent.addChild(child);
			child.setParent(parent);

			if (parent instanceof Tree_Node.Star_Node && parent.getChildCount() == 1) {
				Tree_Node prevParent = parentStack.pop();

			} else if (parent.getChildCount() == 2) {
				Tree_Node prevParent = parentStack.pop();
			}
		}
	}

	/**
	 * Checks to see if this position is inside brackets
	 * 
	 * @param position
	 *            - index position in the regex
	 * @return -1 if its not inside brackets, or the position of the closing
	 *         bracket if it is inside brackets
	 */
	private int insideBrackets(int position) {
		int endBracketPosition = -1;

		for (Entry<Integer, Integer> entry : bracketMap.entrySet()) {
			if (position > entry.getKey() && position < entry.getValue()) {
				endBracketPosition = entry.getValue();
			}
		}
		return endBracketPosition;
	}

}
