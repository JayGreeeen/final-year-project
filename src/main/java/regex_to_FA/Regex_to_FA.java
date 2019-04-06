package regex_to_fa;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import model.Regex_Converter_Panel;
import toolbox.Finite_Automaton;
import toolbox.Tree_Node;
import toolbox.Tree_Node.Concat_Node;
import toolbox.Tree_Node.Leaf_Node;
import toolbox.Tree_Node.Star_Node;
import toolbox.Tree_Node.Union_Node;

/**
 * Performs the conversion from a regular expression to a finite automaton
 * @author Jaydene Green-Stevens
 *
 */
public class Regex_to_FA {

	private Map<Integer, Integer> bracketMap = new HashMap<Integer, Integer>();
	private char[] chars;
	
	
	public Regex_to_FA(Regex_Converter_Panel converter){
	}
	
	public Tree_Node convertToTree(String regex){
		return (new Tree_Builder(regex)).buildTree();
	}

	/**
	 * Generates the FA based on the tree node input
	 * @param node - parent node in the tree
	 * @return the automaton
	 */
	public Finite_Automaton generateFA(Tree_Node node) {

		// take in the root of the tree and generate the FA
		Automaton_Builder builder = new Automaton_Builder();

		if (node instanceof Leaf_Node) {
			return builder.buildSimpleAutomaton((Leaf_Node) node);
			
		} else {
			// not a leaf node

			if (node.getLeftChild() != null) {
				Finite_Automaton leftFA = generateFA(node.getLeftChild());

				if (node instanceof Star_Node) {
					
					Finite_Automaton fa = builder.addStarOperator(leftFA);
					return fa;
					
				} else {

					if (node.getRightChild() != null) {
						Finite_Automaton rightFA = generateFA(node.getRightChild());

						if (node instanceof Union_Node) {
							// use rule 3 to connect child NFAs
							Finite_Automaton fa = builder.combineWithUnion(leftFA, rightFA);
							return fa;
							
						} else if (node instanceof Concat_Node) {
							// use rule 4 to connect child NFAs
							Finite_Automaton fa = builder.combineWithConcat(leftFA, rightFA);
							return fa;
						}
					}
				}
			}
			// the root node is none of the above - problem
			return null;
		}
	}

	/**
	 * Checks to see whether the input regex is valid
	 * @param regex
	 * @return the error message, or an empty string if the regex is valid
	 */
	public String validate(String regex) {
		chars = regex.toCharArray();
		boolean bracketsMatch = validateBrackets(regex);

		if (bracketsMatch == false) {
			return "Brackets do not match";
		}

		boolean validSymbols = validateSymbols(regex);
		if (validSymbols == false) {
			return "Symbols not valid";
		}

		boolean validKleene = validateKleeneStar(regex);
		if (validKleene == false) {
			return "Use of Kleene star * not valid";
		}

		boolean validUnion = validateUnion(regex);
		if (validUnion == false) {
			return "Use of union | not valid";
		}

		return "";
	}

	/**
	 * Validates the symbols used in the regex
	 * @param regex
	 * @return if the regex is valid
	 */
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

	/**
	 * Validates the use of brackets
	 * @param regex
	 * @return false if the brackets do not match
	 */
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
						// add the positions of the bracket pair to the map
						bracketMap.put(key, i);
					}
				}
			}
			if (stack.isEmpty()) {
				return true;
			}
		}
		// regex is not valid - too many opening brackets
		return false; 
	}

	/**
	 * Validates the use of the Kleene star in the regex
	 * @param regex
	 * @return false if use is not valid
	 */
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

	/**
	 * Validates the use of union within the regex
	 * @param regex
	 * @return false if use is not valid
	 */
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
