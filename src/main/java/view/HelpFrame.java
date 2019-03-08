package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class HelpFrame {

	private JFrame frame;
	private JPanel helpCard;
	private JPanel regexCard;
	private JPanel faCard;
	private Font font = new Font("Verdana", Font.PLAIN, 15);

	public HelpFrame() {
		frame = new JFrame("Help");

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();

		int width = 800;
		int height = 560;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		int xpos = (screenWidth / 2) - (width / 2);
		int ypos = (screenHeight / 2) - (height / 2);

		frame.setBounds(xpos, ypos, width, height);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);

		addComponentsToPane(frame.getContentPane());

		frame.setVisible(true);
	}
	
	private void addComponentsToPane(Container pane) {

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(new Color(0, 153, 153));

		helpCard = new JPanel(new BorderLayout());
		regexCard = new JPanel(new BorderLayout());
		faCard = new JPanel(new BorderLayout());

		helpCard.setName("Help");
		regexCard.setName("RegexToFA Help");
		faCard.setName("FAToRegex Help");

		addToHelpPane();
		addToRegexHelpPane();
		addToFaHelpPane();

		tabbedPane.addTab("Help", helpCard);
		tabbedPane.addTab("Regex to FA Help", regexCard);
		tabbedPane.addTab("FA to Regex Help", faCard);

		pane.add(tabbedPane, BorderLayout.CENTER);

	}

	private void addToHelpPane() {
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		
		JEditorPane upperEditorPane = new JEditorPane();
		upperEditorPane.setEditable(false);
		upperEditorPane.setBackground(Color.LIGHT_GRAY);
		upperEditorPane.setFont(font);
		
		upperEditorPane.setBorder(BorderFactory.createEtchedBorder(new Color(0, 153, 153), Color.DARK_GRAY));
		
		String text = "This tool demonstrates the steps for conversion between regular expressions"
				+ " and finite automata.\n\n"
				+ "The 'RegexToFa' page shows the steps of building a finite automata for a regex input\n"
				+ "The 'FaToRegex' page demonstrates the state elimination method to obtain the equivalent "
				+ "regular\nexpression\n";
		
		
		upperEditorPane.setText(text);
		
		JEditorPane lowerEditorPane = new JEditorPane();
		lowerEditorPane.setEditable(false);
		lowerEditorPane.setBackground(Color.LIGHT_GRAY);
		lowerEditorPane.setFont(font);
		lowerEditorPane.setBorder(BorderFactory.createEtchedBorder(new Color(0, 153, 153), Color.DARK_GRAY));

		JLabel label = new JLabel("Saving and Loading...");
		text = "The regex and automaton which you entered can be saved and reopened in the future for use.\n"
				+ "To do this, click 'File' and choose either 'Save' or 'Open'.\n\n"
				
				+ "Saving...\n"
				+ "When saving a file, you will be notified if you choose an existing file name.\n"
				+ "You can exit at any time by pressing cancel or the 'x' button in the top left of the popup.\n"
				+ "When saving a finite automaton you will need to fill out all input boxes, and then click the convert\n"
				+ "button. This is to ensure all the appropriate checks have been made on your input data.\n\n"
				
				+ "Opening...\n"
				+ "When opening a file, you will be presented with the list of saved files. Double click on any to open it.";
		
		
		lowerEditorPane.setText(text);
		
		panel1.add(upperEditorPane, BorderLayout.CENTER);
		panel2.add(label, BorderLayout.NORTH);
		panel2.add(lowerEditorPane, BorderLayout.CENTER);
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(panel1, BorderLayout.NORTH);
		p.add(panel2, BorderLayout.CENTER);
		
		helpCard.add(p, BorderLayout.CENTER);
	}

	private void addToRegexHelpPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(new Color(0, 153, 153));

		JPanel regexHelpCard = new JPanel(new BorderLayout());
		JPanel validCard = new JPanel(new BorderLayout());
		JPanel unionCard = new JPanel(new BorderLayout());
		JPanel kleeneCard = new JPanel(new BorderLayout());

		regexHelpCard.setName("Converting a regex");
		validCard.setName("Valid Regex");
		unionCard.setName("Union |");
		kleeneCard.setName("Kleene Star *");
		
		addToRegexCard(regexHelpCard);
		addToValidCard(validCard);
		addToUnionCard(unionCard);
		addToKleeneCard(kleeneCard);

		tabbedPane.addTab("Converting a regex", regexHelpCard);
		tabbedPane.addTab("Valid Regex", validCard);
		tabbedPane.addTab("Union |", unionCard);
		tabbedPane.addTab("Kleene Star *", kleeneCard);

		regexCard.add(tabbedPane, BorderLayout.CENTER);
		
	}
	
	private void addToRegexCard(JPanel panel){
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(Color.LIGHT_GRAY);
		editorPane.setFont(font);
		editorPane.setBorder(BorderFactory.createEtchedBorder(new Color(0, 153, 153), Color.DARK_GRAY));

		String text = "To see the steps of how to convert a regular expression into a finite automata enter "
				+ "a valid regex\ninto the text box and press the convert button.\n\n"
				+ "The tool will display the conversion steps of how to build up to the completed FA.\n\n"
				+ "Three FAs will be displayed, with the final of the three showing how the 2 above are combined "
				+ "to\ncreate a new automaton.\n"
				+ "Sometimes only two automata may be presented, in the case for the Kleene star operator *, since\n"
				+ "this works on a single automaton, whereas union and concatenation combine 2 automata.\n\n"
				
				+ "Press the 'next' button to display the process of building the automaton, and scroll to look\n"
				+ "through all steps."
				+ "Once the conversion is complete, the next button will be disabled, and the final\n"
				+ "diagram is the converted finite automata.\n\n";
		
		editorPane.setText(text);
		
		panel.add(editorPane, BorderLayout.CENTER);
	}
	
	private void addToValidCard(JPanel panel){
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(Color.LIGHT_GRAY);
		editorPane.setFont(font);
		editorPane.setBorder(BorderFactory.createEtchedBorder(new Color(0, 153, 153), Color.DARK_GRAY));

		String text = "A valid regular expression may only consist of the following characters\n"
				+ "\t - Numbers: 0-9\n"
				+ "\t - Letters: a-z, A-Z\n"
				+ "\t - Parentheses: ( )\n"
				+ "\t - Union symbol: |\n"
				+ "\t - Kleene Star symbol: *\n\n"
				
				+ "Parentheses must match for the regex to be valid, i.e. the following examples are invalid:\n"
				+ "\t - (\n"
				+ "\t - )\n"
				+ "\t - )(\n"
				+ "\t - ()(\n"
				+ "\t - ()((\n"
				+ "\t etc.\n\n"
				
				+ "If any illegal characters appear in the regex, or the operators are used incorrectly "
				+ "the input will be\nrejected, and you will be prompted to enter a valid regex.";
		
		editorPane.setText(text);
		
		panel.add(editorPane, BorderLayout.CENTER);
	}
	private void addToUnionCard(JPanel panel){
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(Color.LIGHT_GRAY);
		editorPane.setFont(font);
		editorPane.setBorder(BorderFactory.createEtchedBorder(new Color(0, 153, 153), Color.DARK_GRAY));

		String text = "The Union operator is denoted using the bar symbol |.\n\n"
				+ "Union has a weak binding, meaning that brackets are important;\n"
				+ "\t - a|b - means a OR b\n"
				+ "\t - a|ba - means a OR ba\n"
				+ "\t - (a|b)a - means a OR B, followed by a\n\n"
				
				+ "The operator must be preceded and followed by valid regular expressions. Valid examples:\n"
				+ "\t - abc|def\n" 
				+ "\t - a*|b*\n" 
				+ "\t - (abc)*|(cd)*e\n\n";
		
		
		editorPane.setText(text);
		
		panel.add(editorPane, BorderLayout.CENTER);
	}
	private void addToKleeneCard(JPanel panel){
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(Color.LIGHT_GRAY);
		editorPane.setFont(font);
		editorPane.setBorder(BorderFactory.createEtchedBorder(new Color(0, 153, 153), Color.DARK_GRAY));

		String text = "The Kleene star operator is denoted using the star symbol *.\n\n"
				+ "The Kleene star has a strong binding, meaning that brackets are important;\n"
				+ "\t - a* - means 0 or more occurrences of a\n"
				+ "\t - ab* - means a, followed by 0 or more occurrences of b\n"
				+ "\t - (ab)* - means 0 or more occurrences of ab\n\n"

				+ "The operator must be preceded and followed by valid regular expressions. Valid examples:\n"
				+ "\t - ab*\n" 
				+ "\t - (a|b)*\n" 
				+ "\t - (a|b)*a*\n\n";
		
		editorPane.setText(text);
		
		panel.add(editorPane, BorderLayout.CENTER);
	}

	private void addToFaHelpPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(Color.LIGHT_GRAY);
		editorPane.setFont(font);
		editorPane.setBorder(BorderFactory.createEtchedBorder(new Color(0, 153, 153), Color.DARK_GRAY));
		
		String text = "To build an automaton enter details into the input boxes. All boxes require input.\n\n"
				
				+ "Input Alphabet and States...\n"
				+ "For the input alphabet and states, separate the inputs with newline characters.\n"
				+ "Short state names are preferred, since these will appear inside the states, e.g. 1 or s1 instead of\n"
				+ "state1.\n\n"

				+ "Transition table...\n"
				+ "The transition table allows for input of the state, or set of states, which are reachable using that\n"
				+ "input char. If more than one state is reachable, separate them with a comma.\n"
				+ "e.g. for row '1', column 'a' - enter 1, 2, 3 to indicate that there are transitions from state 1 to states\n"
				+ "1, 2, and 3 labelled 'a'.\n"
				+ "You can include empty jumps using the last column of the table. Or leave it blank to draw a\n"
				+ "deterministic finite automata.\n\n"

				+ "Initial and Final states...\n"
				+ "The initial state is indicated using the drop down box in the top right.\n"
				+ "The final state is indicated by choosing a state from the list. More than one state can be chosen by\n"
				+ "holding down the 'command' button on your keyboard and selecting multiple states.\n"
				+ "The tool doesnt allow for an automaton with no final states - since the automaton would accept no\n"
				+ "words, meaning no conversion is needed.\n\n"

				+ "Press the 'next' button to display the process of state removal, and scroll to look through all steps.";
		
		editorPane.setText(text);
		
		faCard.add(editorPane, BorderLayout.CENTER);
	}

}
