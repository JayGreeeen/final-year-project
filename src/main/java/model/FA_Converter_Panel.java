package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fa_to_regex.FA_to_Regex;
import fa_to_regex.State_Remover;
import toolbox.Finite_Automata;
import toolbox.State;
import view.FA_Drawer;

public class FA_Converter_Panel extends JPanel {

	// takes in FA
	// converts into regex
	// draws the stages of conversion
	private Finite_Automata prevFA = null;
	private Finite_Automata fa = null;
	private Finite_Automata initialFA = null;

	private FA_to_Regex converter;
	private State_Remover remover;

	private JLabel lblRegex;
	
	boolean backPressed = false;

	public FA_Converter_Panel() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		converter = new FA_to_Regex();
		remover = new State_Remover();
		
		JLabel label = new JLabel("Regex: ");
		
	}

	public void paintComponent(Graphics g) {
		// this.g = g;
		super.paintComponent(g);

		if (fa != null) {
			FA_Drawer drawer = new FA_Drawer(g);

			if (backPressed == true) {
				drawer.drawFA(prevFA);
				backPressed = false;
				System.out.println("drawing previous FA: " + prevFA);
			} else {
				drawer.drawFA(fa);
				// prevFA = fa;
			}

			// TODO

			// String regex = converter.convert(fa);
			// g.drawString(regex, 50, 50);
		}
	}

	public void setFA(Finite_Automata fa) {
		if (initialFA == null) {
			initialFA = fa;
		}
		this.fa = fa;

		repaint();
	}

	public void removeState() {
		prevFA = fa.copy();

		if (fa.getFinalStates().size() > 1) {
			fa = converter.reduceFinalStateCount(fa);
		} else {
			ArrayList<State> states = converter.getStatesToRemove(fa);

			if (!states.isEmpty()) {
				State state = states.get(0);
				remover.removeConnectionsTo(state, fa);

			} else {
				cleanUpInitialAndFinalStates();
			}
		}
		repaint();
	}

	public void previousState() {
		backPressed = true;
		fa = prevFA;
		repaint();
	}

	private void cleanUpInitialAndFinalStates() {
		State initialState = fa.getInitialState();
		State finalState = fa.getFinalStates().get(0);

		ArrayList<String> transitions = finalState.getTransitionsTo(initialState);
		if (transitions.size() > 1) {
			remover.cleanUpMultiArrows(finalState, initialState);
		} else {

			transitions = initialState.getTransitionsTo(finalState);
			if (transitions.size() > 1) {
				remover.cleanUpMultiArrows(initialState, finalState);
			} else {

				// clean up self pointing arrows
				transitions = finalState.getTransitionsTo(finalState);
				if (transitions.size() > 0) {
					remover.cleanUpSelfPointingArrows(finalState);

					// add to the start of final->initial
					// // add to the end of intial-> final
					String label = finalState.getTransitionsTo(finalState).get(0);

					transitions = finalState.getTransitionsTo(initialState);
					if (transitions.size() > 0) {
						String finalToInitial = transitions.get(0); // cleaned
																	// up
						finalState.removeTransitionTo(initialState);
						finalState.addTransition(initialState, label + finalToInitial);
					}

					transitions = initialState.getTransitionsTo(finalState);
					if (transitions.size() > 0) {
						String initialToFinal = transitions.get(0);
						initialState.removeTransitionTo(finalState);
						initialState.addTransition(finalState, initialToFinal + label);
					}

					finalState.removeTransitionTo(finalState);

				} else {
					// clean up self pointing arrows for initial
					transitions = initialState.getTransitionsTo(initialState);
					if (transitions.size() > 0) {
						remover.cleanUpSelfPointingArrows(initialState);

						// add to the end of final->initial
						// // add to the start of intial-> final
						String label = initialState.getTransitionsTo(initialState).get(0);

						transitions = finalState.getTransitionsTo(initialState);
						if (transitions.size() > 0) {
							String finalToInitial = transitions.get(0); // cleaned
																		// up
							finalState.removeTransitionTo(initialState);
							finalState.addTransition(initialState, finalToInitial + label);
						}

						transitions = initialState.getTransitionsTo(finalState);
						if (transitions.size() > 0) {
							String initialToFinal = transitions.get(0);
							initialState.removeTransitionTo(finalState);
							initialState.addTransition(finalState, label + initialToFinal);
						}

						initialState.removeTransitionTo(initialState);
					} else {

						// will either have final->initial, and initial->final
						// or just initial->final
						// in which case, return the label

						transitions = finalState.getTransitionsTo(initialState);
						if (transitions.size() > 0) {
							String initialToFinalLabel = initialState.getTransitionsTo(finalState).get(0);
							String finalToInitialLabel = transitions.get(0);

							String label = initialToFinalLabel + "|(" + initialToFinalLabel + finalToInitialLabel
									+ initialToFinalLabel + ")*";

							finalState.removeTransitionTo(initialState);
							initialState.removeTransitionTo(finalState);
							initialState.addTransition(finalState, label);
						} else {
							
							// only initial -> final 
							String regex = initialState.getTransitionsTo(finalState).get(0);
							lblRegex.setText(regex);
						}
					}
				}
			}
		}
	}


	public void setRegexLabel(JLabel label){
		this.lblRegex = label;
	}
	
	public void resetRegexLabelText(){
		lblRegex.setText("");
	}
	
}

