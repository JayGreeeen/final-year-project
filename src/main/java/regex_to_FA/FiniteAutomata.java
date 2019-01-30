package regex_to_FA;

import java.util.ArrayList;

public class FiniteAutomata {

	private State initialState;
	private ArrayList<State> finalStates;

	private ArrayList<String> inputAlphabet;

//	private TransitionTable transitions;

	private ArrayList<State> states;

	// transition functions - matrix
	// set of states

	public FiniteAutomata(State initialState, ArrayList<State> finalStates, ArrayList<State> states,
			ArrayList<String> inputAlphabet) {
		this.initialState = initialState;
		this.finalStates = finalStates;
		this.states = states;
		this.inputAlphabet = inputAlphabet;
//		this.transitions = transitions;
	}

	// public void setInitialState(State state) {
	// initialState = state;
	// }

	// public void setFinalState(State state) {
	// finalState = state;
	// }

	// public void setTransitions(String[][] transitions) {
	// this.transitions = transitions;
	// }

	// public void addToInputAlphabet(String s) {
	// inputAlphabet.add(s);
	// }
	
	
	public void swapInitialStateToSimple(){
		initialState.setInitial(false);
	}
	
	public void swapFinalStateToSimple(){
		for (State finalState : finalStates){
			finalState.setFinal(false);
		}
	}
	

	public ArrayList<String> getInputAlphabet() {
		return inputAlphabet;
	}

	public String toString() {
		
		String transitionOutput = "";
		String headers = "\t";
		String values = "";
		
		for (int i = 0; i < states.size(); i++){
			values += i + "\t";
			headers += i + "\t";
			for (int j = 0; j < states.size(); j++){
				values += states.get(i).hasTransitionTo(states.get(j)) + "\t";
			}
			values += "\n";
		}
		headers += "\n";
		transitionOutput = "\n" + headers + values;
		
		return "States: " + states + "\nInitial state: " + initialState + "\nFinal state(s): " + finalStates
				+ "\nInput alphabet: " + inputAlphabet + "\nTransitions: " + transitionOutput;
	}
	
	public int getStateCount(){
		return states.size();
	}
	
	public State getInitialState(){
		return initialState;
	}
	
	public ArrayList<State> getFinalStates(){
		return finalStates;
	}
	
	public ArrayList<State> getStates(){
		return states;
	}
	

}
