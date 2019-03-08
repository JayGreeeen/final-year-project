package toolbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class ReadWriteUtility {
	private String targetFolder = "/target";
	private String workingDir = System.getProperty("user.dir");

	private Gson gson = new Gson();

	private File createFAFolder() {
		File dir = new File(workingDir + targetFolder, "Saved FAs");
		dir.mkdir();
		return dir;
	}

	private File createRegexFolder() {
		File dir = new File(workingDir + targetFolder, "Saved Regexs");
		dir.mkdir();
		return dir;
	}

	public void writeToFile(Finite_Automata fa, String filename) {
		String[] options = { "New name", "Replace file" };

		String faString = gson.toJson(fa);

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
			bw.write(faString);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

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

	private boolean checkName(File folder, String filename) {
		return new File(folder, filename + ".txt").exists();
	}

	public String ReadRegexFromFile(String filename) {
		File folder = createRegexFolder();

		boolean check = new File(folder, filename).exists();

		if (check) {
			File file = new File(folder, filename);

			InputStreamReader inputStreamReader;
			try {
				inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
				JsonReader reader = new JsonReader(inputStreamReader);

				String regex = gson.fromJson(reader, String.class);

				return regex;

			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

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
