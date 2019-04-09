package regex_to_fa;

import org.junit.Test;

import regex_to_fa.Automaton_Builder;
import toolbox.Finite_Automaton;
import toolbox.State;
import toolbox.Tree_Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

public class Automaton_Builder_Test {
	private Automaton_Builder builder = new Automaton_Builder();
	private ArrayList<String> inputAlphabet = new ArrayList<String>();
	private String initialLabel = "0";

	// testing from tree to FA

	@Test
	public void simpleFA() {
		/*-
		 * 	a 
		 * 
		 * (0) -a-> (1)
		 */

		String label = "a";
		String finalLabel = "1";
		inputAlphabet.add(label);
		int stateCount = 2;

		Tree_Node.Leaf_Node node = new Tree_Node.Leaf_Node(label);

		Finite_Automaton actual = builder.buildSimpleAutomaton(node);
		
		State initial = actual.getInitialState();
		assertEquals(stateCount, actual.getStateCount());
		assertEquals(initialLabel, initial.getLabel());
		assertEquals(inputAlphabet, actual.getInputAlphabet());
		
		assertTrue(actual.getFinalStates().size() == 1);
		State finalState = actual.getFinalStates().get(0);

		assertEquals(finalLabel, finalState.getLabel());
		assertTrue(initial.getTransitionsTo(finalState).get(0) == "a");
	}

	@Test
	public void concatFA() {
		/*-
		 * 		•
		 * 	   / \
		 *    a   b
		 * 
		 * (0) -a-> (1)
		 * (0) -b-> (1)
		 * 
		 * (0) -e-> (1) -a-> (2) -b-> (3) -e-> (4)
		 * 
		 */
		String label1 = "a";
		String label2 = "b";
		Tree_Node.Leaf_Node node1 = new Tree_Node.Leaf_Node(label1);
		Tree_Node.Leaf_Node node2 = new Tree_Node.Leaf_Node(label2);
		
		Finite_Automaton FA1 = builder.buildSimpleAutomaton(node1);
		Finite_Automaton FA2 = builder.buildSimpleAutomaton(node2);
		
		Finite_Automaton actual = builder.combineWithConcat(FA1, FA2);

		String finalLabel = "4";
		inputAlphabet.add(label1);
		inputAlphabet.add(label2);
		int stateCount = 5;

		assertEquals(stateCount, actual.getStateCount());

		State initial = actual.getInitialState();
		State state1 = actual.getStates().get(1);
		State state2 = actual.getStates().get(2);
		State state3 = actual.getStates().get(3);
		
		assertEquals(initialLabel, initial.getLabel());
		assertEquals(inputAlphabet, actual.getInputAlphabet());
		
		assertTrue(actual.getFinalStates().size() == 1);
		State finalState = actual.getFinalStates().get(0);

		assertEquals(finalLabel, finalState.getLabel());
		assertTrue(initial.getTransitionsTo(state1).get(0) == "ε");
		assertTrue(state1.getTransitionsTo(state2).get(0) == "a");
		assertTrue(state2.getTransitionsTo(state3).get(0) == "b");
		assertTrue(state3.getTransitionsTo(finalState).get(0) == "ε");
	}

	@Test
	public void unionFA() {
		/*-
		 * 		|
		 * 	   / \
		 *    a   b
		 * 
		 * (0) -a-> (1)
		 * (0) -b-> (1)
		 * 
		 * 			  -> (1) -a-> (2) 
		 * 		  e /				  \ e
		 *  (0) --	                   - > (5)
		 *  	  e \				  / e
		 *  		  -> (3) -b-> (4) 
		 * 
		 */
		String label1 = "a";
		String label2 = "b";
		Tree_Node.Leaf_Node node1 = new Tree_Node.Leaf_Node(label1);
		Tree_Node.Leaf_Node node2 = new Tree_Node.Leaf_Node(label2);
		
		Finite_Automaton FA1 = builder.buildSimpleAutomaton(node1);
		Finite_Automaton FA2 = builder.buildSimpleAutomaton(node2);
		
		Finite_Automaton actual = builder.combineWithUnion(FA1, FA2);

		String finalLabel = "5";
		inputAlphabet.add(label1);
		inputAlphabet.add(label2);
		int stateCount = 6;

		assertEquals(stateCount, actual.getStateCount());

		State initial = actual.getInitialState();
		State state1 = actual.getStates().get(1);
		State state2 = actual.getStates().get(2);
		State state3 = actual.getStates().get(3);
		State state4 = actual.getStates().get(4);
		
		assertEquals(initialLabel, initial.getLabel());
		assertEquals(inputAlphabet, actual.getInputAlphabet());
		
		assertTrue(actual.getFinalStates().size() == 1);
		State finalState = actual.getFinalStates().get(0);

		assertEquals(finalLabel, finalState.getLabel());
		assertTrue(initial.getTransitionsTo(state1).get(0) == "ε");
		assertTrue(initial.getTransitionsTo(state3).get(0) == "ε");
		assertTrue(state1.getTransitionsTo(state2).get(0) == "a");
		assertTrue(state2.getTransitionsTo(finalState).get(0) == "ε");
		assertTrue(state3.getTransitionsTo(state4).get(0) == "b");
		assertTrue(state4.getTransitionsTo(finalState).get(0) == "ε");
	}
	
	@Test
	public void starFA() {
		/*-
		 *    *
		 * 	  |
		 *    a  
		 * 
		 * (0) -a-> (1)
		 * 
		 *  (0) -e-> (1) -a-> (2) -e-> (3)
		 *   \		  ^		   /		^
		 *    \		   \ - e -         /
		 *     - - - - - - e - - - - - 
		 * 
		 */
		String label1 = "a";
		inputAlphabet.add(label1);
		
		Tree_Node.Leaf_Node node1 = new Tree_Node.Leaf_Node(label1);
		Finite_Automaton FA1 = builder.buildSimpleAutomaton(node1);
		Finite_Automaton actual = builder.addStarOperator(FA1);
		
		String finalLabel = "3";
		int stateCount = 4;
		
		assertEquals(stateCount, actual.getStateCount());
		
		State initial = actual.getInitialState();
		State state1 = actual.getStates().get(1);
		State state2 = actual.getStates().get(2);
		
		assertEquals(initialLabel, initial.getLabel());
		assertEquals(inputAlphabet, actual.getInputAlphabet());
		
		assertTrue(actual.getFinalStates().size() == 1);
		State finalState = actual.getFinalStates().get(0);
		
		assertEquals(finalLabel, finalState.getLabel());
		assertTrue(initial.getTransitionsTo(state1).get(0) == "ε");
		assertTrue(initial.getTransitionsTo(finalState).get(0) == "ε");
		assertTrue(state1.getTransitionsTo(state2).get(0) == "a");
		assertTrue(state2.getTransitionsTo(state1).get(0) == "ε");
		assertTrue(state2.getTransitionsTo(finalState).get(0) == "ε");
	}
	
	
	
	@Test
	public void unionStarFA() {
		/*-
		 * (a|b)* 
		 * 
		 * 		*
		 * 		|
		 * 		|
		 * 	   / \
		 *    a   b
		 * 
		 * (0) -a-> (1)
		 * (0) -b-> (1)
		 * 
		 * 			  -> (1) -a-> (2) 
		 * 		  e /				  \ e
		 *  (0) --	                   - > (5)
		 *  	  e \				  / e
		 *  		  -> (3) -b-> (4) 
		 *  
		 *  
		 *  
		 * 			         -> (2) -a-> (3) 
		 * 		          e /				  \ e
		 *  (0) -e-> (1) --	                   - > (6) -e-> (7)
		 *   \	      ^    e \				  / e   /        ^
		 *    \		   \      -> (4) -b-> (5)      /        /
		 *     \        \  						  /        /
		 *      \         - - - - - - e - - - - -         /
		 *       \    									 /
		 *        - - - - - - - - - e - - - - - - - - - -
		 * 
		 */
		String label1 = "a";
		String label2 = "b";
		inputAlphabet.add(label1);
		inputAlphabet.add(label2);
		
		Tree_Node.Leaf_Node node1 = new Tree_Node.Leaf_Node(label1);
		Tree_Node.Leaf_Node node2 = new Tree_Node.Leaf_Node(label2);
		
		Finite_Automaton FA1 = builder.buildSimpleAutomaton(node1);
		Finite_Automaton FA2 = builder.buildSimpleAutomaton(node2);
		
		Finite_Automaton union = builder.combineWithUnion(FA1, FA2);
		Finite_Automaton star = builder.addStarOperator(union);
		
		String finalLabel = "7";
		int stateCount = 8;
		
		assertEquals(stateCount, star.getStateCount());
		
		State initial = star.getInitialState();
		State state1 = star.getStates().get(1);
		State state2 = star.getStates().get(2);
		State state3 = star.getStates().get(3);
		State state4 = star.getStates().get(4);
		State state5 = star.getStates().get(5);
		State state6 = star.getStates().get(6);
		
		assertEquals(initialLabel, initial.getLabel());
		assertEquals(inputAlphabet, star.getInputAlphabet());
		
		assertTrue(star.getFinalStates().size() == 1);
		State finalState = star.getFinalStates().get(0);
		
		assertEquals(finalLabel, finalState.getLabel());
		assertTrue(initial.getTransitionsTo(state1).get(0) == "ε");
		assertTrue(initial.getTransitionsTo(finalState).get(0) == "ε");
		assertTrue(state1.getTransitionsTo(state2).get(0) == "ε");
		assertTrue(state1.getTransitionsTo(state4).get(0) == "ε");
		assertTrue(state2.getTransitionsTo(state3).get(0) == "a");
		assertTrue(state3.getTransitionsTo(state6).get(0) == "ε");
		assertTrue(state4.getTransitionsTo(state5).get(0) == "b");
		assertTrue(state5.getTransitionsTo(state6).get(0) == "ε");
		assertTrue(state6.getTransitionsTo(state1).get(0) == "ε");
		assertTrue(state6.getTransitionsTo(finalState).get(0) == "ε");
	}

}
