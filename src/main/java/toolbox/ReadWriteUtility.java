package toolbox;

import java.awt.Frame;
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
	private Frame frame;

	private Gson gson = new Gson();

	public ReadWriteUtility(Frame frame) {
		this.frame = frame;
	}

	private File createFAFolder() {
		File dir = new File(workingDir + targetFolder, "Saved FAs");
		dir.mkdir();
		return dir;
	}

	private File createRegexFolder() {
		File dir = new File(workingDir + targetFolder, "Saved Regex's");
		dir.mkdir();
		return dir;
	}

	public void writeToFile(Finite_Automata fa, String filename) {
		String faString = gson.toJson(fa);

		File folder = createFAFolder();

		boolean check = checkName(folder, filename);

		while (check) {
			filename = JOptionPane.showInputDialog(frame,
					"File '" + filename + "' already exists. Enter a different name for the file:", "Saving..",
					JOptionPane.WARNING_MESSAGE);

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
		File folder = createRegexFolder();

		boolean check = checkName(folder, filename);

		while (check) {
			filename = JOptionPane.showInputDialog(frame,
					"File '" + filename + "' already exists. Enter a different name for the file:", "Saving..",
					JOptionPane.WARNING_MESSAGE);

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
