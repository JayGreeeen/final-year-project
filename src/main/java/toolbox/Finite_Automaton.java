package toolbox;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Object representing a finite automaton, containing states, transitions, and
 * an alphabet of symbols.
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class Finite_Automaton {

	private State initialState;
	private ArrayList<State> finalStates;
	private ArrayList<String> inputAlphabet;
	private ArrayList<State> states;

	public Finite_Automaton(State initialState, ArrayList<State> finalStates, ArrayList<State> states,
			ArrayList<String> inputAlphabet) {
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.states = states;
		this.inputAlphabet = inputAlphabet;
	}

	/**
	 * Removes the specified state from the automaton
	 * 
	 * @param state
	 *            to remove
	 */
	public void removeState(State state) {
		states.remove(state);
	}

	/**
	 * @return the input alphabet for the automaton
	 */
	public ArrayList<String> getInputAlphabet() {
		return inputAlphabet;
	}

	/**
	 * @return the number of sttaes in the automaton
	 */
	public int getStateCount() {
		return states.size();
	}

	/**
	 * @return the initial state of the automaton
	 */
	public State getInitialState() {
		return initialState;
	}

	/**
	 * @return the list of final states - there may be mroe than one
	 */
	public ArrayList<State> getFinalStates() {
		return finalStates;
	}

	/**
	 * @return the list of states in the automaton
	 */
	public ArrayList<State> getStates() {
		return states;
	}

	/**
	 * Provides a clean output for the automaton
	 */
	public String toString() {

		String transitionOutput = "";
		String headers = "\t";
		String values = "";

		for (int i = 0; i < states.size(); i++) {
			values += i + "\t";
			headers += i + "\t";
			for (int j = 0; j < states.size(); j++) {
				values += states.get(i).getTransitionsTo(states.get(j)) + "\t";
			}
			values += "\n";
		}
		headers += "\n";
		transitionOutput = "\n" + headers + values;

		return "States: " + states + "\nInitial state: " + initialState + "\nFinal state(s): " + finalStates
				+ "\nInput alphabet: " + inputAlphabet + "\nTransitions: " + transitionOutput;
	}

	/**
	 * @return a new automaton object with all the same information as the
	 *         current automaton
	 */
	public Finite_Automaton copy() {
		ArrayList<State> finalStatesList = new ArrayList<>();

		State initial = initialState;

		ArrayList<State> stateList = new ArrayList<>();
		for (State s : states) {
			State newState = new State(s.getLabel());

			if (s.isFinalState()) {
				newState.setFinal(true);
				finalStatesList.add(newState);
			}
			if (s.isInitialState()) {
				newState.setInitial(true);
				initial = newState;
			}

			stateList.add(newState);
		}

		for (State s : states) {

			State newState = getState(s.getLabel(), stateList);

			Map<State, ArrayList<String>> map = s.getTransitions();

			for (Entry<State, ArrayList<String>> e : map.entrySet()) {

				// State old = (State) e.getKey();
				State to = getState(((State) e.getKey()).getLabel(), stateList);

				ArrayList<String> transitions = (ArrayList<String>) e.getValue();

				for (String label : transitions) {
					newState.addTransition(to, label);
				}
			}
		}

		ArrayList<String> inputList = new ArrayList<>();
		inputList.addAll(inputAlphabet);

		return new Finite_Automaton(initial, finalStatesList, stateList, inputList);
	}

	/**
	 * Finds a state object in the list of states based on its label
	 * 
	 * @param label
	 *            of the state
	 * @param states
	 *            list of states
	 * @return the state object
	 */
	private State getState(String label, ArrayList<State> states) {
		for (State state : states) {
			if (state.getLabel().equals(label)) {
				return state;
			}
		}
		return null;
	}

}
