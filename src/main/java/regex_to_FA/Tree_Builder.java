package regex_to_FA;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

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

	public Tree_Node buildTree() {
		// System.out.println("Building tree for " + regex);
		char[] chars = regex.toCharArray();

		for (int i = 0; i < regex.length(); i++) {
			char c = chars[i];

			if (c == '(') {
				// System.out.println("Opening bracket");

				int closingBracketPosition = bracketMap.get(i);
				int lastCharPosition = regex.length() - 1;

				if (closingBracketPosition != lastCharPosition) {
					// ) is NOT the last char - (..)..

					int nextCharPosition = closingBracketPosition + 1;
					char nextChar = chars[nextCharPosition];

					if (nextChar == '*') {
						// (...)*
						// System.out.println("Closing bracket followed by *");

						if (nextCharPosition != lastCharPosition) {
							addConcatNode();
						}
						addStarNode();

						// generate subtree for substring between brackets
						String substr = regex.substring(i + 1, closingBracketPosition);
						Tree_Node subtree = generateSubtree(substr);
						// System.out.println("Generated subtree for " + substr
						// + " . Root: " + subtree);
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
						// System.out.println("\t Generated subtree for " +
						// substr + " . Root: " + subtree);

						i = closingBracketPosition;
					}

				} else {
					// otherwise regex: (...) - brackets are irrelevant
					String substr = regex.substring(i + 1, closingBracketPosition);

					Tree_Node subtree = generateSubtree(substr);
					addChildToParent(subtree);
					// System.out.println("\t Generated subtree for " + substr +
					// " . Root: " + subtree);
					i = closingBracketPosition;
				}

			} else if (c == '|') {

				// will return the closing bracket position, or -1 if not inside
				// brackets
				int closingBracketPosition = insideBrackets(i);
				if (closingBracketPosition != -1) {
					// System.out.println("Union symbol found inside brackets");

					Tree_Node parent = getCurrentParent();
					if (parent instanceof Tree_Node.ConcatNode) {
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
						Tree_Node unionNode = new Tree_Node.UnionNode();
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
					// System.out.println("Union symbol found outside brackets.
					// Adding union node as new root");

					Tree_Node unionNode = new Tree_Node.UnionNode();
					unionNode.addChild(root);
					root.setParent(unionNode);
					root = unionNode;

					Tree_Node currentParent = getCurrentParent();
					Tree_Node leftChild = currentParent.getLeftChild();
					// System.out
					// .println("Replacing current parent " + currentParent + "
					// with its left child " + leftChild);

					if (currentParent != root) {
						Tree_Node ancestor = currentParent.getParent();

						// **************
						ancestor.replaceChild(currentParent, leftChild);

						leftChild.setParent(ancestor);
						// System.out.println("ancestor " + ancestor + " is the
						// new parent of " + leftChild);
					}
					parentStack.pop();
					parentStack.insertElementAt(unionNode, 0);
					// System.out.println("Stack: " + parentStack);
				}

			} else if (Character.isLetterOrDigit(c)) {
				// System.out.println("Symbol of alphabet: " + c);

				int nextCharPosition = i + 1;

				if (nextCharPosition == regex.length() || chars[nextCharPosition] == ')') {
					// System.out.println("Next symbol is null or )");
					addLeafToParent(c);
				} else if (chars[nextCharPosition] == '*') {
					// System.out.println("next symbol is *");

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

	private Tree_Node generateSubtree(String regex) {
		Tree_Builder builder = new Tree_Builder(regex);
		Tree_Node subtree = builder.buildTree();
		return subtree;
	}

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

	private void addConcatNode() {
		// System.out.println("Added • node");
		Tree_Node concatNode;
		if (root == null) {
			concatNode = new Tree_Node.ConcatNode(null);
			root = concatNode;
		} else {
			Tree_Node parent = getCurrentParent();
			concatNode = new Tree_Node.ConcatNode(parent);

			// *************
			addChildToParent(concatNode);
		}
		parentStack.push(concatNode);
		// System.out.println("Added • node to stack. Stack: " + parentStack);
	}

	private void addStarNode() {
		// System.out.println("Added * node");
		Tree_Node starNode;
		if (root == null) {
			starNode = new Tree_Node.StarNode(null);
			root = starNode;
		} else {
			Tree_Node parent = getCurrentParent();
			starNode = new Tree_Node.StarNode(parent);

			// ***********
			addChildToParent(starNode);
		}
		parentStack.push(starNode);
		// System.out.println("Added * node to stack. Stack: " + parentStack);
	}

	private Tree_Node getCurrentParent() {
		if (!parentStack.isEmpty()) {
			return parentStack.peek();
		}
		return null;
	}

	private void addLeafToParent(char c) {
		addChildToParent(new Tree_Node.LeafNode(Character.toString(c)));
	}

	private void addChildToParent(Tree_Node child) {
		Tree_Node parent = getCurrentParent();

		if (parent == null) {
			root = child;
			child.setParent(null);
		} else {
			parent.addChild(child);
			child.setParent(parent);
			// System.out.println("Adding " + child.getText() + " to parent " +
			// parent + ". parent has "
			// + parent.getChildCount() + " children");

			if (parent instanceof Tree_Node.StarNode && parent.getChildCount() == 1) {
				Tree_Node prevParent = parentStack.pop();
				// System.out.println("Parent node full - removing " +
				// prevParent + ". Stack: " + parentStack);

				// if (!parentStack.isEmpty()) {
				// addChildToParent(prevParent);
				// }

			} else if (parent.getChildCount() == 2) {
				Tree_Node prevParent = parentStack.pop();
				// System.out.println("Parent node full - removing " +
				// prevParent + ". Stack: " + parentStack);
				// if (!parentStack.isEmpty()) {
				// addChildToParent(prevParent);
				// }
			}
		}
	}

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
