package regex_to_fa;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;

import org.junit.Test;

import regex_to_fa.Regex_to_FA;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import toolbox.Finite_Automaton;
import toolbox.State;
import view.FA_Drawer;

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
		
		// *********
		Regex_to_FA converter = new Regex_to_FA(null);
		Finite_Automaton actualFA = null;
//		Finite_Automata actualFA = converter.convertToFA(regex);

		State actualInitial = actualFA.getInitialState();
		State actualFinal = actualFA.getFinalStates().get(0);

		assertEquals(initialLabel, actualInitial.getLabel());
		assertEquals(finalStateLabel, actualFinal.getLabel());
		assertEquals(numStates, actualFA.getStateCount());
		assertEquals(inputAlphabet, actualFA.getInputAlphabet());
		assertTrue(actualInitial.getTransitionsTo(actualFinal).get(0).equals("a"));
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
		Regex_to_FA converter = new Regex_to_FA(null);
		Finite_Automaton actualFA = null;
//		Finite_Automata actualFA = converter.convertToFA(regex);

		State actualInitial = actualFA.getInitialState();
		State actualFinal = actualFA.getFinalStates().get(0);
		State actualState1 = actualFA.getStates().get(1);
		State actualState2 = actualFA.getStates().get(2);
		State actualState3 = actualFA.getStates().get(3);

		assertEquals(initialLabel, actualInitial.getLabel());
		assertEquals(finalStateLabel, actualFinal.getLabel());
		assertEquals(numStates, actualFA.getStateCount());
		assertEquals(inputAlphabet, actualFA.getInputAlphabet());

		assertTrue(actualInitial.getTransitionsTo(actualState1).get(0).equals("ε"));
		assertTrue(actualState1.getTransitionsTo(actualState2).get(0).equals("a"));
		assertTrue(actualState2.getTransitionsTo(actualState3).get(0).equals("b"));
		assertTrue(actualState3.getTransitionsTo(actualFinal).get(0).equals("ε"));
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
		Regex_to_FA converter = new Regex_to_FA(null);
		Finite_Automaton actualFA = null;
//		Finite_Automata actualFA = converter.convertToFA(regex);
		
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

		assertTrue(actualInitial.getTransitionsTo(actualState1).get(0).equals("ε"));
		assertTrue(actualInitial.getTransitionsTo(actualState3).get(0).equals("ε"));
		assertTrue(actualState1.getTransitionsTo(actualState2).get(0).equals("a"));
		assertTrue(actualState2.getTransitionsTo(actualFinal).get(0).equals("ε"));
		assertTrue(actualState3.getTransitionsTo(actualState4).get(0).equals("b"));
		assertTrue(actualState4.getTransitionsTo(actualFinal).get(0).equals("ε"));
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
		Regex_to_FA converter = new Regex_to_FA(null);
		Finite_Automaton actualFA = null;
//		Finite_Automata actualFA = converter.convertToFA(regex);
		
		System.out.println(actualFA);

		State actualInitial = actualFA.getInitialState();
		State actualFinal = actualFA.getFinalStates().get(0);
		State actualState1 = actualFA.getStates().get(1);
		State actualState2 = actualFA.getStates().get(2);

		assertEquals(initialLabel, actualInitial.getLabel());
		assertEquals(finalStateLabel, actualFinal.getLabel());
		assertEquals(numStates, actualFA.getStateCount());
		assertEquals(inputAlphabet, actualFA.getInputAlphabet());

		assertTrue(actualInitial.getTransitionsTo(actualState1).get(0).equals("ε"));
		assertTrue(actualInitial.getTransitionsTo(actualFinal).get(0).equals("ε"));
		assertTrue(actualState1.getTransitionsTo(actualState2).get(0).equals("a"));
		assertTrue(actualState2.getTransitionsTo(actualFinal).get(0).equals("ε"));
		assertTrue(actualState2.getTransitionsTo(actualState1).get(0).equals("ε"));
	}

}
