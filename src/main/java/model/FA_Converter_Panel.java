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
import toolbox.Finite_Automata;
import toolbox.State;
import view.FA_Drawer;

public class FA_Converter_Panel extends JPanel {

	// takes in FA
	// converts into regex
	// draws the stages of conversion
	private Finite_Automata fa = null;
	private Finite_Automata initialFA = null;

	private FA_to_Regex converter;
	private State_Remover remover;
	private FA_Drawer drawer;

	private JLabel lblRegex;
	private JButton btnNext;

	private int centrex = 200;
	private int centrey = 100;
	private int spacing = 150;

	private int frameWidth = 1000;
	private int frameHeight = 350;

	private int currentLowestPoint = 0;

	private Map<Finite_Automata, Integer[]> faMap;
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
			if (addedToMap == false){
				paintFA(fa);
			}
		}
	}

	private void paintFA(Finite_Automata fa) {
//		System.out.println("painting FA: " + centrex + ", " + centrey);
		drawer.setCentre(centrex, centrey);
		drawer.drawFA(fa);

		FA_Dimension dimension = drawer.getDimension();

		int forwardArrowHeight = dimension.getTransitionHeight();
		int backArrowHeight = dimension.getBackTransitionHeight();
		int length = dimension.getLength();
		
//		System.out.println("forward arrow height: " + forwardArrowHeight + ". Back arrow height: " + backArrowHeight);

		if (forwardArrowHeight < 0) {
//			System.out.println("goes off the page");
			// goes off the page
			centrey += (0 - forwardArrowHeight) + 50;
			backArrowHeight += (0 - forwardArrowHeight) + 50;
		}

		if (length >= frameWidth) {
//			System.out.println("wider than the page - length: " + length + " > " + frameWidth);
			setFrameSize(length + 200, frameHeight);
		}

		if (backArrowHeight >= frameHeight - 100) {
//			System.out.println("lowest point, goes off the page: " + backArrowHeight);
			// System.out.println("longer than the page");
			setFrameSize(frameWidth, backArrowHeight + 200);
		}
		
//		System.out.println("\tCurrent lowest point: " + currentLowestPoint);
		
		if (currentLowestPoint != 0 && forwardArrowHeight < currentLowestPoint) {
//			System.out.println("moving down");
			int difference;
			if (forwardArrowHeight < 0){
				difference = currentLowestPoint - (0 - forwardArrowHeight);
			} else {
				difference = currentLowestPoint - forwardArrowHeight;
			}

			centrey += difference + 50;
			backArrowHeight += difference + 50;

			if (addedToMap == false) {
				Integer[] values = { centrex, centrey, spacing };
				faMap.put(fa.copy(), values);
//				System.out.println("\tadding to FA map");
			}
		} else {
			Integer[] values = { centrex, centrey, spacing };

			if (addedToMap == false) {
				faMap.put(fa.copy(), values);
//				System.out.println("\tadding to FA map. size: " + faMap.size());
			}
		}

		currentLowestPoint = backArrowHeight;
		addedToMap = true;

		repaint();
	}

	private void redraw() {
		Finite_Automata fa;
		Integer[] values;

		for (Entry<Finite_Automata, Integer[]> entry : faMap.entrySet()) {
			fa = (Finite_Automata) entry.getKey();
			values = entry.getValue();

			drawer.setSpacing(values[2]);
			drawer.setCentre(values[0], values[1]);
			drawer.drawFA(fa);
		}
	}

	private void setFrameSize(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		frameWidth = width;
		frameHeight = height;
	}

	public void convert(Finite_Automata fa) {
		if (initialFA == null) {
			initialFA = fa;
		}
		this.fa = fa;

		centrey = 100;
		spacing = 150;
		drawer.setCentre(centrex, centrey);
		drawer.setSpacing(spacing);

		faMap = new HashMap<>();
		btnNext.setEnabled(true);
		addedToMap = false;
		currentLowestPoint = 0;

		repaint();
	}

	public void removeState() {
		addedToMap = false;
		
//		System.out.println("removing state. size of fa Map: " + faMap.size());

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
			} else {
				State initial = fa.getInitialState();

				if (initial.isFinalState() == true) {
					fa = remover.splitUpInitialAndFinalStates(fa);

				} else {
					cleanUpInitialAndFinalStates();
				}
			}
		}
		repaint();
	}

	private void cleanUpInitialAndFinalStates() {
		State initialState = fa.getInitialState();
		State finalState = fa.getFinalStates().get(0);

		ArrayList<String> transitions = initialState.getTransitionsTo(finalState);
		if (transitions.size() == 0) {
			// there are no transitions
			lblRegex.setText("\\ FA accepts no words");
			btnNext.setEnabled(false);
		}

		transitions = finalState.getTransitionsTo(initialState);
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
						} else {
							
							// should just be left with initial -> final
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

}
//