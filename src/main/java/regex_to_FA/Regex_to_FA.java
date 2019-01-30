package regex_to_FA;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import regex_to_FA.TreeNode.ConcatNode;
import regex_to_FA.TreeNode.LeafNode;
import regex_to_FA.TreeNode.StarNode;
import regex_to_FA.TreeNode.UnionNode;

public class Regex_to_FA {

	private Map<Integer, Integer> bracketMap = new HashMap<Integer, Integer>();
	private char[] chars;

	// take in a regex and turn it into a FA
	public FiniteAutomata convertToFA(String regex) {
		chars = regex.toCharArray();

		boolean valid = validate(regex);
		if (!valid) {
			System.out.println("Regex not valid");
		} else {
			System.out.println("building tree");
			TreeNode root = (new Tree_Builder(regex)).buildTree();

			PrintTree.print(root);
			FiniteAutomata FA = generateFA(root);
			return FA;
		}
		return null;
	}

	private FiniteAutomata generateFA(TreeNode node) {

		// take in the root of the tree and generate the FA
		Automaton_Builder builder = new Automaton_Builder();

		if (node instanceof LeafNode) {
			return builder.buildSimpleAutomaton((LeafNode) node);
		} else {
			// not a leaf node

			if (node.getLeftChild() != null) {
				System.out.println("\tGenerating FA for left child");
				FiniteAutomata leftFA = generateFA(node.getLeftChild());

				if (node instanceof StarNode) {
					System.out.println("Adding star operator to left child");
					return builder.addStarOperator(leftFA);
				} else {

					if (node.getRightChild() != null) {
						System.out.println("\tGenerating FA for right child");
						FiniteAutomata rightFA = generateFA(node.getRightChild());
						
						if (node instanceof UnionNode) {
							// use rule 3 to connect child NFAs
							System.out.println("\tCombining child nodes with |");
							return builder.combineWithUnion(leftFA, rightFA);
							
						} else if (node instanceof ConcatNode) {
							// use rule 4 to connect child NFAs
							System.out.println("\tCombining child nodes with •");
							return builder.combineWithConcat(leftFA, rightFA);
						}
					} else {
						// no right child - should never be the case
						System.out.println("No right child found");
					}
				}
			}
			// the root node is none of the above - problem 
			return null;
		}
	}

	private boolean validate(String regex) {
		boolean bracketsMatch = validateBrackets(regex);
		if (!bracketsMatch) {
			System.out.println("brackets dont match");
			return false;
		}

		boolean validSymbols = validateSymbols(regex);
		if (!validSymbols) {
			System.out.println("symbols not valid");
			return false;
		}

		boolean validKleene = validateKleeneStar(regex);
		if (!validKleene) {
			System.out.println("Use of Kleene star not valid");
			return false;
		}

		boolean validUnion = validateUnion(regex);
		if (!validUnion) {
			System.out.println("Use of union not valid");
			return false;
		}

		return true;
	}

	private boolean validateSymbols(String regex) {
		String symbols = regex.replaceAll("[\\w]", "");

		if (symbols.length() == 0) {
			return true; // valid - contains no symbols
		} else {
			if (StringUtils.containsOnly(symbols, "()|*")) {
				return true; // valid - contains valid symbols
			}
		}
		return false; // not valid - contains other symbols
	}

	private boolean validateBrackets(String regex) {
		if (!regex.contains("(") || !regex.contains(")")) {
			return true;
		}
		int openCount = StringUtils.countMatches(regex, '(');
		int closeCount = StringUtils.countMatches(regex, ')');

		Stack<Integer> stack = new Stack<Integer>();
		if (openCount == closeCount) {

			for (int i = 0; i < regex.length(); i++) {
				char c = chars[i];

				if (c == '(') {
					stack.push(i);
				} else if (c == ')') {
					if (stack.isEmpty()) {
						return false;
						// regex is not valid - too many closing brackets
					} else {
						int key = stack.pop();
						bracketMap.put(key, i);
						// add the positions of the bracket pair to the map
					}
				}
			}
			if (stack.isEmpty()) {
				return true;
			}
		}
		return false; // regex is not valid - too many opening brackets
	}

	public boolean validateKleeneStar(String regex) {
		if (!regex.contains("*")) {
			return true;
		}
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];

			if (c == '*') {
				if ((i - 1) >= 0) {
					char prevChar = chars[i - 1];

					if (Character.isLetterOrDigit(prevChar)) {
						return true;
					}
					if (prevChar == ')' && chars[i - 2] != '(') {
						// cant have the case ()*
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean validateUnion(String regex) {
		if (!regex.contains("|")) {
			return true;
		}
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '|') {
				if ((i - 1) >= 0 && (i + 1) < regex.length()) {
					char charBefore = chars[i - 1];
					char charAfter = chars[i + 1];

					if (charBefore == '*' || charBefore == ')' || Character.isLetterOrDigit(charBefore)) {
						if (charAfter == '(' || Character.isLetterOrDigit(charAfter)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
