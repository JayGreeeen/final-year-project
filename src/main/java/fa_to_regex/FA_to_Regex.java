package fa_to_regex;

import java.util.ArrayList;

import toolbox.Finite_Automaton;
import toolbox.State;

/**
 * Aids in conversion from FA to Regex
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class FA_to_Regex {

	public FA_to_Regex() {

	}

	/**
	 * Adds a new final state by adding empty transition jumps from all the
	 * existing final states to the new final states
	 * 
	 * @param finalStates
	 *            the list of existing final states
	 * @param finalState
	 *            the new final state
	 */
	private void createNewFinalState(ArrayList<State> finalStates, State finalState) {
		for (State state : finalStates) {
			state.setFinal(false);
			state.addEmptyTransition(finalState);
		}

	}

	/**
	 * Returns the list of states which can be removed. Does not include the
	 * initial or final state
	 * 
	 * @param fa
	 *            the automaton to get the list of states for
	 * @return the states to be removed from the automaton
	 */
	public ArrayList<State> getStatesToRemove(Finite_Automaton fa) {
		ArrayList<State> states = new ArrayList<State>();
		states.addAll(fa.getStates());

		State initial = fa.getInitialState();
		State finalState;

		states.remove(initial);

		finalState = fa.getFinalStates().get(0);
		states.remove(finalState);

		return states;
	}

	/**
	 * Creates a new final state for the specified automaton
	 * 
	 * @param fa to create the new final state for 
	 * @return the automaton
	 */
	public Finite_Automaton createNewFinalState(Finite_Automaton fa) {
		ArrayList<State> finalStates = fa.getFinalStates();
		
		// state count might be 3, but may be labelled 1-3
		State finalState = new State("end");

		finalState.setFinal(true);

		createNewFinalState(finalStates, finalState);

		ArrayList<State> states = fa.getStates();
		states.add(finalState);

		finalStates = new ArrayList<>();
		finalStates.add(finalState);
		fa = new Finite_Automaton(fa.getInitialState(), finalStates, states, fa.getInputAlphabet());
		// adds the new final state to the automaton

		return fa;
	}

}
