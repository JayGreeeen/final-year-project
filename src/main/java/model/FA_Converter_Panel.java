package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fa_to_regex.FA_to_Regex;
import fa_to_regex.State_Remover;
import toolbox.FA_Dimension;
import toolbox.Finite_Automaton;
import toolbox.State;
import view.FA_Drawer;

/**
 * The panel which handles the display of the FA on the FAToRegex page
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class FA_Converter_Panel extends JPanel {

	// takes in FA
	// converts into regex
	// draws the stages of conversion
	private Finite_Automaton fa = null;
	private Finite_Automaton initialFA = null;

	private FA_to_Regex converter;
	private State_Remover remover;
	private FA_Drawer drawer;

	private JLabel lblRegex;
	private JLabel lblDone;
	private JButton btnNext;

	private int centrex = 200;
	private int centrey = 100;
	private int spacing = 150;

	private int frameWidth = 1000;
	private int frameHeight = 350;

	private int currentLowestPoint = 0;

	private Map<Finite_Automaton, Integer[]> faMap;
	private boolean addedToMap = false;

	public FA_Converter_Panel() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(800, 500));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		converter = new FA_to_Regex();
		remover = new State_Remover();
		drawer = new FA_Drawer();

		faMap = new HashMap<>();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawer.setGraphics(g);

		if (fa != null) {
			redraw();
			if (addedToMap == false) {
				paintFA(fa);
			}
		}
	}

	/**
	 * Paints the fa to the screen
	 * 
	 * @param fa
	 */
	private void paintFA(Finite_Automaton fa) {
		drawer.setCentre(centrex, centrey);
		drawer.drawFA(fa);

		FA_Dimension dimension = drawer.getDimension();

		int forwardArrowHeight = dimension.getTransitionHeight();
		int backArrowHeight = dimension.getBackTransitionHeight();
		int length = dimension.getLength();

		if (forwardArrowHeight <= 10) {
			centrey += (0 - forwardArrowHeight) + 50;
			backArrowHeight += (0 - forwardArrowHeight) + 50;
		}

		if (length >= frameWidth) {
			setFrameSize(length + 200, frameHeight);
		}

		if (backArrowHeight >= frameHeight - 100) {
			setFrameSize(frameWidth, backArrowHeight + 200);
		}

		if (currentLowestPoint != 0 && forwardArrowHeight <= currentLowestPoint) {
			int difference;
			if (forwardArrowHeight < 0) {
				difference = currentLowestPoint - (0 - forwardArrowHeight);
			} else {
				difference = currentLowestPoint - forwardArrowHeight;
			}

			centrey += difference + 50;
			backArrowHeight += difference + 50;

			if (addedToMap == false) {
				Integer[] values = { centrex, centrey, spacing };
				faMap.put(fa.copy(), values);
			}
		} else {
			Integer[] values = { centrex, centrey, spacing };

			if (addedToMap == false) {
				faMap.put(fa.copy(), values);
			}
		}

		currentLowestPoint = backArrowHeight;
		addedToMap = true;

		if (btnNext.isEnabled()) {
			repaint();
		}
	}

	/**
	 * Redraws all the previous steps
	 */
	private void redraw() {
		Finite_Automaton fa;
		Integer[] values;

		for (Entry<Finite_Automaton, Integer[]> entry : faMap.entrySet()) {
			fa = (Finite_Automaton) entry.getKey();
			values = entry.getValue();

			drawer.setSpacing(values[2]);
			drawer.setCentre(values[0], values[1]);
			drawer.drawFA(fa);
		}
	}

	/**
	 * Sets the size of the frame
	 * 
	 * @param width
	 * @param height
	 */
	private void setFrameSize(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		frameWidth = width;
		frameHeight = height;
	}

	/**
	 * Performs the initial conversion step when the convert button is pressed
	 * 
	 * @param fa
	 */
	public void convert(Finite_Automaton fa) {
		if (initialFA == null) {
			initialFA = fa;
		}
		this.fa = fa;

		centrey = 100;
		spacing = 150;
		drawer.setCentre(centrex, centrey);
		drawer.setSpacing(spacing);
		resetRegexLabelText();
		resetDoneLabelText();

		faMap = new HashMap<>();
		btnNext.setEnabled(true);
		addedToMap = false;
		currentLowestPoint = 0;

		repaint();
	}

	/**
	 * Removes a state from the FA each time the 'next' button is pressed
	 */
	public void removeState() {
		if (fa != null) {

			addedToMap = false;
			spacing = spacing + 50;
			drawer.setSpacing(spacing);
			centrey += 100;

			if (fa.getFinalStates().size() > 1) {
				fa = converter.createNewFinalState(fa);

			} else {
				ArrayList<State> states = converter.getStatesToRemove(fa);

				if (!states.isEmpty()) {
					State state = states.get(0);
					remover.removeConnectionsTo(state, fa);

					checkFinished();
				} else {
					State initial = fa.getInitialState();

					if (initial.isFinalState() == true) {
						fa = remover.splitUpInitialAndFinalStates(fa);

					} else if (checkFinished() == false) {
						cleanUpInitialAndFinalStates();
					}
				}
			}
			if (btnNext.isEnabled()) {
				repaint();
			}
		}
	}

	/**
	 * Checks to see if the FA consists of only an initial and final state with
	 * a single transition
	 * 
	 * @return
	 */
	private boolean checkFinished() {
		// called when there is only the final and initial state left
		if (fa.getStateCount() == 2) {

			State initialState = fa.getInitialState();
			State finalState = fa.getFinalStates().get(0);

			ArrayList<String> initialTransitions = initialState.getTransitionsTo(finalState);

			if (initialTransitions.size() == 0) {
				// there are no transitions
				lblRegex.setText("\\ FA accepts no words");
				btnNext.setEnabled(false);
				lblDone.setText("Done");

				addedToMap = true;

				return true;
			} else {

				ArrayList<String> finalTransitions = finalState.getTransitionsTo(initialState);

				if (initialTransitions.size() == 1 && finalTransitions.size() == 0) {

					// there is a single transition left
					String regex = initialState.getTransitionsTo(finalState).get(0);
					lblRegex.setText(regex);
					lblDone.setText("Done");
					btnNext.setEnabled(false);
					addedToMap = true;

					int width = lblRegex.getText().length();
					if (width >= 60) {
						spacing += 200;
						drawer.setSpacing(spacing);
					}
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Cleans up the arrows around the initial and final states to reduce them
	 * down to one transition
	 */
	private void cleanUpInitialAndFinalStates() {
		State initialState = fa.getInitialState();
		State finalState = fa.getFinalStates().get(0);

		ArrayList<String> transitions = initialState.getTransitionsTo(finalState);
		if (transitions.size() == 0) {
			// there are no transitions
			lblRegex.setText("\\ FA accepts no words");
			btnNext.setEnabled(false);
			lblDone.setText("Done");
		} else {
			// final -> initial
			transitions = finalState.getTransitionsTo(initialState);
			if (transitions.size() > 1) {
				remover.cleanUpMultiArrows(finalState, initialState);
			} else {
				// initial -> final
				transitions = initialState.getTransitionsTo(finalState);

				if (transitions.size() > 1) {
					remover.cleanUpMultiArrows(initialState, finalState);
				} else {

					// final -> final
					transitions = finalState.getTransitionsTo(finalState);
					if (transitions.size() > 0) {
						remover.cleanUpSelfPointingArrows(finalState);

						// add to the start of final->initial
						// // add to the end of intial-> final
						String label = finalState.getTransitionsTo(finalState).get(0);

						// final -> initial
						transitions = finalState.getTransitionsTo(initialState);
						if (transitions.size() > 0) {
							String finalToInitial = transitions.get(0); // cleaned
																		// up
							finalState.removeTransitionTo(initialState);
							finalState.addTransition(initialState, label + finalToInitial);
						}

						// initial -> final
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

							// will either have final->initial, and
							// initial->final
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

								// only initial -> final
								String regex = initialState.getTransitionsTo(finalState).get(0);
								lblRegex.setText(regex);
								lblDone.setText("Done");
								btnNext.setEnabled(false);

								int width = lblRegex.getText().length();
								if (width >= 60) {
									spacing += 200;
									drawer.setSpacing(spacing);
								}
							} else {

								// should just be left with initial -> final
								String regex = initialState.getTransitionsTo(finalState).get(0);
								lblRegex.setText(regex);
								lblDone.setText("Done");
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
	}

	/**
	 * Sets the regex label to the output of the conversion
	 * 
	 * @param label
	 */
	public void setRegexLabel(JLabel label) {
		this.lblRegex = label;
	}

	/**
	 * Resets the regex label
	 */
	public void resetRegexLabelText() {
		lblRegex.setText("");
	}

	/**
	 * Sets the 'done' label, indicating completion of the conversion
	 * 
	 * @param label
	 */
	public void setDoneLabel(JLabel label) {
		this.lblDone = label;
	}

	/**
	 * Resets the 'done; label
	 */
	public void resetDoneLabelText() {
		lblDone.setText("");
	}

	/**
	 * Sets the next button object
	 * 
	 * @param next
	 */
	public void setNextButton(JButton next) {
		this.btnNext = next;
	}

	/**
	 * Resets the details on the page
	 */
	public void resetPage() {
		resetRegexLabelText();
		resetDoneLabelText();

		fa = null;
		setFrameSize(1000, 350);
	}

}
