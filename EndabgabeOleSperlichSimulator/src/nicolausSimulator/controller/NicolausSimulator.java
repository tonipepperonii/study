package nicolausSimulator.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class NicolausSimulator extends Application {
	public static final String DIRECTORY = "programs" + File.separator;

	private ArrayList<String> programs = new ArrayList<String>();
	private ArrayList<String> openPrograms = new ArrayList<String>();

	public void run() {
		launch();
	}

	/**
	 * loadProgramNames from programs\ directory, start first program with available
	 * new name
	 */
	@Override
	public void start(Stage primaryStage) {
		programs = loadProgramNames();

		String nextName = generateNewName();
		Program program = new Program(nextName, this);
		program.start();
		openPrograms.add(program.getName());
		
	}

	/**
	 * start a new program with a given name
	 * @param name
	 * @return
	 */
	public boolean startNewProgram(String name) {
		if (!openPrograms.contains(name) && !programs.contains(name)) {
			Program program = new Program(name, this);
			program.start();
			openPrograms.add(program.getName());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * read File, create new Program
	 * @param file
	 * @return true on success
	 */
	public boolean openProgram(File file) {
		if (file == null) {
			return false;
		}
		String fileName = file.getName().replaceAll(".java", "");
		if (!openPrograms.contains(fileName)) {
			Program program = new Program(fileName, this);
			program.start();
			program.setCode(readFile(file, program));
			program.setFileAndSavedFlag(file);
			openPrograms.add(program.getName());
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * write editor text in given file
	 * @param program
	 * @param file
	 * @param text
	 * @return
	 */
	public boolean saveProgram(Program program, File file, String text) {
		if (file == null) {
			return false;
		}
		
		
		try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
			String name = file.getName().replaceAll(".java", "");
			program.setName(name);
			out.write(getPrefix(program) + text + getPostfix());
			if (!program.hasSavedFlag()) {
				program.setFileAndSavedFlag(file);
			}
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * close one program/stage. close whole simulator if no program is left
	 * @param program
	 */
	public void closeProgram(Program program) {
		program.close();
		openPrograms.remove(program.getName());
		if (openPrograms.isEmpty()) {
			Platform.exit();
		}
	}

	/**
	 * read file, extract text which shall be written in editor
	 * @param file
	 * @param program
	 * @return file content without prefix and postfix
	 */
	private String readFile(File file, Program program) {
		if (file == null) {
			return null;
		}
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {

			StringBuilder textBuilder = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				textBuilder.append(line);
				textBuilder.append(System.getProperty("line.separator"));
			}
			String text = textBuilder.toString();
			text = text.substring(getPrefix(program).length(), text.length() - getPostfix().length() - 2);
			text = text.trim();

			String fileName = file.getName().replace(".java", "");
			program.setName(fileName);

			return text;
		} catch (IOException e) {
			return "Datei konnte nicht geladen werden!";
		}
	}

	/**
	 * load program names from programs\ directory
	 * @return
	 */
	private static ArrayList<String> loadProgramNames() {
		ArrayList<String> programNames = new ArrayList<String>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(DIRECTORY))) {
			Path programsDir = Paths.get(DIRECTORY);
			if (!Files.exists(programsDir)) {
				Files.createDirectory(programsDir);
				return programNames;
			}
			for (Path file : directoryStream) {
				String fileName = file.getFileName().toString();
				// remove suffix .java
				programNames.add(fileName.substring(0, fileName.length() - 5));
			}

			return programNames;
		} catch (IOException e) {
			return programNames;
		}
	}
	
	/**
	 * generates an available new name
	 * @return "DefaultNicolaus" or "DefaultNicolaus1" or "DefaultNicolaus2" and so on...
	 */
	public String generateNewName() {
		String base = "DefaultNicolaus";
		String name = base;
		int i = 0;
		while (programs.contains(name)) {
			i++;
			name = base + i;
		}
		return name;
	}

	private static String getPrefix(Program program) {
		return "public class " + program.getName() + " extends nicolausSimulator.model.Nicolaus {"
				+ System.getProperty("line.separator") + "public ";
	}

	private static String getPostfix() {
		return System.getProperty("line.separator") + "}";
	}

}
