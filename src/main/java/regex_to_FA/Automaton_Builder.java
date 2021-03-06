package regex_to_fa;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import toolbox.Finite_Automaton;
import toolbox.State;
import toolbox.Tree_Node.Leaf_Node;

/**
 * Builds a finite automaton based on a given Tree_Node input
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class Automaton_Builder {

	public Automaton_Builder() {
	}

	/**
	 * Creates an automaton for a leaf node. Initial and final state with a
	 * single transition, labelled with contents of leaf node
	 * 
	 * @param node
	 *            to build the automaton for
	 * @return the automaton
	 */
	public Finite_Automaton buildSimpleAutomaton(Leaf_Node node) {
		String label = node.getText();

		// initial state
		State initialState = new State("0");
		initialState.setInitial(true);

		// final state
		State finalState = new State("1");
		finalState.setFinal(true);
		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		// input alphabet
		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add(label);

		// states
		ArrayList<State> states = new ArrayList<State>();
		states.add(initialState);
		states.add(finalState);

		// transitions
		initialState.addTransition(finalState, label);

		return new Finite_Automaton(initialState, finalStates, states, inputAlphabet);
	}

	/**
	 * Combines two automata using the union operator '|' - the third rule of
	 * Thompson's construction algorithm
	 * 
	 * @param FA1
	 *            the first automaton
	 * @param FA2
	 *            the second automaton
	 * @return the combined outcome of the two
	 */
	public Finite_Automaton combineWithUnion(Finite_Automaton FA1, Finite_Automaton FA2) {
		State initialState = createNewInitialState();

		int totalStateCount = FA1.getStateCount() + FA2.getStateCount();
		State finalState = createNewFinalState(totalStateCount);

		ArrayList<State> states = renameStates(FA1.getStates(), FA2.getStates());
		states.add(0, initialState);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		// set up FA1
		State initialFA1 = FA1.getInitialState();
		initialFA1.setInitial(false);
		initialState.addEmptyTransition(initialFA1);

		State finalFA1 = FA1.getFinalStates().get(0); // only 1 final state
		finalFA1.setFinal(false);
		finalFA1.addEmptyTransition(finalState);

		// set up FA2
		State initialFA2 = FA2.getInitialState();
		initialFA2.setInitial(false);
		initialState.addEmptyTransition(initialFA2);

		State finalFA2 = FA2.getFinalStates().get(0); // only 1 final state
		finalFA2.setFinal(false);
		finalFA2.addEmptyTransition(finalState);

		// input alphabet
		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.addAll(FA1.getInputAlphabet());
		inputAlphabet.addAll(FA2.getInputAlphabet());

		return new Finite_Automaton(initialState, finalStates, states, inputAlphabet);
	}

	/**
	 * Combines two automata using concatenation, '•' - the fourth rule of
	 * Thompson's construction algorithm
	 * 
	 * @param FA1
	 *            the first automaton
	 * @param FA2
	 *            the second automaton
	 * @return the outcome of the combination
	 */
	public Finite_Automaton combineWithConcat(Finite_Automaton FA1, Finite_Automaton FA2) {
		State initialState = createNewInitialState();

		State initialStateFA1 = FA1.getInitialState();
		State finalStateFA1 = FA1.getFinalStates().get(0);

		State initialStateFA2 = FA2.getInitialState();
		State finalStateFA2 = FA2.getFinalStates().get(0);

		// combine final state of FA1 and initial state of FA2
		combineStates(finalStateFA1, initialStateFA2);

		// remove initial state of FA2 from set
		ArrayList<State> FA2States = FA2.getStates();
		FA2States.remove(initialStateFA2);

		ArrayList<State> states = renameStates(FA1.getStates(), FA2States);

		State finalState = createNewFinalState(states.size());
		states.add(0, initialState);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		initialStateFA1.setInitial(false);
		initialState.addEmptyTransition(initialStateFA1);
		finalStateFA1.setFinal(false);

		finalStateFA2.setFinal(false);
		finalStateFA2.addEmptyTransition(finalState);

		// input alphabet
		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.addAll(FA1.getInputAlphabet());
		inputAlphabet.addAll(FA2.getInputAlphabet());

		return new Finite_Automaton(initialState, finalStates, states, inputAlphabet);
	}

	/**
	 * Combines two states into one state. Adds all of the second states
	 * transitions onto the first state
	 * 
	 * @param state1
	 *            first state which acquires new transitions
	 * @param state2
	 *            the state to remove
	 * @return the state with all transitions
	 */
	private State combineStates(State state1, State state2) {
		Map<State, ArrayList<String>> transitions = state2.getTransitions();

		for (Entry<State, ArrayList<String>> transition : transitions.entrySet()) {
			String label = transition.getValue().get(0); // should only be 1
															// transition
			state1.addTransition(transition.getKey(), label);
		}
		return state1;
	}

	/**
	 * Adds the star operator to an automaton, '*' - Thompson's fifth rule of
	 * construction
	 * 
	 * @param FA
	 *            the automaton to add the star to
	 * @return the new automaton
	 */
	public Finite_Automaton addStarOperator(Finite_Automaton FA) {
		State initialState = createNewInitialState();
		State finalState = createNewFinalState(FA.getStateCount());

		ArrayList<State> states = renameStates(FA.getStates());
		states.add(0, initialState);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		State oldInitial = FA.getInitialState();
		oldInitial.setInitial(false);
		State oldFinal = FA.getFinalStates().get(0);
		oldFinal.setFinal(false);

		initialState.addEmptyTransition(oldInitial);
		initialState.addEmptyTransition(finalState);

		oldFinal.addEmptyTransition(finalState);
		oldFinal.addEmptyTransition(oldInitial);

		return new Finite_Automaton(initialState, finalStates, states, FA.getInputAlphabet());
	}

	/**
	 * Takes in two lists of states, and combines them into a single list,
	 * renaming them from '1' to the total number of states. Prevents duplicate
	 * names when combing lists.
	 * 
	 * @param statesSet1
	 *            first set of states
	 * @param statesSet2
	 *            second set of states
	 * @return the single list of renamed states
	 */
	private ArrayList<State> renameStates(ArrayList<State> statesSet1, ArrayList<State> statesSet2) {
		ArrayList<State> states = new ArrayList<State>();
		states.addAll(statesSet1);
		states.addAll(statesSet2);

		int counter = 1; // starts from 1, b/c a new initial state has 0
		for (State state : states) {
			state.renameState(Integer.toString(counter));
			counter++;
		}
		return states;
	}

	/**
	 * Takes in a list of states and renames them from 1 to the total count
	 * 
	 * @param states
	 *            to rename
	 * @return the list of renamed states
	 */
	private ArrayList<State> renameStates(ArrayList<State> states) {
		int counter = 1; // starts from 1, b/c a new initial state has 0
		for (State state : states) {
			state.renameState(Integer.toString(counter));
			counter++;
		}
		return states;
	}

	/**
	 * Creates a new initial state
	 * 
	 * @return the new state
	 */
	private State createNewInitialState() {
		State initial = new State("0");
		initial.setInitial(true);
		return initial;
	}

	/**
	 * Creates a new final state with the specified label number
	 * 
	 * @param totalStateCount
	 *            the label to be used for the state
	 * @return the new final state
	 */
	private State createNewFinalState(int totalStateCount) {
		State finalState = new State(Integer.toString(totalStateCount + 1));
		finalState.setFinal(true);
		return finalState;
	}
}
