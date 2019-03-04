package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fa_to_regex.FA_to_Regex;
import fa_to_regex.State_Remover;
import toolbox.FA_Dimension;
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
	private FA_Drawer drawer;

	private JLabel lblRegex;
	private JButton btnNext;
	private JButton btnBack;

	private int centre = 200;
	private int spacing = 150;

	private int frameWidth = 100;
	private int frameHeight = 300;

	boolean backPressed = false;
	boolean redraw = false;

	public FA_Converter_Panel() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		converter = new FA_to_Regex();
		remover = new State_Remover();
		drawer = new FA_Drawer();
	}

	public void paintComponent(Graphics g) {
		// this.g = g;
		super.paintComponent(g);

		if (fa != null) {
			drawer.setGraphics(g);
			// drawer.setCentre(100, 200);

			drawer.setCentre(centre, centre);

			// if goes off the page - redraw and resize the page
			// int

			if (backPressed == true) {
				drawer.drawFA(prevFA);
				backPressed = false;
				// System.out.println("drawing previous FA: " + prevFA);
			} else {
				drawer.drawFA(fa);
				// prevFA = fa;
			}

			FA_Dimension dimension = drawer.getDimension();

			int highestPoint = dimension.getTransitionHeight();
			int lowestPoint = dimension.getBackTransitionHeight();
			int length = dimension.getLength();

			if (highestPoint < 0) {
				redraw = true;
				int distance = 0 - highestPoint;
				int spacing = 20;

				centre += distance + spacing;
			}

			if (lowestPoint >= frameHeight) {
				setFrameSize(frameWidth, lowestPoint + 200);
			}

			if (length >= frameWidth) {
				setFrameSize(length + 200, frameHeight);
			}
		}

		if (redraw == true) {
			redraw = false;
			repaint();
		}
	}

	private void setFrameSize(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		frameWidth = width;
		frameHeight = height;
	}

	public void setFA(Finite_Automata fa) {
		if (initialFA == null) {
			initialFA = fa;
		}
		this.fa = fa;

		centre = 200;
		spacing = 150;

		drawer.setCentre(centre, centre);
		drawer.setSpacing(spacing);

		btnNext.setEnabled(true);

		repaint();
	}

	public void removeState() {
		setFrameSize(100, 300);
		prevFA = fa.copy();
		spacing = spacing + 50;
		drawer.setSpacing(spacing);
		centre = 200;

		if (fa.getFinalStates().size() > 1) {
			fa = converter.createNewFinalState(fa);
		} else {
			ArrayList<State> states = converter.getStatesToRemove(fa);

			if (!states.isEmpty()) {
				State state = states.get(0);
				remover.removeConnectionsTo(state, fa);

			} else {

				// *************
				// TODO
				if (fa.getFinalStates().size() == 0) {
					// there are no final states
					// regex is empty - nothing is accepted

					// this shouldnt be the case - the front end prevents this
					// but might be needed
				}

				State initial = fa.getInitialState();

				if (initial.isFinalState() == true) {
					fa = remover.splitUpInitialAndFinalStates(fa);
				} else {
					cleanUpInitialAndFinalStates();
				}
			}
		}

		btnBack.setEnabled(true);
		repaint();
	}

	public void previousState() {
		backPressed = true;
		fa = prevFA;
		btnBack.setEnabled(false);
		btnNext.setEnabled(true);
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
							// } else {

							// only initial -> final
							String regex = initialState.getTransitionsTo(finalState).get(0);
							lblRegex.setText(regex);
							btnNext.setEnabled(false);

							int width = lblRegex.getText().length();
							if (width >= 60) {
								spacing += 200;
								drawer.setSpacing(spacing);
							}
						}
					}
				}
			}
		}
	}

	public void setRegexLabel(JLabel label) {
		this.lblRegex = label;
	}

	public void resetRegexLabelText() {
		lblRegex.setText("");
	}

	public void setNextButton(JButton next) {
		this.btnNext = next;
	}

	public void setBackButton(JButton back) {
		this.btnBack = back;
	}

}
