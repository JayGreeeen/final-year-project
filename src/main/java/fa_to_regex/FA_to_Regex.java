package fa_to_regex;

import java.util.ArrayList;

import toolbox.Finite_Automata;
import toolbox.State;

public class FA_to_Regex {
	private State_Remover remover = new State_Remover();

	public FA_to_Regex() {

	}

	public String convert(Finite_Automata fa) {
		ArrayList<State> finalStates = fa.getFinalStates();
		ArrayList<State> states = new ArrayList<State>();
		states.addAll(fa.getStates());

		State initial = fa.getInitialState();
		State finalState;
		
		states.remove(initial);

		if (finalStates.size() > 1) { // more than one final state
			finalState = new State(Integer.toString(fa.getStateCount() + 1));
			newFinalState(finalStates, finalState);

			finalStates.clear();
			finalStates.add(finalState);
			fa = new Finite_Automata(initial, finalStates, states, fa.getInputAlphabet());
			// adds the new final state to the automaton

		} else { // there is only one final state
			finalState = finalStates.get(0);
			states.remove(finalState);
		}
		
		
		if (!states.isEmpty()) {
			removeStates(states, fa);
		}

		String regex = remover.cleanUpInitialAndFinalStates(fa);

//		if (regex.startsWith("(") && regex.endsWith(")")){
//			regex = regex.substring(1, regex.length() - 1);
//		}
		System.out.println("Regex: " + regex);
		return regex;
	}

	private void newFinalState(ArrayList<State> finalStates, State finalState) {
		for (State state : finalStates) {
			state.setFinal(false);
			state.addEmptyTransition(finalState);
		}

	}

	private void removeStates(ArrayList<State> states, Finite_Automata fa) {
		for (State state : states) {
			remover.removeConnectionsTo(state, fa);
		}
	}

}
