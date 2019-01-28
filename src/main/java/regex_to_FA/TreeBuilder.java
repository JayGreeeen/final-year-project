package regex_to_FA;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import regex_to_FA.Node.ConcatNode;
import regex_to_FA.Node.StarNode;

public class TreeBuilder {

	private static Node root;

	private Stack<Node> parentStack;

	public TreeBuilder() {
		parentStack = new Stack<Node>();
	}

	public Node generateTree(String regex, Map<Integer, Integer> bracketMap) {
		char[] chars = regex.toCharArray();

		for (int i = 0; i < regex.length(); i++) {
			char c = chars[i];

			if (c == '(') {
				System.out.println("Found opening bracket");
				int closingBracketPosition = bracketMap.get(i);
				int positionAfterClosingBracket = closingBracketPosition + 1;

				// make sure this is a valid position
				if (positionAfterClosingBracket < regex.length()) {

					char charAfter = chars[positionAfterClosingBracket];
					System.out.println("the char after the closing bracket is: " + charAfter);

					if (charAfter == '*') {

						if (positionAfterClosingBracket != regex.length() - 1) {
							// if its not the last char
							System.out.println("* is not the last char");
							addConcatNode();
						}
						// star is the last char
						addStarNode();

						String substr = regex.substring(i + 1, closingBracketPosition);
						System.out.println("substring: " + substr);

						// generate the tree for the substring
						generateSubTree(substr);
						// System.out.println("subtree root: " +
						// subtree.getText());
						// PrintTree.print(subtree);

						// current char = * node - skip to this node
						i = positionAfterClosingBracket;

					}
				}
				
				// *******************
				// wonder if its worth creating the subtree by recalling the whole class and having it completely separate 
				// 		not sure how that would affect the parent stack, would need to try it out with examples
				// would then need to link the root of the subtree to the current parents of the tree
				// example: (a|b)* - doesnt currently work with this, since the substring a|b replaces the current parent: * 
				
			} else if (c == '|'){
				// determine whether the char is between brackets 
				Entry<Integer, Integer> bracketPair = insideBrackets(i, bracketMap);
				
				if (bracketPair != null && bracketPair.getValue() != regex.length() - 1){	//if it is null, then | is not inside brackets
//					System.out.println("| is inside brakcets entry: " + bracketPair.getKey() + " " + bracketPair.getValue());
					Node parent = getCurrentParent();
					if (parent != null) {
						
						System.out.println("Inserting union between the current parent and left child");
						System.out.println("\t current parent is " + parent);
						if (parent instanceof ConcatNode) {
							 Node leftChild = parent.getLeftChild();
							 Node unionNode = new Node.UnionNode();
							 leftChild.setParent(unionNode);
							 unionNode.addChild(leftChild);
							 unionNode.setParent(parent); // ********
							 parent.setLeftChild(unionNode);
							 parentStack.push(unionNode);
							 System.out.println("\t Stack: " + parentStack.toString());
							 
							 String substr = regex.substring(i + 1, bracketPair.getValue());							 
							 generateSubTree(substr);
							 
							 i = bracketPair.getValue();	// skip to char after brackets 
						}
					}
					
				} else {
					System.out.println("Removing the parent node from the tree");
					Node unionNode = new Node.UnionNode();
					Node currentRoot = root;
					System.out.println("\t current root is " + root);
					root.setParent(unionNode);
					unionNode.addChild(currentRoot);
					
					// replace current root with union node
					int index = parentStack.indexOf(root);
					parentStack.add(index, unionNode);

					root = unionNode;

					Node currentParent = getCurrentParent();
					parentStack.remove(parentStack.indexOf(currentParent));
					Node leftChild = currentParent.getLeftChild();
					Node ancestor = currentParent.getParent();
					
					System.out.println("\t replacing " + currentParent + " with left child " + leftChild);
					
					ancestor.setLeftChild(leftChild);
					leftChild.setParent(ancestor);
				}
				
				// otherwise, this means that the closing bracket is the last
				// position - so leave it.
			} else if (Character.isLetterOrDigit(c)) {
				System.out.println("regex: " + regex + ". current char: " + c);

				if (i + 1 >= regex.length() || chars[i + 1] == ')') {

					addLeafNode(c);
				} else if (chars[i + 1] == '*') {
					if (i + 1 != regex.length() - 1) {
						addConcatNode(); // * is not the last char
					}
					addStarNode();
					addLeafNode(c);
					i++; // skip the next char
				} else {
					addConcatNode();
					addLeafNode(c);
				}
			}
		}
		System.out.println("done with " + regex);
		return root;
	}
	
	private Entry<Integer, Integer> insideBrackets(int position, Map<Integer, Integer> bracketMap){
		Entry<Integer, Integer> bracketPair = null;
		for (Entry<Integer, Integer> entry : bracketMap.entrySet()){
			if (position > entry.getKey() && position < entry.getValue()){
				System.out.println("Union symbol is between " + entry.getKey() + " and " + entry.getValue());
				bracketPair = entry;
			}
		}
		return bracketPair;
	}

	private Node generateSubTree(String substring) {
		System.out.println("building subtree");
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
					bracketMap.put(key, i);
					// add the positions of the bracket pair to the map
				}
			}
		}
		return generateTree(substring, bracketMap);
	}

	private boolean treeInitialised() {
		if (root == null) {
			System.out.println("tree is not initialised");
			return false;
		}
		System.out.println("tree is initialised. Root is: " + root.getText() + ", and current parent is: "
				+ getCurrentParent().getText() + ". stack: " + parentStack.toString());

		return true;
	}

	private void addConcatNode() {
		System.out.println("Adding concat node");
		Node concatNode;
		if (treeInitialised()) {
			concatNode = new Node.ConcatNode(getCurrentParent());
		} else {

			concatNode = new Node.ConcatNode(null);
			root = concatNode;
		}
		parentStack.push(concatNode);
		System.out.println("stack: " + parentStack.toString());
	}

	 public void addUnionNode(){
	
	 }

	public void addStarNode() {
		System.out.println("adding star node");
		Node starNode;
		if (treeInitialised()) {
			starNode = new Node.StarNode(getCurrentParent());
		} else {
			starNode = new Node.StarNode(null);
			root = starNode;
		}
		parentStack.push(starNode);
		System.out.println("stack: " + parentStack.toString());
	}

	public void addLeafNode(char c) {
		System.out.println("adding leaf node to current parent: " + getCurrentParent().getText());
		addChildToParentNode(new Node.LeafNode(Character.toString(c), getCurrentParent()));

	}

	public Node getCurrentParent() {
		if (!parentStack.isEmpty()) {
			return parentStack.peek();
		}
		return null;
	}

	public void addChildToParentNode(Node child) {
		System.out.println("Adding child " + child.getText());
		Node parent = getCurrentParent();
		parent.addChild(child);
		child.setParent(parent);

		if (parent.getChildCount() == 2) {
			parentStack.pop();
			System.out.println("Removed parent " + parent.getText() + " from stack");
			if (!parentStack.isEmpty()) {
				System.out.println("Current parent now " + parentStack.peek().getText());
				
				if (!parent.parentSet()){
					addChildToParentNode(parent);
				}
			}

		} else if (parent instanceof StarNode && parent.getChildCount() == 1) {
			parentStack.pop();
			System.out.println("Removed parent " + parent.getText() + " from stack");
			if (!parentStack.isEmpty()) {
				System.out.println("Current parent now " + parentStack.peek().getText());
				if (!parent.parentSet()){
					addChildToParentNode(parent);
				}			
			}
		}

	}

}
