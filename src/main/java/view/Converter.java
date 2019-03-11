package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

import model.FA_Converter_Panel;
import model.Regex_Converter_Panel;
import toolbox.Finite_Automata;
import toolbox.ReadWriteUtility;
import toolbox.State;

public class Converter {

	final static String REGEX_PANEL = "Convert Regex to FA";
	final static String FA_PANEL = "Convert FA to Regex";
	final static int extraWindowWidth = 100;

	private static int frameWidth;
	private static int frameHeight;

	private static JFrame frame;
	private JPanel regexCard;
	private JPanel faCard;

	private static ReadWriteUtility rwu;

	// regex to FA
	private JButton btnRegexConvert;
	private JTextField txtRegex;
	private Regex_Converter_Panel faDiagramRegexPanel;

	// FA to Regex
	private JButton btnFAConvert;
	private JTextArea txtInputAlphabet;
	private JTextArea txtStates;
	private FA_Converter_Panel faDiagramFAPanel;
	private JTable transitionTable;
	private JComboBox<String> initialStateCombo;
	private JList<String> finalStateList;
	private Finite_Automata faToConvert = null;
	
	public static void main(String[] args) {
		createAndShowGUI();
	}

	private static void createAndShowGUI() {
		// Create and set up the window.

		frame = new JFrame("Converter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		rwu = new ReadWriteUtility();

		// Create and set up the content pane.
		Converter converter = new Converter();
		converter.addComponentsToPane(frame.getContentPane());

		// Display the window.
		frame.pack();

		// frame.setSize(1000, 800);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		frameWidth = screenSize.width;
		frameHeight = screenSize.height;

		frame.setBounds(0, 0, frameWidth, frameHeight);

		// frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
	}

	public void addComponentsToPane(Container pane) {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(new Color(0, 153, 153));

		// Create the "cards".
		regexCard = new JPanel() {
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width += extraWindowWidth;
				return size;
			}
		};

		faCard = new JPanel();

		setUpRegexPanel();
		setUpFAPanel();

		regexCard.setName("RegexToFA");
		tabbedPane.addTab(REGEX_PANEL, regexCard);

		faCard.setName("FAToRegex");
		tabbedPane.addTab(FA_PANEL, faCard);

		pane.add(tabbedPane, BorderLayout.CENTER);

		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");

		ImageIcon saveIcon = new ImageIcon(new ImageIcon("src/resources/images/save_green.png").getImage()
				.getScaledInstance(15, 15, Image.SCALE_DEFAULT));
		ImageIcon openIcon = new ImageIcon(new ImageIcon("src/resources/images/open_green.png").getImage()
				.getScaledInstance(15, 15, Image.SCALE_DEFAULT));

		JMenuItem open = new JMenuItem("Open", openIcon);
		open.addActionListener(new OpenListener(tabbedPane));
		JMenuItem save = new JMenuItem("Save", saveIcon);
		save.addActionListener(new SaveListener(tabbedPane));

		JMenuItem help = new JMenuItem("Help");
		help.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new HelpFrame();
			}
		});
		
		file.add(open);
		file.add(save);
		menu.add(file);
		menu.add(help);

		pane.add(menu, BorderLayout.NORTH);
	}

	private void setUpRegexPanel() {
		regexCard.setLayout(new BorderLayout());

		JPanel northPanel = new JPanel(new FlowLayout());
		JPanel eastPanel = new JPanel(new FlowLayout());

		JLabel lblRegex = new JLabel("Enter regex:");
		txtRegex = new JTextField();
		txtRegex.setPreferredSize(new Dimension(600, 20));
		btnRegexConvert = new JButton("Convert");
		btnRegexConvert.addActionListener(new ConvertAction());

		btnRegexConvert.setBackground(new Color(0, 153, 153));
		btnRegexConvert.setBorder(BorderFactory.createRaisedBevelBorder());
		btnRegexConvert.setOpaque(true);
		btnRegexConvert.setPreferredSize(new Dimension(90, 20));

		northPanel.add(lblRegex);
		northPanel.add(txtRegex);
		northPanel.add(btnRegexConvert);

		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				faDiagramRegexPanel.drawNext();

			}

		});
		eastPanel.add(btnNext);

		btnNext.setBackground(new Color(0, 153, 153));
		btnNext.setBorder(BorderFactory.createRaisedBevelBorder());
		btnNext.setOpaque(true);
		btnNext.setPreferredSize(new Dimension(70, 20));

		regexCard.add(eastPanel, BorderLayout.EAST);
		regexCard.add(northPanel, BorderLayout.NORTH);

		faDiagramRegexPanel = new Regex_Converter_Panel();
		faDiagramRegexPanel.setPreferredSize(new Dimension(2000, 180));
		faDiagramRegexPanel.setNextButton(btnNext);

		JScrollPane scroll = new JScrollPane(faDiagramRegexPanel);

		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		faDiagramRegexPanel.repaint();
		regexCard.add(scroll, BorderLayout.CENTER);
	}

	private void setUpFAPanel() {
		faCard.setLayout(new BorderLayout());

		// fa panel
		faDiagramFAPanel = new FA_Converter_Panel();
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

		// *********
		clear.setBackground(new Color(0, 153, 153));
		clear.setBorder(BorderFactory.createRaisedBevelBorder());
		clear.setOpaque(true);
		clear.setPreferredSize(new Dimension(450, 20));

		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawTable(transitionPanel, txtStates.getText(), txtInputAlphabet.getText());
//				faDiagramFAPanel.resetRegexLabelText();
			}
		});
		transitionPanel.add(clear, BorderLayout.SOUTH);

		// input states and alphabet
		txtInputAlphabet = new JTextArea();
		txtStates = new JTextArea();
		txtInputAlphabet.setMinimumSize(new Dimension(200, 50));

		txtInputAlphabet.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				drawTable(transitionPanel, txtStates.getText(), txtInputAlphabet.getText());
			}
		});

		txtStates.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				drawTable(transitionPanel, txtStates.getText(), txtInputAlphabet.getText());

				String[] states = txtStates.getText().split("\n");

				initialStateCombo.removeAllItems();
				finalStateList.removeAll();

				ArrayList<String> statesSeen = new ArrayList<>();

				DefaultListModel<String> model = (DefaultListModel<String>) finalStateList.getModel();
				model.removeAllElements();
				for (String s : states) {
					s.trim();
					if (s.length() != 0 && !statesSeen.contains(s)) {
						initialStateCombo.addItem(s);
						model.addElement(s);
					}
					statesSeen.add(s);
				}
				finalStateList.setModel(model);
			}
		});

		JLabel lblAlphabet = new JLabel("Alphabet:");
		JLabel lblStates = new JLabel("States:");

		// right panel
		String[] tmp = { "-" };
		initialStateCombo = new JComboBox<String>(tmp);
		// ******************* unsure
		initialStateCombo.setBackground(new Color(0, 153, 153));

		JPanel initialStatePanel = new JPanel(new FlowLayout());
		initialStatePanel.add(new JLabel("Initial state:"));
		initialStatePanel.add(initialStateCombo);

		JPanel finalStatePanel = new JPanel(new BorderLayout());
		finalStatePanel.add(new JLabel("Final state(s):"), BorderLayout.NORTH);

		finalStateList = new JList<String>();
		finalStateList.setSelectionBackground(new Color(0, 153, 153));

		finalStateList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addElement("-");
		finalStateList.setModel(listModel);
		finalStatePanel.add(finalStateList, BorderLayout.CENTER);

		JScrollPane finalStateScroll = new JScrollPane(finalStatePanel);

		rightPanel.add(initialStatePanel, BorderLayout.NORTH);
		rightPanel.add(finalStateScroll, BorderLayout.CENTER);

		// *****************************
		btnFAConvert = new JButton("Convert");
		btnFAConvert.addActionListener(new ConvertAction());

		btnFAConvert.setBackground(new Color(0, 153, 153));
		btnFAConvert.setBorder(BorderFactory.createRaisedBevelBorder());
		btnFAConvert.setOpaque(true);

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

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(bottomScrollPane, BorderLayout.CENTER);

		
		// *************
		JPanel buttonPanel = new JPanel(new BorderLayout());
//		JPanel buttonPanel = new JPanel(new FlowLayout());
		
		

		JButton btnNext = new JButton("Next");

		btnNext.setBackground(new Color(0, 153, 153));
		btnNext.setBorder(BorderFactory.createRaisedBevelBorder());
		btnNext.setOpaque(true);

		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				faDiagramFAPanel.removeState();
			}
		});

		
		JLabel lblDone = new JLabel();
		faDiagramFAPanel.setDoneLabel(lblDone);
		
		buttonPanel.add(btnNext, BorderLayout.NORTH);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
//		p.add(Box.createRigidArea(new Dimension(5,0)));
		
		p.add(btnNext);
		btnNext.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnNext.setMaximumSize(new Dimension(70, 20));
		btnNext.setPreferredSize(new Dimension(70, 20));
		btnNext.setMinimumSize(new Dimension(70, 20));
//		btnNext.setPreferredSize(new Dimension(70, 20));
		p.add(lblDone);
		lblDone.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		buttonPanel.add(p, BorderLayout.NORTH);
		
//		buttonPanel.add(lblDone);
		
		
		
		
		
		
		JLabel lblRegex = new JLabel();

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Regex:"));
		panel.add(lblRegex);
		faDiagramFAPanel.setRegexLabel(lblRegex);

		
		
//		buttonPanel.add(btnBack);
		faDiagramFAPanel.setNextButton(btnNext);
//		faDiagramFAPanel.setBackButton(btnBack);
		
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		bottomPanel.add(panel, BorderLayout.SOUTH);

		// bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// bottomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScrollPane, bottomPanel);
		splitPane.setBackground(new Color(0, 153, 153));

		splitPane.setOneTouchExpandable(true);
		splitPane.setMinimumSize(new Dimension(frameWidth, frameHeight));

		faCard.add(splitPane, BorderLayout.CENTER);
	}
	
	

	class OpenListener implements ActionListener {

		private JTabbedPane pane;

		public OpenListener(JTabbedPane pane) {
			this.pane = pane;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			int index = pane.getSelectedIndex();
			Component tab = pane.getComponent(index);

			String name = tab.getName();

			String targetFolder = "/target";
			String workingDir = System.getProperty("user.dir");

			if (name == "RegexToFA") {
				File dir = new File(workingDir + targetFolder, "Saved Regexs");

				JFileChooser jfc = new JFileChooser(dir);
				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					String regex = rwu.ReadRegexFromFile(selectedFile.getName());
					txtRegex.setText(regex);
				}
			} else {
				// FAToRegex
				File dir = new File(workingDir + targetFolder, "Saved FAs");

				JFileChooser jfc = new JFileChooser(dir);
				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					String fa = rwu.ReadFAFromFile(selectedFile.getName());
					convert(fa);
				}
			}
		}

		private void convert(String stringFA) {
			String initialLbl = "\\{\"initialState\":\\{\"label\":";
			int initialStartPos = stringFA.indexOf(initialLbl);
			int posAfterInitial = initialStartPos + initialLbl.length();

			String initialState = stringFA.substring(posAfterInitial, stringFA.indexOf(",") - 1);

			String alphabetLbl = "inputAlphabet";

			String stateLbl = "states";
			int statesStartPos = stringFA.indexOf(stateLbl);
			int posAfterStates = statesStartPos + stateLbl.length(); // "states":[.....]

			int startIndex = stringFA.indexOf(alphabetLbl) + alphabetLbl.length() + 2;
			String inputAlphabetStr = stringFA.substring(startIndex, statesStartPos - 2);

			String stateList = stringFA.substring(posAfterStates + 3, stringFA.length() - 2);

			String[] stateArray = stateList.split("\\{\"label\":");

			ArrayList<String> states = new ArrayList<>();
			Map<String, Map<String, ArrayList<String>>> stateTransitions = new HashMap<>();

			for (String state : stateArray) {

				String transitionsLbl = "\"transitions\":";

				if (state.contains(transitionsLbl)) {
					int transitionsPos = state.indexOf(transitionsLbl);
					int posAfterTransitions = transitionsPos + transitionsLbl.length();

					String initialStatelbl = "\"isInitialState\":";
					int initialPos = state.indexOf(initialStatelbl);

					String label = state.substring(0, transitionsPos);
					String transitions = state.substring(posAfterTransitions, initialPos - 1);

					label = label.replaceAll(",", "");
					label = label.replaceAll("\"", "");

					transitions = transitions.replaceAll("\\{", "");
					transitions = transitions.replaceAll("\\}", "");

					String[] transitionArray = transitions.split("\\],");

					Map<String, ArrayList<String>> transitionMap = new HashMap<>();

					for (String t : transitionArray) {
						String[] b = t.split(":");

						ArrayList<String> lbls = new ArrayList<>();

						if (b.length > 0) {
							String to = b[0];
							String lbl = b[1];

							to = to.replaceAll("\"", "");
							lbl = lbl.replaceAll("\\[\"", "");
							lbl = lbl.replaceAll("\"\\]", "");
							lbl = lbl.replaceAll("\"", "");

							if (lbl.contains(",")) {
								// means there is more than one label
								String[] labels = lbl.split(",");

								for (String l : labels) {
									lbls.add(l);
								}
								transitionMap.put(to, lbls);

							} else {
								// single transition
								lbls.add(lbl);
								transitionMap.put(to, lbls);
							}
						}
					}
					stateTransitions.put(label, transitionMap);
					states.add(label);
				}
			}

			DefaultTableModel tableModel = (DefaultTableModel) transitionTable.getModel();
			tableModel.setRowCount(0);
			tableModel.setColumnCount(0);

			Vector<String> columns = new Vector<String>();
			Vector<String> row = new Vector<String>();

			tableModel.setColumnCount(states.size() + 1);
			columns.add(" ");
			
			inputAlphabetStr = inputAlphabetStr.replaceAll("\\[", "");
			inputAlphabetStr = inputAlphabetStr.replaceAll("\\]", "");

			String[] alphabetLetters = inputAlphabetStr.split(",");

			txtInputAlphabet.setText("");

			for (String str : alphabetLetters) {
				str = str.replaceAll("\"", "");
				txtInputAlphabet.append(str + "\n");
				
				columns.add(str);
			}
			columns.add("ε");
			
			for (String state : states) {
				
				row.add(state);
				Map<String, ArrayList<String>> transitionMap = stateTransitions.get(state);
				
				for (int i = 1; i < columns.size(); i++){
					
					String label = columns.get(i);
					String reachableStates = "";
					
					for (String stateTo : states){
						ArrayList<String> transitionsLabels = transitionMap.get(stateTo);
						
						if (transitionsLabels == null){
							reachableStates += " ";
						} else if (transitionsLabels.contains(label)){
							reachableStates += stateTo + ", ";
						} else {
							reachableStates += " ";
						}
					}
					
					reachableStates = reachableStates.trim();
				
					if (reachableStates.contains(",") && reachableStates.lastIndexOf(",") == reachableStates.length() -1){
						reachableStates = reachableStates.substring(0, reachableStates.length() -1);
					}
					
					row.addElement(reachableStates);
				}
				
				tableModel.addRow(row);
				row = new Vector<String>();
			}
			transitionTable.setModel(tableModel);
			tableModel.setColumnIdentifiers(columns);

			txtStates.setText("");
			finalStateList.removeAll();

			DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
			DefaultListModel<String> listModel = (DefaultListModel<String>) finalStateList.getModel();
			listModel.removeAllElements();

			for (String s : states) {
				txtStates.append(s + "\n");
				initialStateCombo.addItem(s);
				comboModel.addElement(s);
				listModel.addElement(s);
			}
			initialStateCombo.setModel(comboModel);
			initialStateCombo.setSelectedItem(initialState);
			finalStateList.setModel(listModel);
		}
	}

	class SaveListener implements ActionListener {

		private JTabbedPane pane;

		public SaveListener(JTabbedPane pane) {
			this.pane = pane;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			int index = pane.getSelectedIndex();
			Component tab = pane.getComponent(index);

			String name = tab.getName();

			if (name == "RegexToFA") {
				System.out.println("got regex tab");

				String fileName = JOptionPane.showInputDialog(frame, "Enter a name for the file", "Saving..",
						JOptionPane.WARNING_MESSAGE);

				if (fileName != null) {
					rwu.writeToFile(txtRegex.getText(), fileName);
				}

			} else {
				// FAToRegex
				System.out.println("got fa tab");

				if (faToConvert != null) {

					String fileName = JOptionPane.showInputDialog(frame, "Enter a name for the file", "Saving..",
							JOptionPane.WARNING_MESSAGE);

					if (fileName != null) {
						rwu.writeToFile(faToConvert, fileName);
					}

				} else {
					String message = "Please click convert button to create FA, and then save";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);

				}
			}
		}
	}

	private void drawTable(JPanel transitionPanel, String txtStates, String txtInputAlphabet) {
		DefaultTableModel model = (DefaultTableModel) transitionTable.getModel();

		transitionTable.setBackground(Color.LIGHT_GRAY);

		String[] states = txtStates.split("\n");
		String[] inputAlphabet = txtInputAlphabet.split("\n");

		model.setRowCount(0);
		Vector<String> row = new Vector<String>();
		Vector<String> columns = new Vector<String>();

		model.setColumnCount(0);
		model.setColumnCount(states.length);

		ArrayList<String> inputSeen = new ArrayList<>();

		columns.add(" ");
		for (String s : inputAlphabet) {
			if (!inputSeen.contains(s)) {
				columns.addElement(s);
			}
			inputSeen.add(s);
		}
		columns.add("ε");

		inputSeen = new ArrayList<>();

		for (int i = 0; i < states.length; i++) {
			String s = states[i];
			s = s.trim();

			if (s.length() != 0 && !inputSeen.contains(s)) {

				row.addElement(s);
				for (int j = 0; j < columns.size(); j++) {
					row.add(" ");
				}
				model.addRow(row);
				row = new Vector<String>();
			}
			inputSeen.add(s);
		}
		model.setColumnIdentifiers(columns);

		transitionPanel.revalidate();
		transitionPanel.repaint();
	}

	private class ConvertAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == btnRegexConvert) {
				String regex = txtRegex.getText();

				if (regex.length() == 0) {
					String message = "Regex cannot be empty";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					faDiagramRegexPanel.convert(regex);
					regexCard.repaint();
				}
			}

			if (e.getSource() == btnFAConvert) {
				drawAndConvertFA();
				faDiagramFAPanel.resetRegexLabelText();
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
								states.add(0, state); // want initial state to
														// be at front
							} else {
								states.add(state);
							}
						}

						transitionTable.clearSelection();
						int rows = transitionTable.getRowCount();
						int cols = transitionTable.getColumnCount();

						for (int i = 0; i < rows; i++) {
							for (int j = 1; j < cols; j++) {

								// if its valid, add transitions to states
								Object cellItem = transitionTable.getModel().getValueAt(i, j);

								String input = transitionTable.getColumnName(j);
								State stateFrom = getState(transitionTable.getValueAt(i, 0).toString(), states);

								if (cellItem != null) {
									String[] reachableStates = cellItem.toString().split(",");

									ArrayList<String> statesReached = new ArrayList<>();

									for (String stateLabel : reachableStates) {
										stateLabel = stateLabel.trim();

										if (!stateLabel.equals("") && !statesReached.contains(stateLabel)) {

											State stateTo = getState(stateLabel, states);
											if (stateTo != null) {
												// add tra sition from state to
												// this state, with column
												// header as the label
												stateFrom.addTransition(stateTo, input);

											} else {
												String message = "State " + stateLabel
														+ " is not part of the state list.";
												throwError(message);
												return;
											}
											statesReached.add(stateLabel);
										}
									}

								}
							}
						}
						// it will all be initialised at this point, since its
						// inside all of the if statements
						faToConvert = createFA(initialState, finalStates, states, inputAlphabet);
						faDiagramFAPanel.convert(faToConvert);

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
