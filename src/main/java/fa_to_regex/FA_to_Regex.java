package fa_to_regex;

import java.util.ArrayList;

import toolbox.Finite_Automata;
import toolbox.State;

public class FA_to_Regex {
	private State_Remover remover = new State_Remover();

	public FA_to_Regex() {

	}

	public String convert(Finite_Automata fa) {

//		String regex = remover.cleanUpInitialAndFinalStates(fa);
		String regex = "";

		System.out.println("Regex: " + regex);
		return regex;
	}

	private void createNewFinalState(ArrayList<State> finalStates, State finalState) {
		for (State state : finalStates) {
			state.setFinal(false);
			state.addEmptyTransition(finalState);
		}

	}

//	private void removeStates(ArrayList<State> states, Finite_Automata fa) {
//		for (State state : states) {
//			remover.removeConnectionsTo(state, fa);
//		}
//	}

	public ArrayList<State> getStatesToRemove(Finite_Automata fa) {
		ArrayList<State> states = new ArrayList<State>();
		states.addAll(fa.getStates());

		State initial = fa.getInitialState();
		State finalState;

		states.remove(initial);

		finalState = fa.getFinalStates().get(0);
		states.remove(finalState);

		return states;
	}

	public Finite_Automata reduceFinalStateCount(Finite_Automata fa) {
		ArrayList<State> finalStates = fa.getFinalStates();
		// state count might be 3, but may be labled 1-3
		State finalState = new State("end");
		
		finalState.setFinal(true);
		
		createNewFinalState(finalStates, finalState);

		ArrayList<State> states = fa.getStates();
		states.add(finalState);
		
		finalStates = new ArrayList<>();
		finalStates.add(finalState);
		fa = new Finite_Automata(fa.getInitialState(), finalStates, states, fa.getInputAlphabet());
		// adds the new final state to the automaton

		System.out.println("added new final state: " + fa.getStates());
		
		return fa;
	}

}
