package regex_to_fa;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import model.Regex_Converter_Panel;
import toolbox.Finite_Automata;
import toolbox.Tree_Node;
import toolbox.Tree_Node.ConcatNode;
import toolbox.Tree_Node.LeafNode;
import toolbox.Tree_Node.StarNode;
import toolbox.Tree_Node.UnionNode;

public class Regex_to_FA {

	private Map<Integer, Integer> bracketMap = new HashMap<Integer, Integer>();
	private char[] chars;
	
	private Regex_Converter_Panel converter;
	
	public Regex_to_FA(Regex_Converter_Panel converter){
		this.converter = converter;
	}
	
	// take in a regex and turn it into a FA
//	public Finite_Automata convertToFA(String regex) {
//		regex = regex.replace(" ", "");
//		System.out.println("Regex - " + regex);
//		// chars = regex.toCharArray();
//
//		// boolean valid = validate(regex);
//		// if (valid == false) {
//		// System.out.println("Regex not valid");
//		// } else {
//		// System.out.println("building tree");
//		
//		Tree_Node root = (new Tree_Builder(regex)).buildTree();
//		Finite_Automata FA = generateFA(root);
//		return FA;
//
//		// }
//		// return null;
//	}
	
	public Tree_Node convertToTree(String regex){
		return (new Tree_Builder(regex)).buildTree();
	}

	public Finite_Automata generateFA(Tree_Node node) {

		// take in the root of the tree and generate the FA
		Automaton_Builder builder = new Automaton_Builder();

		if (node instanceof LeafNode) {
//			Finite_Automata leftFA = builder.buildSimpleAutomaton((LeafNode) node);
//			converter.setLeftFA(leftFA);
//			return leftFA;
			return builder.buildSimpleAutomaton((LeafNode) node);
			
		} else {
			// not a leaf node

			if (node.getLeftChild() != null) {
//				 System.out.println("\tGenerating FA for left child - " + node.getLeftChild().getText());
				Finite_Automata leftFA = generateFA(node.getLeftChild());
				// System.out.println("Left child: " + leftFA);
				
				
//				converter.setLeftFA(leftFA);
				// ******************
				// draw the left child

				if (node instanceof StarNode) {
//					 System.out.println("Adding star operator to left child");
					
					
					// ***********
					// combining left child with char operator 
					// draw out the left child and right child which are being combined
					
					Finite_Automata fa = builder.addStarOperator(leftFA);
					
//					converter.setRightFA(null);
//					converter.setCombinedFA(fa);
					
					return fa;
					
					// ******************
					// draw the combined fa
					
				} else {

					if (node.getRightChild() != null) {
//						 System.out.println("\tGenerating FA for right child - " + node.getRightChild().getText());
						Finite_Automata rightFA = generateFA(node.getRightChild());
						// System.out.println("Right child: " + rightFA);
						
						// ******************
						// draw the right child
//						converter.setRightFA(rightFA);

						if (node instanceof UnionNode) {
							// use rule 3 to connect child NFAs
//							 System.out.println("\tCombining child nodes with |");
							
							Finite_Automata fa = builder.combineWithUnion(leftFA, rightFA);
							
//							converter.setCombinedFA(fa);
							
							return fa;
							
							// ******************
							// draw the combined fa

						} else if (node instanceof ConcatNode) {
							// use rule 4 to connect child NFAs
//							 System.out.println("\tCombining child nodes with •");
							Finite_Automata fa = builder.combineWithConcat(leftFA, rightFA);
							
//							converter.setCombinedFA(fa);
							
							return fa;
							// ******************
							// draw the combined fa
							
						}
					} else {
						// no right child - should never be the case
						// System.out.println("No right child found");
					}
				}
			}
			// the root node is none of the above - problem
			return null;
		}
	}

	public String validate(String regex) {
		chars = regex.toCharArray();
		boolean bracketsMatch = validateBrackets(regex);

		if (bracketsMatch == false) {
			// System.out.println("brackets dont match");
			return "Brackets do not match";
			// return false;
		}

		boolean validSymbols = validateSymbols(regex);
		if (validSymbols == false) {
			// System.out.println("symbols not valid");
			return "Symbols not valid";
			// return false;
		}

		boolean validKleene = validateKleeneStar(regex);
		if (validKleene == false) {
			// System.out.println("Use of Kleene star not valid");
			return "Use of Kleene star * not valid";
//			return false;
		}

		boolean validUnion = validateUnion(regex);
		if (validUnion == false) {
			// System.out.println("Use of union not valid");
			return "Use of union | not valid";
			// return false;
		}

		return "";
		// return true;
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
		if (!regex.contains("(") && !regex.contains(")")) {
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
