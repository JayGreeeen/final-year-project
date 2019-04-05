package fa_to_regex;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import toolbox.Finite_Automaton;
import toolbox.State;

public class FA_to_Regex_Test {
	// put in an FA and make sure the correct regex comes out
	private FA_to_Regex converter = new FA_to_Regex();

	@Test
	public void simple() {
		/*-
		 * (0) -a-> (1)
		 * 
		 * regex: a
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State finalState = new State("1");
		finalState.setFinal(true);

		initial.addTransition(finalState, "a");

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("a", regex);
	}

	@Test
	public void concatenation() {
		/*-
		 * (0) -a-> (1) -b-> (2)
		 * 
		 * regex: ab
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State state1 = new State("1");
		State finalState = new State("2");
		finalState.setFinal(true);

		initial.addTransition(state1, "a");
		state1.addTransition(finalState, "b");

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(state1);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");
		inputAlphabet.add("b");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("ab", regex);
	}

	@Test
	public void concatenationWithEmptyJumps() {
		/*-
		 * (0) -e-> (1) -a-> (2) -b-> (3) -e-> (4)
		 * 
		 * regex: ab
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State state1 = new State("1");
		State state2 = new State("2");
		State state3 = new State("3");
		State finalState = new State("4");
		finalState.setFinal(true);

		initial.addTransition(state1, "ε");
		state1.addTransition(state2, "a");
		state2.addTransition(state3, "b");
		state3.addTransition(finalState, "ε");

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(state1);
		states.add(state2);
		states.add(state3);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");
		inputAlphabet.add("b");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("εabε", regex);
	}

	@Test
	public void union() {
		/*-
		 *      -  a  -
		 *    /		    \
		 *  (0)        (1)
		 *   \ 			/
		 *     -   b  -
		 * 
		 * regex: a|b
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State finalState = new State("1");
		finalState.setFinal(true);

		initial.addTransition(finalState, "a");
		initial.addTransition(finalState, "b");

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");
		inputAlphabet.add("b");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("(a|b)", regex);
	}

	@Test
	public void unionWithEmptyJumps() {
		/*-
		 * 			  -> (1) -a-> (2) 
		 * 		  e /				  \ e
		 *  (0) --	                   - > (5)
		 *  	  e \				  / e
		 *  		  -> (3) -b-> (4) 
		 * 
		 * regex: εaε | εbε
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State state1 = new State("1");
		State state2 = new State("2");
		State state3 = new State("3");
		State state4 = new State("4");
		State finalState = new State("5");
		finalState.setFinal(true);

		initial.addEmptyTransition(state1);
		initial.addEmptyTransition(state3);
		state1.addTransition(state2, "a");
		state2.addEmptyTransition(finalState);
		state3.addTransition(state4, "b");
		state4.addEmptyTransition(finalState);

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(state1);
		states.add(state2);
		states.add(state3);
		states.add(state4);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");
		inputAlphabet.add("b");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("(εaε|εbε)", regex);
	}

	@Test
	public void star() {
		/*-
		 *      _
		 *    /   \ 
		 *  (0)    a  
		 *   ^     |
		 *    \ _ /
		 * 	
		 * regex: a*
		 */

		State state = new State("0");

		state.addTransition(state, "a");

		ArrayList<State> states = new ArrayList<State>();
		states.add(state);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(state);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");

		Finite_Automaton fa = new Finite_Automaton(state, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("a*", regex);
	}

	@Test
	public void starWithEmptyJumps() {
		/*-
		 *  (0) -e-> (1) -a-> (2) -e-> (3)
		 *   \		  ^		   /		^
		 *    \		   \ - e -         /
		 *     - - - - - - e - - - - - 
		 * 
		 * regex: a*
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State state1 = new State("1");
		State state2 = new State("2");
		State finalState = new State("3");
		finalState.setFinal(true);

		initial.addEmptyTransition(state1);
		initial.addEmptyTransition(finalState);
		state1.addTransition(state2, "a");
		state2.addEmptyTransition(state1);
		state2.addEmptyTransition(finalState);

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(state1);
		states.add(state2);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
		String regex = "";
//		String regex = converter.convert(fa);

		assertEquals("(ε|εa(εa)*ε)", regex);
	}

	@Test
	public void complexExample1() {
		/*-
		  * 		  	  (2) 
		 * 			     /	 \	
		 * 			    a     b
		 * 			   /	   \
		 *  (0) -a-> (1)	   (4)   
		 *  	  	   \	   /	
		 *  		    b	  b
		 *  			 \   /
		 *  		      (3) 
		 * 
		 * regex: aab|abb
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State state1 = new State("1");
		State state2 = new State("2");
		State state3 = new State("3");
		State finalState = new State("4");
		finalState.setFinal(true);

		initial.addTransition(state1, "a");
		state1.addTransition(state2, "a");
		state1.addTransition(state3, "b");
		state2.addTransition(finalState, "b");
		state3.addTransition(finalState, "b");

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(state1);
		states.add(state2);
		states.add(state3);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");
		inputAlphabet.add("b");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("(aab|abb)", regex);
	}

	@Test
	public void complexExample2() {
		/*-
		 * -a-		-b-		 -a-
		 * | |		| |		 | |
		 * (0) -a-> (1) -b-> (2) 
		 * 
		 * regex: a*ab*ba*
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State state1 = new State("1");
		State finalState = new State("2");
		finalState.setFinal(true);

		initial.addTransition(state1, "a");
		initial.addTransition(initial, "a");
		state1.addTransition(state1, "b");
		state1.addTransition(finalState, "b");
		finalState.addTransition(finalState, "a");

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(state1);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");
		inputAlphabet.add("b");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";

		assertEquals("a*ab*ba*", regex);
	}

	@Test
	public void complexExample3() {
		/*-
		 *  - - - - - - a - - - - - - 
		 * | 						  |
		 * |  -a-	   -c-		 -a-  |
		 * |  | |	   | |		 | |  |
		 * -- (0) -a-> (1) -c-> (2) < -  
		 *     ^  <-b-     <-b-  |
		 * 	   |				 |
		 *      - - - - c - - - -
		 * 
		 * regex: 
		 */

		State initial = new State("0");
		initial.setInitial(true);
		State state1 = new State("1");
		State finalState = new State("2");
		finalState.setFinal(true);

		initial.addTransition(initial, "a");
		initial.addTransition(state1, "a");
		initial.addTransition(finalState, "a");

		state1.addTransition(state1, "c");
		state1.addTransition(initial, "b");
		state1.addTransition(finalState, "c");

		finalState.addTransition(finalState, "a");
		finalState.addTransition(initial, "c");
		finalState.addTransition(state1, "b");

		ArrayList<State> states = new ArrayList<State>();
		states.add(initial);
		states.add(state1);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add("a");
		inputAlphabet.add("b");
		inputAlphabet.add("c");

		Finite_Automaton fa = new Finite_Automaton(initial, finalStates, states, inputAlphabet);
//		String regex = converter.convert(fa);
		String regex = "";
		
		System.out.println("actual: " + regex);

		String expected = "(a | ac*b)* (a | ac*c) (a | bc*c)*| ( (a | ac*b)* (a | ac*c) "
				+ "(a | bc*c)* (a | bc*c)* (c | bc*b) (a | ac*b)* (a | ac*b)* (a | ac*c) (a | bc*c)* ) *";
		expected = expected.replace(" ", "");
		System.out.println("expected " + expected);
		assertEquals(expected, regex);
	}
}
