package toolbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * Handles reading from files and writing to files of regexes and FAs
 * 
 * @author Jaydene Green-Stevens
 *
 */
public class Read_Write_Utility {
	private String workingDir = System.getProperty("user.dir");

	/**
	 * Creates a folder for the FAs if it doesnt already exist. Otherwise
	 * returns the existing folder
	 * 
	 * @return the folder
	 */
	private File createFAFolder() {
		File dir = new File(workingDir, "Saved FAs");
		dir.mkdir();
		return dir;
	}

	/**
	 * Creates a folder for regexes if it doesnt already exist. Otherwise
	 * returns the existing folder
	 * 
	 * @return the folder
	 */
	private File createRegexFolder() {
		File dir = new File(workingDir, "Saved Regexs");
		dir.mkdir();
		return dir;
	}

	/**
	 * Writes an FA to a file
	 * 
	 * @param inputAlphabet
	 *            - list of inputs as a string
	 * @param states
	 *            - list of states as a string
	 * @param transitionTable
	 *            - the table containing the transitions
	 * @param initialState
	 *            - the index of the initial state
	 * @param finalStates
	 *            - index array of all final states
	 * @param filename
	 *            - name of the file
	 */
	public void writeToFile(String inputAlphabet, String states, JTable transitionTable, int initialState,
			int[] finalStates, String filename) {

		String[] options = { "New name", "Replace file" };

		File folder = createFAFolder();

		boolean check = checkName(folder, filename);

		while (check) {

			int result = JOptionPane.showOptionDialog(null,
					"File '" + filename + "' already exists. Replace the file, or enter a different name:", "Saving...",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, null);

			if (result == JOptionPane.YES_OPTION) {
				filename = JOptionPane.showInputDialog("Enter a new name for the file:");
			} else {

				if (checkName(folder, filename)) {
					File file = new File(folder, filename + ".txt");
					file.delete();
				}
			}

			check = checkName(folder, filename);
		}

		File file = new File(folder, filename + ".txt");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			ArrayList<String> inputAlphArray = splitIntoArrayList(inputAlphabet);
			ArrayList<String> stateArray = splitIntoArrayList(states);

			ArrayList<String> finalStatesList = new ArrayList<>();
			for (int index : finalStates) {
				finalStatesList.add(stateArray.get(index));
			}

			// transitions
			// rows - states
			// cols - input alphabet
			ArrayList<String> transitions = new ArrayList<>();

			transitionTable.clearSelection();
			int rows = transitionTable.getRowCount();
			int cols = transitionTable.getColumnCount();

			for (int i = 0; i < rows; i++) {
				for (int j = 1; j < cols; j++) {

					// if its valid, add transitions to states
					Object cellItem = transitionTable.getModel().getValueAt(i, j);

					String input = transitionTable.getColumnName(j);
					String stateFrom = transitionTable.getValueAt(i, 0).toString();

					if (cellItem != null && cellItem != " ") {

						// add it to the list of transitions
						cellItem = ((String) cellItem).replaceAll(",", " ");
						transitions.add(stateFrom + ":" + input + ">" + cellItem);
					}
				}
			}

			bw.write("Input Alphabet:" + inputAlphArray + "\n");
			bw.write("States:" + stateArray + "\n");
			bw.write("Transitions:" + transitions + "\n");
			bw.write("Initial State:" + stateArray.get(initialState) + "\n");
			bw.write("Final State(s):" + finalStatesList.toString() + "\n");

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Splits string input into an array list
	 * @param input
	 * @return the array list
	 */
	private ArrayList<String> splitIntoArrayList(String input) {
		String[] itemArray = input.split("\n");

		ArrayList<String> list = new ArrayList<>();
		for (String item : itemArray) {
			item = item.trim();

			if (item.length() != 0) {
				list.add(item);
			}
		}
		return list;
	}

	/** 
	 * Writes the regex to a file
	 * @param regex
	 * @param filename 
	 */
	public void writeToFile(String regex, String filename) {
		String[] options = { "New name", "Replace file" };

		File folder = createRegexFolder();
		boolean check = checkName(folder, filename);

		while (check) {
			int result = JOptionPane.showOptionDialog(null,
					"File '" + filename + "' already exists. Replace the file, or enter a different name:", "Saving...",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, null);

			if (result == JOptionPane.YES_OPTION) {
				filename = JOptionPane.showInputDialog("Enter a new name for the file:");
			} else {

				if (checkName(folder, filename)) {
					File file = new File(folder, filename + ".txt");
					file.delete();
				}
			}

			check = checkName(folder, filename);
		}

		File file = new File(folder, filename + ".txt");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(regex);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Checks to see if the file to be read from exists
	 * @param folder
	 * @param filename
	 * @return whetehr or not it exists
	 */
	private boolean checkName(File folder, String filename) {
		return new File(folder, filename + ".txt").exists();
	}

	/**
	 * Reads the regex stored inside the file
	 * @param filename
	 * @return the file contents
	 */
	public String ReadRegexFromFile(String filename) {
		File folder = createRegexFolder();

		boolean check = new File(folder, filename).exists();

		if (check) {
			File file = new File(folder, filename);

			try {

				InputStream is = new FileInputStream(file);
				BufferedReader buf = new BufferedReader(new InputStreamReader(is));

				String line = buf.readLine();
				StringBuilder sb = new StringBuilder();

				while (line != null) {
					sb.append(line);
					line = buf.readLine();
				}
				buf.close();

				return sb.toString();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return "";
	}

	/**
	 * Reads the FA stored in the file
	 * @param filename
	 * @return the contents of the file
	 */
	public String ReadFAFromFile(String filename) {
		File folder = createFAFolder();

		boolean check = new File(folder, filename).exists();

		if (check) {
			File file = new File(folder, filename);

			try {

				InputStream is = new FileInputStream(file);
				BufferedReader buf = new BufferedReader(new InputStreamReader(is));

				String line = buf.readLine();
				StringBuilder sb = new StringBuilder();

				while (line != null) {
					sb.append(line);
					line = buf.readLine();
				}
				buf.close();

				return sb.toString();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

}
