package backend;

import regex_to_FA.Automaton_Builder;
import regex_to_FA.FiniteAutomata;
import regex_to_FA.Regex_to_FA;
import regex_to_FA.TreeNode;
import regex_to_FA.TreeNode.LeafNode;

public class Main {
    public static void main( String[] args ){
    	
    	String regex = "(a|b)*|ab";
    	
    	// aab
    	// a | b
    	// (a|b)
    	// (a|b)a
    	// a*
    	// a*b
    	// (ab)*
    	// (ab)*a
    	// (ab)*a*
    	// (a|b)*
    	
    	Regex_to_FA converter = new Regex_to_FA();
//    	converter.convertToFA(regex);
    	
    	TreeNode node1 = new TreeNode.LeafNode("a");
    	TreeNode node2 = new TreeNode.LeafNode("b");
    	
    	Automaton_Builder builder = new Automaton_Builder();
    	FiniteAutomata FA = builder.buildSimpleAutomaton((LeafNode) node1);
    	FiniteAutomata FA2 = builder.buildSimpleAutomaton((LeafNode) node2);
//    	System.out.println(FA);
//    	System.out.println("\n");
    	
//    	FiniteAutomata FA3 = builder.combineWithUnion(FA, FA2);
//    	System.out.println(FA3);
    	
    	FiniteAutomata FA4 = builder.combineWithConcat(FA, FA2);
    	System.out.println(FA4);

//    	FiniteAutomata FA5 = builder.addStarOperator(FA);
//    	System.out.println(FA5);
    }
}
