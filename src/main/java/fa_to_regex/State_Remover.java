package fa_to_regex;

import java.util.ArrayList;

import toolbox.Finite_Automaton;
import toolbox.State;

public class State_Remover {

	public State_Remover() {
	}

	// public Finite_Automata remove(State state, Finite_Automata oldfa) {
	public void removeConnectionsTo(State state, Finite_Automaton oldfa) {
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

//				System.out.println("\tcreating transition from " + startState + " to " + endState);

				String label = createUnionLabel(startState.getTransitionsTo(state));
				// System.out.println("Label, " + startState + " to " + state +
				// ": " + label);

				String selfPointingLabel = createUnionLabel(state.getTransitionsTo(state));
				if (selfPointingLabel != "") {
					label += createStarLabel(selfPointingLabel);
				}

				label += createUnionLabel(state.getTransitionsTo(endState));
				startState.addTransition(endState, label);
//				System.out.println(
//						"\tAdding transition from " + startState + " to " + endState + " with label: " + label);

			}
		}

		for (State startState : startStates) {
			startState.removeTransitionTo(state);
		}
		for (State endState : endStates) {
			state.removeTransitionTo(endState);
		}

		oldfa.removeState(state);
	}
	
	// TODO 
	// ***************************
	// the case for when the initial and final states are the same
	private void singleStateAutomaton(Finite_Automaton fa) {
		cleanUpSelfPointingArrows(fa.getInitialState());
	}

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

	private String createStarLabel(String l) {
		String label = "";

		if (l.length() == 1 || (l.startsWith("(") && l.endsWith(")"))) {
			label = l + "*";
		} else if (l.length() > 1) {
			label = "(" + l + ")*";
		}
		return label;
	}

	/*
	 * if there is no label, only expect 1 to be returned if there is more than
	 * one label, they are expected to not be empty
	 */
	private String createUnionLabel(ArrayList<String> labels) {
		String label = "";

		for (int i = 0; i < labels.size(); i++){
			String l = labels.get(i);
			label += l;
			
			if (i != labels.size() - 1){
				label += "|";
			}
		}
		
		return label;
	}
	
	public Finite_Automaton splitUpInitialAndFinalStates(Finite_Automaton fa){
		// for the case when the final and initial stateds are the same
		
		State initial = fa.getInitialState();
		initial.setFinal(false);
		
		State finalState = new State("end");
		finalState.setFinal(true);
		
		ArrayList<String> transitions = initial.getTransitionsTo(initial);
		initial.removeTransitionTo(initial);
		for (String t : transitions){
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
