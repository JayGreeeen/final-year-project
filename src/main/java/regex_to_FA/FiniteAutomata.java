package regex_to_FA;

import java.util.ArrayList;

public class FiniteAutomata {

	private State initialState;
	private ArrayList<State> finalStates;
	private ArrayList<String> inputAlphabet;
	private ArrayList<State> states;
	// private TransitionTable transitions;


	public FiniteAutomata(State initialState, ArrayList<State> finalStates, ArrayList<State> states,
			ArrayList<String> inputAlphabet) {
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.states = states;
		this.inputAlphabet = inputAlphabet;
		// this.transitions = transitions;
	}

	public ArrayList<String> getInputAlphabet() {
		return inputAlphabet;
	}

	public int getStateCount() {
		return states.size();
	}

	public State getInitialState() {
		return initialState;
	}

	public ArrayList<State> getFinalStates() {
		return finalStates;
	}

	public ArrayList<State> getStates() {
		return states;
	}

	public String toString() {

		String transitionOutput = "";
		String headers = "\t";
		String values = "";

		for (int i = 0; i < states.size(); i++) {
			values += i + "\t";
			headers += i + "\t";
			for (int j = 0; j < states.size(); j++) {
				values += states.get(i).hasTransitionTo(states.get(j)) + "\t";
			}
			values += "\n";
		}
		headers += "\n";
		transitionOutput = "\n" + headers + values;

		return "States: " + states + "\nInitial state: " + initialState + "\nFinal state(s): " + finalStates
				+ "\nInput alphabet: " + inputAlphabet + "\nTransitions: " + transitionOutput;
	}

}
