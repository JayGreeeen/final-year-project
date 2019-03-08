package toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State {

	private String label;
	private Map<State, ArrayList<String>> transitions;
	private boolean isInitialState = false;
	private boolean isFinalState = false;
	
	public State(String label) {
		this.label = label;
		transitions = new HashMap<State, ArrayList<String>>();
	}

	public void addTransition(State to, String transition) {
		ArrayList<String> transitionList;
		
		if (transitions.containsKey(to)){
			transitionList = transitions.get(to);
		} else {
			transitionList = new ArrayList<String>();
		}
		transitionList.add(transition);
		transitions.put(to, transitionList);
	}

	public void addEmptyTransition(State to) {
		addTransition(to, "ε");
	}

	public void renameState(String label) {
		this.label = label;
	}

	public ArrayList<String> getTransitionsTo(State state) {
		if (transitions.containsKey(state)) {
			return transitions.get(state);
		}
		
		
		ArrayList<String> transitionList = new ArrayList<String>();
		return transitionList;
	}

	public Map<State, ArrayList<String>> getTransitions() {
		return transitions;
	}
	
	public void removeTransitionTo(State state){
		transitions.remove(state);
	}

	public String getLabel() {
		return label;
	}
	
	public void setInitial(boolean initial) {
		isInitialState = initial;
	}

	public void setFinal(boolean initial) {
		isFinalState = initial;
	}

	public boolean isInitialState() {
		return isInitialState;
	}

	public boolean isFinalState() {
		return isFinalState;
	}

	public String toString() {
		return label;
	}

}