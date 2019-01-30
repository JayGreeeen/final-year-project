package regex_to_FA;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import regex_to_FA.TreeNode.LeafNode;

public class Automaton_Builder {

	public Automaton_Builder() {
	}

	// create basic automaton for a leaf node - single transition
	// initial state -- node text --> final state
	public FiniteAutomata buildSimpleAutomaton(LeafNode node) {
		String label = node.getText();

		// initial state
		State initialState = new State("0");
		initialState.setInitial(true);

		// final state
		State finalState = new State("1");
		finalState.setFinal(true);
		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		// input alphabet
		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.add(label);

		// states
		ArrayList<State> states = new ArrayList<State>();
		states.add(initialState);
		states.add(finalState);

		// transitions
		initialState.addTransition(finalState, label);

		return new FiniteAutomata(initialState, finalStates, states, inputAlphabet);
	}

	// combine with union symbol - rule 3
	public FiniteAutomata combineWithUnion(FiniteAutomata FA1, FiniteAutomata FA2) {
		State initialState = createNewInitialState();

		int totalStateCount = FA1.getStateCount() + FA2.getStateCount();
		State finalState = createNewFinalState(totalStateCount);

		ArrayList<State> states = renameStates(FA1.getStates(), FA2.getStates());
		states.add(0, initialState);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		// set up FA1
		State initialFA1 = FA1.getInitialState();
		initialFA1.setInitial(false);
		initialState.addEmptyTransition(initialFA1);

		State finalFA1 = FA1.getFinalStates().get(0); // only 1 final state
		finalFA1.setFinal(false);
		finalFA1.addEmptyTransition(finalState);

		// set up FA2
		State initialFA2 = FA2.getInitialState();
		initialFA2.setInitial(false);
		initialState.addEmptyTransition(initialFA2);

		State finalFA2 = FA2.getFinalStates().get(0); // only 1 final state
		finalFA2.setFinal(false);
		finalFA2.addEmptyTransition(finalState);

		// input alphabet
		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.addAll(FA1.getInputAlphabet());
		inputAlphabet.addAll(FA2.getInputAlphabet());

		return new FiniteAutomata(initialState, finalStates, states, inputAlphabet);
	}

	// combine with concat symbol = rule 4
	public FiniteAutomata combineWithConcat(FiniteAutomata FA1, FiniteAutomata FA2) {
		State initialState = createNewInitialState();

		State initialStateFA1 = FA1.getInitialState();
		State finalStateFA1 = FA1.getFinalStates().get(0);

		State initialStateFA2 = FA2.getInitialState();
		State finalStateFA2 = FA2.getFinalStates().get(0);

		// combine final state of FA1 and initial state of FA2
		combineStates(finalStateFA1, initialStateFA2);
		
		// remove initial state of FA2 from set 
		ArrayList<State> FA2States = FA2.getStates();
		FA2States.remove(initialStateFA2);
		
		ArrayList<State> states = renameStates(FA1.getStates(), FA2States);
		
		State finalState = createNewFinalState(states.size());
		states.add(0, initialState);
		states.add(finalState);
		
		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);
		
		initialStateFA1.setInitial(false);
		initialState.addEmptyTransition(initialStateFA1);
		finalStateFA1.setFinal(false);

		finalStateFA2.setFinal(false);
		finalStateFA2.addEmptyTransition(finalState);

		// input alphabet
		ArrayList<String> inputAlphabet = new ArrayList<String>();
		inputAlphabet.addAll(FA1.getInputAlphabet());
		inputAlphabet.addAll(FA2.getInputAlphabet());

		return new FiniteAutomata(initialState, finalStates, states, inputAlphabet);
	}
	
	private State combineStates(State state1, State state2){
		Map<State, String> transitions = state2.getTransitions();
		
		for (Entry<State, String> transition : transitions.entrySet()){
			state1.addTransition(transition.getKey(), transition.getValue());
		}
		return state1;
	}

	// star operator - rule 5
	public FiniteAutomata addStarOperator(FiniteAutomata FA) {
		State initialState = createNewInitialState();
		State finalState = createNewFinalState(FA.getStateCount());

		ArrayList<State> states = renameStates(FA.getStates());
		states.add(0, initialState);
		states.add(finalState);

		ArrayList<State> finalStates = new ArrayList<State>();
		finalStates.add(finalState);

		State oldInitial = FA.getInitialState();
		oldInitial.setInitial(false);
		State oldFinal = FA.getFinalStates().get(0);
		oldFinal.setFinal(false);

		initialState.addEmptyTransition(oldInitial);
		initialState.addEmptyTransition(finalState);

		oldFinal.addEmptyTransition(finalState);
		oldFinal.addEmptyTransition(oldInitial);

		return new FiniteAutomata(initialState, finalStates, states, FA.getInputAlphabet());
	}

	private ArrayList<State> renameStates(ArrayList<State> statesSet1, ArrayList<State> statesSet2) {
		ArrayList<State> states = new ArrayList<State>();
		states.addAll(statesSet1);
		states.addAll(statesSet2);

		int counter = 1;
		for (State state : states) {
			System.out.println("Renaming state " + state.getLabel() + " to state " + counter);
			state.renameState(Integer.toString(counter));
			counter++;
		}
		return states;
	}

	private ArrayList<State> renameStates(ArrayList<State> states) {
		int counter = 1;
		for (State state : states) {
			System.out.println("Renaming state " + state.getLabel() + " to state " + counter);
			state.renameState(Integer.toString(counter));
			counter++;
		}
		return states;
	}

	private State createNewInitialState() {
		State initial = new State("0");
		initial.setInitial(true);
		return initial;
	}

	private State createNewFinalState(int totalStateCount) {
		State finalState = new State(Integer.toString(totalStateCount + 1));
		finalState.setFinal(true);
		return finalState;
	}
}
