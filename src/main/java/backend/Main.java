package backend;

import java.util.ArrayList;

import fa_to_regex.FA_to_Regex;
import regex_to_fa.Automaton_Builder;
import regex_to_fa.Regex_to_FA;
import toolbox.Finite_Automata;
import toolbox.State;
import toolbox.Tree_Node;
import toolbox.Tree_Node.LeafNode;

public class Main {
    public static void main( String[] args ){
    	
//    	String regex = "abc* | (ab)* a*";
    	
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
    	
//    	Regex_to_FA converter = new Regex_to_FA();
//    	converter.convertToFA(regex);
    	
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
    	
    	// *********************
    	
    	// (0) -a-> (1) -b-> (2)
    	State initial = new State("0");
    	initial.setInitial(true);
    	State simple = new State("1");
    	State finalState = new State("2");
    	finalState.setFinal(true);
    	
    	initial.addTransition(simple, "a");
    	simple.addTransition(finalState, "b");
    	
    	ArrayList<State> finalStates = new ArrayList<State>();
    	finalStates.add(finalState);
    	
    	ArrayList<State> states = new ArrayList<State>();
    	states.add(initial);
    	states.add(simple);
    	states.add(finalState);
    	
    	ArrayList<String> inputAlphabet = new ArrayList<String>();
    	inputAlphabet.add("a");
    	inputAlphabet.add("b");
    	
    	Finite_Automata fa = new Finite_Automata(initial, finalStates, states, inputAlphabet);
    	
    	FA_to_Regex converter = new FA_to_Regex();
    	String regex = converter.convert(fa);
    	
    	System.out.println("regex: " + regex);
    	
    }
}
