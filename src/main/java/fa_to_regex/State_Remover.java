package fa_to_regex;

import java.util.ArrayList;

import toolbox.Finite_Automata;
import toolbox.State;

public class State_Remover {

	public State_Remover() {
	}

	// public Finite_Automata remove(State state, Finite_Automata oldfa) {
	public void removeConnectionsTo(State state, Finite_Automata oldfa) {
		System.out.println("Removing state " + state.getLabel());

		ArrayList<State> states = oldfa.getStates();

		// states which point to this state
		ArrayList<State> startStates = new ArrayList<State>();

		for (State s : states) {
			// System.out.println("checking state " + s);
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

				System.out.println("\tcreating transition from " + startState + " to " + endState);

				String label = createUnionLabel(startState.getTransitionsTo(state));
//				System.out.println("Label, " + startState + " to " + state + ": " + label);

				String selfPointingLabel = createUnionLabel(state.getTransitionsTo(state));
				if (selfPointingLabel != "") {
					label += createStarLabel(selfPointingLabel);
				}

				label += createUnionLabel(state.getTransitionsTo(endState));
				startState.addTransition(endState, label);
				System.out
						.println("\tAdding transition from " + startState + " to " + endState + " with label: " + label);

			}
		}

		for (State startState : startStates) {
			startState.removeTransitionTo(state);
		}
		for (State endState : endStates) {
			state.removeTransitionTo(endState);
		}
	}

	protected String cleanUpInitialAndFinalStates(Finite_Automata fa) {
		// remove any self pointing arrows
		// remove multiple arrows to initial / final state
		State initial = fa.getInitialState();
		State finalState = fa.getFinalStates().get(0);

		if (initial == finalState) {
			return singleStateAutomaton(fa);
		}

		// clean up initial
		System.out.println("initial: " + initial.getTransitions());
		System.out.println("finalState: " + finalState.getTransitions());
		
		String initialToFinal = cleanUpMultiArrows(initial, finalState);
		String finalToInitial = cleanUpMultiArrows(finalState, initial);
		String initialToInitial = cleanUpSelfPointingArrows(initial);
		String finalToFinal = cleanUpSelfPointingArrows(finalState);

		String regex = initialToFinal;
		String f2i = finalToInitial;

		if (!initialToInitial.equals("-") && !initialToInitial.equals("")) {
			System.out.println("Adding " + initialToInitial + " to start of " + regex);

			regex = initialToInitial + initialToFinal;
			f2i += initialToInitial;
		}

//		if (finalToFinal != "-" && finalToFinal != "") {
		if (!finalToFinal.equals("-") && !finalToFinal.equals("")) {
			System.out.println("Adding " + finalToFinal + " to end of " + regex);
			regex += finalToFinal;
			f2i = finalToFinal + f2i;
		}

//		if (finalToInitial != "-" && finalToInitial != "") {
		if (!finalToInitial.equals("-") && !finalToInitial.equals("")) {
			System.out.println("adding " + f2i + " as choice: " + "(" + f2i + regex + ")*");
			regex = regex + "|" + "(" + regex + f2i + regex + ")*";
		}

		return regex;
	}

	private String singleStateAutomaton(Finite_Automata fa) {
		return cleanUpSelfPointingArrows(fa.getInitialState());
	}

	private String cleanUpSelfPointingArrows(State state) {
		ArrayList<String> transitions = state.getTransitionsTo(state);
		String label = "";

		if (transitions.size() > 0 && !transitions.get(0).equals("-")) {
			if (transitions.size() > 1) {
				label = createUnionLabel(transitions);
			} else {

				label = transitions.get(0);
			}
			label = createStarLabel(label);
			state.removeTransitionTo(state);
			state.addTransition(state, label);
			System.out.println("self pointing label: " + label);
		}
		return label;
	}

	private String cleanUpMultiArrows(State from, State to) {
		ArrayList<String> transitions = from.getTransitionsTo(to);
		String label = "";
		
//		System.out.println("found mutliple transitions: " + from + " to " + to + ": " + from.getTransitionsTo(to));

		if (transitions.size() > 0 && transitions.get(0) != "-") {
			if (transitions.size() > 1) {
				label = createUnionLabel(transitions);
			} else {
				label = transitions.get(0);
			}
			from.removeTransitionTo(to);
			from.addTransition(to, label);
			System.out.println("multi arrow label: " + label);

		}
		return label;
	}

	private String createStarLabel(String l) {
		String label = "";

		if (l.length() == 1 || (l.startsWith("(") && l.endsWith(")"))) {
			label = l + "*";
		} else if (l.length() > 1) {
			label = "(" + l + ")*";
		}
		// System.out.println("creating star label: " + label);
		return label;
	}

	/*
	 * if there is no label, only expect 1 to be returned if there is more than
	 * one label, they are expected to not be empty
	 */
	private String createUnionLabel(ArrayList<String> labels) {
		String label = "";

		for (String l : labels) {
			if (l != "-") {
				label += l;
				if (labels.indexOf(l) != labels.size() - 1) {
					label += "|";// not the last label
				}
			}
		}
		if (label.length() > 1 && label.contains("|")) {
			label = "(" + label + ")";
		}
		// System.out.println("Creating union label: " + label);
		return label;
	}

}
