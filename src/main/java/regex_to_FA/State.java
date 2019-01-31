package regex_to_FA;

import java.util.HashMap;
import java.util.Map;

public class State {

	private String label;
	private Map<State, String> transitions;
	private boolean isInitialState = false;
	private boolean isFinalState = false;

	public State(String label) {
		this.label = label;
		transitions = new HashMap<State, String>();
	}

	public void addTransition(State to, String transition) {
		transitions.put(to, transition);
	}

	public void addEmptyTransition(State to) {
		transitions.put(to, "ε");
	}

	public void renameState(String label) {
		this.label = label;
	}

	public String getTransitionTo(State state) {
		if (transitions.containsKey(state)) {
			return transitions.get(state);
		}
		return "-";
	}

	public Map<State, String> getTransitions() {
		return transitions;
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