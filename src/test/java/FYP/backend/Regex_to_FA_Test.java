package FYP.backend;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import regex_to_FA.Finite_Automata;
import regex_to_FA.Regex_to_FA;
import regex_to_FA.State;

public class Regex_to_FA_Test {
	private ArrayList<String> inputAlphabet = new ArrayList<String>();
	private String initialLabel = "0";

	@Test
	public void singleLetter() {
		/*-
		 *  (0) -a-> (1)
		 */
		// takes in a regex "a", produces an FA

		String finalStateLabel = "1";
		int numStates = 2;
		inputAlphabet.add("a");

		String regex = "a";
		Regex_to_FA converter = new Regex_to_FA();
		Finite_Automata actualFA = converter.convertToFA(regex);

		State actualInitial = actualFA.getInitialState();
		State actualFinal = actualFA.getFinalStates().get(0);

		assertEquals(initialLabel, actualInitial.getLabel());
		assertEquals(finalStateLabel, actualFinal.getLabel());
		assertEquals(numStates, actualFA.getStateCount());
		assertEquals(inputAlphabet, actualFA.getInputAlphabet());
		assertTrue(actualInitial.getTransitionTo(actualFinal).equals("a"));
	}

	@Test
	public void concatenation() {
		/*-
		 *  (0) -a-> (1)
		 *  (0) -b-> (1)
		 *  
		 *  (0) -e-> (1) -a-> (2) -b-> (3) -e-> (4)
		 */
		// takes in a regex "ab", produces an FA

		String finalStateLabel = "4";
		int numStates = 5;

		String label1 = "a";
		String label2 = "b";
		inputAlphabet.add(label1);
		inputAlphabet.add(label2);

		String regex = "ab";
		Regex_to_FA converter = new Regex_to_FA();
		Finite_Automata actualFA = converter.convertToFA(regex);

		State actualInitial = actualFA.getInitialState();
		State actualFinal = actualFA.getFinalStates().get(0);
		State actualState1 = actualFA.getStates().get(1);
		State actualState2 = actualFA.getStates().get(2);
		State actualState3 = actualFA.getStates().get(3);

		assertEquals(initialLabel, actualInitial.getLabel());
		assertEquals(finalStateLabel, actualFinal.getLabel());
		assertEquals(numStates, actualFA.getStateCount());
		assertEquals(inputAlphabet, actualFA.getInputAlphabet());

		assertTrue(actualInitial.getTransitionTo(actualState1).equals("ε"));
		assertTrue(actualState1.getTransitionTo(actualState2).equals("a"));
		assertTrue(actualState2.getTransitionTo(actualState3).equals("b"));
		assertTrue(actualState3.getTransitionTo(actualFinal).equals("ε"));
	}

	@Test
	public void union() {
		/*-
		 *  (0) -a-> (1)
		 *  (0) -b-> (1)
		 *  
		 * 			  -> (1) -a-> (2) 
		 * 		  e /				  \ e
		 *  (0) --	                   - > (5)
		 *  	  e \				  / e
		 *  		  -> (3) -b-> (4) 
		 */
		// takes in a regex "a|b", produces an FA

		String label1 = "a";
		String label2 = "b";
		inputAlphabet.add(label1);
		inputAlphabet.add(label2);

		String finalStateLabel = "5";
		int numStates = 6;

		String regex = "a|b";
		Regex_to_FA converter = new Regex_to_FA();
		Finite_Automata actualFA = converter.convertToFA(regex);

		System.out.println(actualFA);

		State actualInitial = actualFA.getInitialState();
		State actualFinal = actualFA.getFinalStates().get(0);
		State actualState1 = actualFA.getStates().get(1);
		State actualState2 = actualFA.getStates().get(2);
		State actualState3 = actualFA.getStates().get(3);
		State actualState4 = actualFA.getStates().get(4);

		assertEquals(initialLabel, actualInitial.getLabel());
		assertEquals(finalStateLabel, actualFinal.getLabel());
		assertEquals(numStates, actualFA.getStateCount());
		assertEquals(inputAlphabet, actualFA.getInputAlphabet());

		assertTrue(actualInitial.getTransitionTo(actualState1).equals("ε"));
		assertTrue(actualInitial.getTransitionTo(actualState3).equals("ε"));
		assertTrue(actualState1.getTransitionTo(actualState2).equals("a"));
		assertTrue(actualState2.getTransitionTo(actualFinal).equals("ε"));
		assertTrue(actualState3.getTransitionTo(actualState4).equals("b"));
		assertTrue(actualState4.getTransitionTo(actualFinal).equals("ε"));
	}

	@Test
	public void star() {
		/*-
		 * (0) -a-> (1)
		 * 
		 *  (0) -e-> (1) -a-> (2) -e-> (3)
		 *   \		  ^		   /		^
		 *    \		   \ - e -         /
		 *     - - - - - - e - - - - - 
		 */
		// takes in a regex "a*", produces an FA

		String finalStateLabel = "3";
		int numStates = 4;

		String label1 = "a";
		inputAlphabet.add(label1);

		String regex = "a*";
		Regex_to_FA converter = new Regex_to_FA();
		Finite_Automata actualFA = converter.convertToFA(regex);

		System.out.println(actualFA);

		State actualInitial = actualFA.getInitialState();
		State actualFinal = actualFA.getFinalStates().get(0);
		State actualState1 = actualFA.getStates().get(1);
		State actualState2 = actualFA.getStates().get(2);

		assertEquals(initialLabel, actualInitial.getLabel());
		assertEquals(finalStateLabel, actualFinal.getLabel());
		assertEquals(numStates, actualFA.getStateCount());
		assertEquals(inputAlphabet, actualFA.getInputAlphabet());

		assertTrue(actualInitial.getTransitionTo(actualState1).equals("ε"));
		assertTrue(actualInitial.getTransitionTo(actualFinal).equals("ε"));
		assertTrue(actualState1.getTransitionTo(actualState2).equals("a"));
		assertTrue(actualState2.getTransitionTo(actualFinal).equals("ε"));
		assertTrue(actualState2.getTransitionTo(actualState1).equals("ε"));
	}

}
