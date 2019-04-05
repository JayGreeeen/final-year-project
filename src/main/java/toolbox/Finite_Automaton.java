package toolbox;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Finite_Automaton {

	private State initialState;
	private ArrayList<State> finalStates;
	private ArrayList<String> inputAlphabet;
	private ArrayList<State> states;
	// private TransitionTable transitions;

	public Finite_Automaton(State initialState, ArrayList<State> finalStates, ArrayList<State> states,
			ArrayList<String> inputAlphabet) {
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.states = states;
		this.inputAlphabet = inputAlphabet;
		// this.transitions = transitions;
	}

	public void removeState(State state) {
		states.remove(state);
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
				values += states.get(i).getTransitionsTo(states.get(j)) + "\t";
			}
			values += "\n";
		}
		headers += "\n";
		transitionOutput = "\n" + headers + values;

		return "States: " + states + "\nInitial state: " + initialState + "\nFinal state(s): " + finalStates
				+ "\nInput alphabet: " + inputAlphabet + "\nTransitions: " + transitionOutput;
	}

	public Finite_Automaton copy() {
		ArrayList<State> finalStatesList = new ArrayList<>();
//		finalStatesList.addAll(finalStates);
		
		State initial = initialState;

		ArrayList<State> stateList = new ArrayList<>();
		for (State s : states){
			State newState = new State(s.getLabel());
			
			if (s.isFinalState()){
				newState.setFinal(true);
				finalStatesList.add(newState);
			}
			if (s.isInitialState()){
				newState.setInitial(true);
				initial = newState;
			}
			
			stateList.add(newState);
		}
		
		for (State s : states){
			
			// need to first make the list, and then go through and add all the transitions to it
			// pretty sure have done this once in the class that builds the FA from the front end
			// so check that
			
			State newState = getState(s.getLabel(), stateList);
			
			Map<State, ArrayList<String>> map = s.getTransitions();
			
			for (Entry<State, ArrayList<String>> e : map.entrySet()){
				
//				State old = (State) e.getKey();
				State to = getState(((State) e.getKey()).getLabel(), stateList);
				
				ArrayList<String> transitions = (ArrayList<String>) e.getValue();
				
				for (String label : transitions){
					newState.addTransition(to, label);
				}
			}
		}
		
		ArrayList<String> inputList = new ArrayList<>();
		inputList.addAll(inputAlphabet);

		
		return new Finite_Automaton(initial, finalStatesList, stateList, inputList);
	}
	
	private State getState(String label, ArrayList<State> states) {
		for (State state : states) {
			if (state.getLabel().equals(label)) {
				return state;
			}
		}
		return null;
	}

}
