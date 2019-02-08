package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.ArrayUtils;

import fa_to_regex.FA_to_Regex;
import regex_to_fa.Regex_to_FA;
import toolbox.Finite_Automata;
import toolbox.State;

public class Converter {

	final static String REGEX_PANEL = "Convert Regex to FA";
	final static String FA_PANEL = "Convert FA to Regex";
	final static int extraWindowWidth = 100;

	final static int frameWidth = 1000;
	final static int frameHeight = 800;

	private static JFrame frame;

	private JPanel regexCard;
	private JPanel faCard;

	// regex to FA
	private JButton btnRegexConvert;
	private JTextField txtField;
	private RegexConverterPanel faDiagramRegexPanel;

	// FA to Regex
	private JButton btnFAConvert;
	private JTextArea txtInputAlphabet;
	private JTextArea txtStates;
	private FAConverterPanel faDiagramFAPanel;
	private JTable transitionTable;
	private JComboBox<String> initialStateCombo;
	private JList<String> finalStateList;

	public static void main(String[] args) {
		createAndShowGUI();
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("Converter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		Converter converter = new Converter();
		converter.addComponentsToPane(frame.getContentPane());

		// Display the window.
		frame.pack();

		// frame.setSize(1000, 800);
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
	}

	public void addComponentsToPane(Container pane) {
		JTabbedPane tabbedPane = new JTabbedPane();

		// Create the "cards".
		regexCard = new JPanel() {
			// Make the panel wider than it really needs, so
			// the window's wide enough for the tabs to stay
			// in one row.
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width += extraWindowWidth;
				return size;
			}
		};

		faCard = new JPanel();

		setUpRegexPanel();
		setUpFAPanel();

		tabbedPane.addTab(REGEX_PANEL, regexCard);
		tabbedPane.addTab(FA_PANEL, faCard);

		pane.add(tabbedPane, BorderLayout.CENTER);
	}

	private void setUpRegexPanel() {
		regexCard.setLayout(new BorderLayout());

		JPanel panel = new JPanel(new FlowLayout());

		JLabel lblRegex = new JLabel("Enter regex:");
		txtField = new JTextField();
		txtField.setPreferredSize(new Dimension(600, 20));
		btnRegexConvert = new JButton("Convert");
		btnRegexConvert.addActionListener(new ConvertAction());

		panel.add(lblRegex);
		panel.add(txtField);
		panel.add(btnRegexConvert);

		regexCard.add(panel, BorderLayout.NORTH);

		faDiagramRegexPanel = new RegexConverterPanel();

		faDiagramRegexPanel.repaint();
		regexCard.add(faDiagramRegexPanel, BorderLayout.CENTER);
	}

	private void setUpFAPanel() {
		faCard.setLayout(new BorderLayout());

		// fa panel
		faDiagramFAPanel = new FAConverterPanel();
		faDiagramFAPanel.setPreferredSize(new Dimension(1500, 180));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setPreferredSize(new Dimension(800, 300));
		JPanel leftPanel = new JPanel(new GridLayout(2, 1, 10, 10));
		JPanel middlePanel = new JPanel(new FlowLayout());
		JPanel rightPanel = new JPanel(new BorderLayout());

		// transition panel
		JPanel transitionPanel = new JPanel(new BorderLayout());
		transitionPanel.setPreferredSize(new Dimension(500, 250));

		transitionTable = new JTable();
		Object[] columns = { "Enter some states" };
		final DefaultTableModel model = new DefaultTableModel();
		model.setColumnIdentifiers(columns);
		transitionTable.setModel(model);

		JLabel lblTransitions = new JLabel("Transitions:");
		transitionPanel.add(lblTransitions, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(transitionTable);
		transitionPanel.add(scroll, BorderLayout.CENTER);

		JButton clear = new JButton("Clear table");
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawTable(transitionPanel, "");
			}
		});
		transitionPanel.add(clear, BorderLayout.SOUTH);

		// input states and alphabet
		txtInputAlphabet = new JTextArea();
		txtStates = new JTextArea();
		txtInputAlphabet.setMinimumSize(new Dimension(200, 50));

		txtStates.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				drawTable(transitionPanel, txtStates.getText());

				String[] states = txtStates.getText().split("\n");

				initialStateCombo.removeAllItems();
				finalStateList.removeAll();

				DefaultListModel<String> model = (DefaultListModel<String>) finalStateList.getModel();
				model.removeAllElements();
				for (String s : states) {
					s.trim();
					if (s.length() != 0) {
						initialStateCombo.addItem(s);
						model.addElement(s);
					}
				}
				finalStateList.setModel(model);

				// finalStateList.add
				// rightPanel.repaint();
			}

		});

		JLabel lblAlphabet = new JLabel("Alphabet:");
		JLabel lblStates = new JLabel("States:");

		// right panel
		String[] tmp = { "-" };
		initialStateCombo = new JComboBox<String>(tmp);

		JPanel initialStatePanel = new JPanel(new FlowLayout());
		initialStatePanel.add(new JLabel("Initial state:"));
		initialStatePanel.add(initialStateCombo);

		JPanel finalStatePanel = new JPanel(new BorderLayout());
		finalStatePanel.add(new JLabel("Final state(s):"), BorderLayout.NORTH);

		finalStateList = new JList<String>();

		finalStateList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addElement("-");
		finalStateList.setModel(listModel);
		finalStatePanel.add(finalStateList, BorderLayout.CENTER);

		JScrollPane finalStateScroll = new JScrollPane(finalStatePanel);

		rightPanel.add(initialStatePanel, BorderLayout.NORTH);
		rightPanel.add(finalStateScroll, BorderLayout.CENTER);

		btnFAConvert = new JButton("Convert");
		btnFAConvert.addActionListener(new ConvertAction());

		JPanel alphabetPanel = new JPanel(new BorderLayout());
		JPanel statePanel = new JPanel(new BorderLayout());

		alphabetPanel.add(lblAlphabet, BorderLayout.NORTH);
		alphabetPanel.add(txtInputAlphabet, BorderLayout.CENTER);

		statePanel.add(lblStates, BorderLayout.NORTH);
		statePanel.add(txtStates, BorderLayout.CENTER);

		JScrollPane alphabetScrollPane = new JScrollPane(alphabetPanel);
		JScrollPane stateScrollPane = new JScrollPane(statePanel);

		leftPanel.add(alphabetScrollPane);
		leftPanel.add(stateScrollPane);

		stateScrollPane.setPreferredSize(new Dimension(150, 300));

		middlePanel.add(transitionPanel);
		rightPanel.add(btnFAConvert, BorderLayout.SOUTH);

		topPanel.add(leftPanel, BorderLayout.WEST);
		topPanel.add(middlePanel, BorderLayout.CENTER);
		topPanel.add(rightPanel, BorderLayout.EAST);

		JScrollPane topScrollPane = new JScrollPane(topPanel);
		JScrollPane bottomScrollPane = new JScrollPane(faDiagramFAPanel);
		bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScrollPane, bottomScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setMinimumSize(new Dimension(frameWidth, frameHeight));

		faCard.add(splitPane, BorderLayout.CENTER);
	}

	private void drawTable(JPanel transitionPanel, String txtStates) {
		DefaultTableModel model = (DefaultTableModel) transitionTable.getModel();

		transitionTable.setBackground(Color.LIGHT_GRAY);

		model.setRowCount(0);
		Vector<String> row = new Vector<String>();
		Vector<String> columns = new Vector<String>();

		String[] states = txtStates.split("\n");

		model.setColumnCount(0);
		model.setColumnCount(states.length);

		columns.add(" ");

		for (int i = 0; i < states.length; i++) {
			String s = states[i];
			s.trim();

			if (s.length() != 0) {

				row.addElement(s);
				for (int j = 0; j < states.length - 1; j++) {
					row.add(" ");
				}
				columns.addElement(s);

				model.addRow(row);
				row = new Vector<String>();
			}
		}
		model.setColumnIdentifiers(columns);

		transitionPanel.revalidate();
		transitionPanel.repaint();
	}

	private class ConvertAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnRegexConvert) {
				String regex = txtField.getText();
				faDiagramRegexPanel.convertToFa(regex);

				faDiagramRegexPanel.repaint();
				regexCard.repaint();
			}

			if (e.getSource() == btnFAConvert) {
				drawAndConvertFA();
			}
		}

		private void drawAndConvertFA() {
			// input alphabet
			State initialState = null;
			ArrayList<State> finalStates = new ArrayList<State>();
			ArrayList<State> states = new ArrayList<>();

			ArrayList<String> inputAlphabet = splitIntoArrayList(txtInputAlphabet.getText());
			if (validListInput(inputAlphabet)) {

				// states
				ArrayList<String> stateLabels = splitIntoArrayList(txtStates.getText());
				if (validListInput(stateLabels)) {

					// final states
					ArrayList<String> finalStateLabels = new ArrayList<String>();
					finalStateLabels.addAll(finalStateList.getSelectedValuesList());

					if (validListInput(finalStateLabels)) {

						for (String label : stateLabels) {
							State state = createState(label);

							if (finalStateLabels.contains(label)) {
								state.setFinal(true);
								finalStates.add(state);
							}

							String initial = initialStateCombo.getSelectedItem().toString();
							if (initial.equals(label)) {
								initialState = state;
								state.setInitial(true);
							}
							states.add(state);
						}

						// transitions
						transitionTable.clearSelection();
						int rows = transitionTable.getRowCount();
						int cols = transitionTable.getColumnCount();

						for (int i = 0; i < rows; i++) {
							for (int j = 1; j < cols; j++) {

								// if its valid, add transitions to states
								Object cellItem = transitionTable.getModel().getValueAt(i, j);
								if (cellItem != null) {
									String transitionText = cellItem.toString();

									String[] transition = transitionText.split(",");
									for (String label : transition) {
										label = label.trim();

										if (!label.equals("")) {
											if (inputAlphabet.contains(label)) {
												String stateLabel = transitionTable.getValueAt(i, 0).toString();
												// gets row title
												String state2Label = transitionTable.getColumnName(j);
												// gets col title
												State from = getState(stateLabel, states);
												State to = getState(state2Label, states);

												if (from != null && to != null) {
													from.addTransition(to, label);
													System.out.println(
															"adding transition: " + from + " -" + label + "-> " + to);
												}

											} else {
												String message = "Transition label " + label
														+ " is not part of the input language.";
												throwError(message);
												return;

											}
										}
									}
								}
							}
						}
						// it will all be initialised at this point, since its
						// inside all of the if statements
						System.out.println("creating fa");
						Finite_Automata fa = createFA(initialState, finalStates, states, inputAlphabet);
						faDiagramFAPanel.convertToRegex(fa);

					} else {
						String message = "Please choose a final state. To select multiple hold the 'command' key";
						throwError(message);
					}
				} else {
					String message = "Enter states";
					throwError(message);
				}
			} else {
				String message = "Input alphabet cannot be empty";
				throwError(message);
			}
		}

		private State getState(String label, ArrayList<State> states) {
			for (State state : states) {
				if (state.getLabel().equals(label)) {
					return state;
				}
			}
			return null;
		}

		private void throwError(String message) {
			JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		private boolean validListInput(List<String> list) {
			boolean valid = true;

			if (list.size() == 0 || list.get(0).equals("")) {
				valid = false;
			}
			return valid;
		}

		private ArrayList<String> splitIntoArrayList(String txtInput) {
			String[] inputArray = txtInput.split("\n");

			ArrayList<String> list = new ArrayList<>();
			for (String input : inputArray) {
				input = input.trim();

				if (input.length() != 0) {
					list.add(input);
				}
			}
			return list;
		}

		private Finite_Automata createFA(State initialState, ArrayList<State> finalStates, ArrayList<State> states,
				ArrayList<String> inputAlphabet) {
			return new Finite_Automata(initialState, finalStates, states, inputAlphabet);
		}

		private State createState(String label) {
			return new State(label);
		}

	}

}

class RegexConverterPanel extends JPanel {

	private Finite_Automata fa = null;

	public RegexConverterPanel() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (fa != null) {
			FA_Drawer drawer = new FA_Drawer();
			drawer.drawFA(g, fa);
		}
	}

	public void convertToFa(String regex) {
		Regex_to_FA converter = new Regex_to_FA();
		Finite_Automata fa = converter.convertToFA(regex);

		this.fa = fa;

		repaint();
	}

}

class FAConverterPanel extends JPanel {

	private String regex = "";
	private Finite_Automata fa = null;

	public FAConverterPanel() {
		setBackground(Color.white);
		setMinimumSize(new Dimension(100, 300));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (fa != null) {
			FA_Drawer drawer = new FA_Drawer();
			drawer.drawFA(g, fa);
		}
		if (fa != null) {

			FA_Drawer drawer = new FA_Drawer();
			drawer.drawFA(g, fa);
			g.drawString(regex, 50, 50);

		}
	}

	public void convertToRegex(Finite_Automata fa) {
		this.fa = fa;

		FA_to_Regex converter = new FA_to_Regex();
		// this.regex = converter.convert(fa);

		// take in details of fa
		// draw fa on screen
		// convert fa to regex
		// display regex

		repaint();
	}

}
