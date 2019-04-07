package fa_to_regex;

import java.util.ArrayList;

import toolbox.Finite_Automaton;
import toolbox.State;

/**
 * Removes a state and all its connections from an automaton
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class State_Remover {

	public State_Remover() {
	}

	/**
	 * Removes all connections to a state in the automaton
	 * 
	 * @param state
	 *            to remove
	 * @param fa
	 *            to remove the state from
	 */
	public void removeConnectionsTo(State state, Finite_Automaton fa) {
		ArrayList<State> states = fa.getStates();

		// states which point to this state
		ArrayList<State> startStates = new ArrayList<State>();

		for (State s : states) {
			ArrayList<String> labels = s.getTransitionsTo(state);
			// get the list of labels from start state to current
			String label = createUnionLabel(labels);
			if (label != "") {
				startStates.add(s);
			}
		}

		if (startStates.contains(state)) {
			startStates.remove(state);
		}

		// states which this state points to
		ArrayList<State> endStates = new ArrayList<State>();
		endStates.addAll(state.getTransitions().keySet());
		if (endStates.contains(state)) {
			endStates.remove(state);
		}

		for (State startState : startStates) {

			for (State endState : endStates) {

				String label = createUnionLabel(startState.getTransitionsTo(state));

				String selfPointingLabel = createUnionLabel(state.getTransitionsTo(state));
				if (selfPointingLabel != "") {
					label += createStarLabel(selfPointingLabel);
				}

				label += createUnionLabel(state.getTransitionsTo(endState));
				startState.addTransition(endState, label);
			}
		}

		// removes all connections to the state
		for (State startState : startStates) {
			startState.removeTransitionTo(state);
		}

		// removes all connections from the state
		for (State endState : endStates) {
			state.removeTransitionTo(endState);
		}

		fa.removeState(state);
	}

	/**
	 * Adds a * onto the end of the label of a self-pointing arrow
	 * 
	 * @param state
	 *            to clean up the transition for
	 */
	public void cleanUpSelfPointingArrows(State state) {
		ArrayList<String> transitions = state.getTransitionsTo(state);
		String label = "";

		if (transitions.size() > 0) {
			if (transitions.size() > 1) {
				label = createUnionLabel(transitions);
			} else {
				label = transitions.get(0);
			}
			label = createStarLabel(label);
			state.removeTransitionTo(state);
			state.addTransition(state, label);
		}

		state.removeTransitionTo(state);
		state.addTransition(state, label);
	}

	/**
	 * Removes multiple transitions pointing to the same state by combining the
	 * labels with a union label into one transition
	 * 
	 * @param from
	 *            state the transition leaves from
	 * @param to
	 *            state the transition points to
	 */
	public void cleanUpMultiArrows(State from, State to) {
		ArrayList<String> transitions = from.getTransitionsTo(to);
		String label = "";

		if (transitions.size() > 0) {
			if (transitions.size() > 1) {
				label = createUnionLabel(transitions);
			} else {
				label = transitions.get(0);
			}
			from.removeTransitionTo(to);
			from.addTransition(to, label);
		}

		from.removeTransitionTo(to);
		from.addTransition(to, label);
	}

	/**
	 * Adds a * onto the end of the label
	 * 
	 * @param l
	 *            the label to add the * to
	 * @return the new label
	 */
	private String createStarLabel(String l) {
		String label = "";

		if (l.length() == 1 || (l.startsWith("(") && l.endsWith(")"))) {
			label = l + "*";
		} else if (l.length() > 1) {
			label = "(" + l + ")*";
		}
		return label;
	}

	/**
	 * Combines a list of labels using the union symbol
	 * 
	 * @param labels
	 *            the list of labels to combine
	 * @return the single label
	 */
	private String createUnionLabel(ArrayList<String> labels) {
		String label = "";

		for (int i = 0; i < labels.size(); i++) {
			String l = labels.get(i);
			label += l;

			if (i != labels.size() - 1) {
				label += "|";
			}
		}

		return label;
	}

	/**
	 * Handles the case when the initial and the final state are the same state
	 * by creating a new final state
	 * 
	 * @param fa
	 *            the automaton to split the states for
	 * @return the new automaton
	 */
	public Finite_Automaton splitUpInitialAndFinalStates(Finite_Automaton fa) {
		State initial = fa.getInitialState();
		initial.setFinal(false);

		// create a new final state
		State finalState = new State("end");
		finalState.setFinal(true);

		ArrayList<String> transitions = initial.getTransitionsTo(initial);
		initial.removeTransitionTo(initial);
		for (String t : transitions) {
			initial.addTransition(finalState, t);
		}

		ArrayList<State> finalStates = new ArrayList<>();
		finalStates.add(finalState);

		ArrayList<State> states = new ArrayList<>();
		states.add(initial);
		states.add(finalState);

		ArrayList<String> inputAlphabet = new ArrayList<>();
		inputAlphabet.addAll(fa.getInputAlphabet());

		return new Finite_Automaton(initial, finalStates, states, inputAlphabet);
	}

}
