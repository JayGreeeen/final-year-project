package toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * State object class. Represent states within a finite automaton
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class State {

	private String label;
	private Map<State, ArrayList<String>> transitions;
	private boolean isInitialState = false;
	private boolean isFinalState = false;

	public State(String label) {
		this.label = label;
		transitions = new HashMap<State, ArrayList<String>>();
	}

	/**
	 * Adds a transition to the state
	 * 
	 * @param to
	 *            the state the transition points to
	 * @param label
	 *            the label on the transition
	 */
	public void addTransition(State to, String label) {
		ArrayList<String> transitionList;

		if (transitions.containsKey(to)) {
			transitionList = transitions.get(to);
		} else {
			transitionList = new ArrayList<String>();
		}
		transitionList.add(label);
		transitions.put(to, transitionList);
	}

	/**
	 * Adds an empty jump transition to the state. Used for NFAs
	 * 
	 * @param to
	 *            the state the transition points to
	 */
	public void addEmptyTransition(State to) {
		addTransition(to, "ε");
	}

	/**
	 * Renames the state with eht new specified label
	 * 
	 * @param label
	 *            the new name for the state
	 */
	public void renameState(String label) {
		this.label = label;
	}

	/**
	 * Returns the list of labels for the transitions which point to the
	 * specified state, or an empty list if there are no transitions to this
	 * state
	 * 
	 * @param state
	 *            to find the transitions for
	 * @return the list of labels
	 */
	public ArrayList<String> getTransitionsTo(State state) {
		if (transitions.containsKey(state)) {
			return transitions.get(state);
		}

		ArrayList<String> transitionList = new ArrayList<String>();
		return transitionList;
	}

	/**
	 * Returns the mapping of transitions for the state - all the states this
	 * state points to, and the labels
	 * 
	 * @return the map of transitions
	 */
	public Map<State, ArrayList<String>> getTransitions() {
		return transitions;
	}

	/**
	 * Removes all transitions to the specified state
	 * 
	 * @param state
	 *            to remove transitions to
	 */
	public void removeTransitionTo(State state) {
		transitions.remove(state);
	}

	/**
	 * @return the name of the state
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets whether this state is an initial state or not for an automaton
	 * 
	 * @param bool
	 *            true or false
	 */
	public void setInitial(boolean bool) {
		isInitialState = bool;
	}

	/**
	 * Sets whether this state is a final state or not
	 * 
	 * @param bool
	 */
	public void setFinal(boolean bool) {
		isFinalState = bool;
	}

	/**
	 * Returns whether this state is an initial state
	 * 
	 * @return true or false
	 */
	public boolean isInitialState() {
		return isInitialState;
	}

	/**
	 * Returns whether this state is a final state
	 * 
	 * @return true or false
	 */
	public boolean isFinalState() {
		return isFinalState;
	}

	public String toString() {
		return label;
	}

}