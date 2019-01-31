package backend;

import regex_to_FA.Automaton_Builder;
import regex_to_FA.Finite_Automata;
import regex_to_FA.Regex_to_FA;
import regex_to_FA.Tree_Node;
import regex_to_FA.Tree_Node.LeafNode;

public class Main {
    public static void main( String[] args ){
    	
    	String regex = "abc* | (ab)* a*";
    	
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
    	converter.convertToFA(regex);
    	
//    	Tree_Node node1 = new Tree_Node.LeafNode("a");
//    	Tree_Node node2 = new Tree_Node.LeafNode("b");
    	
//    	Automaton_Builder builder = new Automaton_Builder();
//    	Finite_Automata FA = builder.buildSimpleAutomaton((LeafNode) node1);
//    	Finite_Automata FA2 = builder.buildSimpleAutomaton((LeafNode) node2);
//    	System.out.println(FA);
//    	System.out.println("\n");
    	
//    	FiniteAutomata FA3 = builder.combineWithUnion(FA, FA2);
//    	System.out.println(FA3);
    	
//    	Finite_Automata FA4 = builder.combineWithConcat(FA, FA2);
//    	System.out.println(FA4);

//    	FiniteAutomata FA5 = builder.addStarOperator(FA);
//    	System.out.println(FA5);
    }
}
