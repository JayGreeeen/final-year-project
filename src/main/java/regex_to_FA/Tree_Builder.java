package regex_to_FA;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class Tree_Builder {

	private String regex;
	private Map<Integer, Integer> bracketMap;
	private TreeNode root;
	private Stack<TreeNode> parentStack;

	public Tree_Builder(String regex) {
		this.regex = regex;
		bracketMap = generateBracketMap(regex);
		root = null;
		parentStack = new Stack<TreeNode>();
	}

	public TreeNode buildTree() {
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
						TreeNode subtree = generateSubtree(substr);
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

						TreeNode subtree = generateSubtree(substr);
						addChildToParent(subtree);
						// System.out.println("\t Generated subtree for " +
						// substr + " . Root: " + subtree);

						i = closingBracketPosition;
					}

				} else {
					String substr = regex.substring(i + 1, closingBracketPosition);

					TreeNode subtree = generateSubtree(substr);
					addChildToParent(subtree);
					// System.out.println("\t Generated subtree for " + substr +
					// " . Root: " + subtree);
				}
				// otherwise regex: (...) - brackets are irrelevant

			} else if (c == '|') {

				// will return the closing bracket position, or -1 if not inside
				// brackets
				int closingBracketPosition = insideBrackets(i);
				if (closingBracketPosition != -1) {
					// System.out.println("Union symbol found inside brackets");

					TreeNode parent = getCurrentParent();
					if (parent instanceof TreeNode.ConcatNode) {
						/*-
						 * put a new union node between the concat node and its left child
						 * 	 • 		  •
						 * 	/		/	
						 * b  ->   |
						 *  	  /
						 *   	 b
						 *
						 */

						TreeNode leftChild = parent.getLeftChild();
						TreeNode unionNode = new TreeNode.UnionNode();
						parentStack.push(unionNode);
						unionNode.addChild(leftChild);
						leftChild.setParent(unionNode);
						parent.setLeftChild(unionNode);

						// generate subtree between |..)
						String substr = regex.substring(i + 1, closingBracketPosition);
						TreeNode subtree = generateSubtree(substr);
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
					// System.out.println("Union symbol found outside
					// brackets");

					TreeNode unionNode = new TreeNode.UnionNode();
					unionNode.addChild(root);
					root.setParent(unionNode);
					root = unionNode;

					TreeNode currentParent = getCurrentParent();
					TreeNode leftChild = currentParent.getLeftChild();

					if (currentParent != root) {
						TreeNode ancestor = currentParent.getParent();
						ancestor.setLeftChild(leftChild);
						leftChild.setParent(ancestor);
					}
					parentStack.pop();
					parentStack.insertElementAt(unionNode, 0);
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

	private TreeNode generateSubtree(String regex) {
		Tree_Builder builder = new Tree_Builder(regex);
		TreeNode subtree = builder.buildTree();
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
		TreeNode concatNode;
		if (root == null) {
			concatNode = new TreeNode.ConcatNode(null);
			root = concatNode;
		} else {
			TreeNode parent = getCurrentParent();
			concatNode = new TreeNode.ConcatNode(parent);
		}
		parentStack.push(concatNode);
		// System.out.println("Stack: " + parentStack);
	}

	private void addStarNode() {
		// System.out.println("Added * node");
		TreeNode starNode;
		if (root == null) {
			starNode = new TreeNode.StarNode(null);
			root = starNode;
		} else {
			TreeNode parent = getCurrentParent();
			starNode = new TreeNode.StarNode(parent);
		}
		parentStack.push(starNode);
		// System.out.println("Stack: " + parentStack);
	}

	private TreeNode getCurrentParent() {
		if (!parentStack.isEmpty()) {
			return parentStack.peek();
		}
		return null;
	}

	private void addLeafToParent(char c) {
		addChildToParent(new TreeNode.LeafNode(Character.toString(c)));
	}

	private void addChildToParent(TreeNode child) {
		TreeNode parent = getCurrentParent();

		if (parent == null) {
			root = child;
		} else {
			parent.addChild(child);
			child.setParent(parent);

			if (parent instanceof TreeNode.StarNode && parent.getChildCount() == 1) {
				TreeNode prevParent = parentStack.pop();

				if (!parentStack.isEmpty()) {
					addChildToParent(prevParent);
				}

			} else if (parent.getChildCount() == 2) {
				TreeNode prevParent = parentStack.pop();

				if (!parentStack.isEmpty()) {
					addChildToParent(prevParent);
				}
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
