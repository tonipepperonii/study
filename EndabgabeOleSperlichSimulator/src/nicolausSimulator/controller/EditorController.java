package nicolausSimulator.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import nicolausSimulator.model.Nicolaus;
import nicolausSimulator.model.Territory;

public class EditorController {
	Program program;
	NicolausSimulator simulator;
	GuiController guiController;
	Territory territory;

	public EditorController(Program program, NicolausSimulator simulator, GuiController guiController,
			Territory territory) {
		this.program = program;
		this.simulator = simulator;
		this.guiController = guiController;
		this.territory = territory;
		// initial compiling
		boolean allowFailAlert = false;
		compile(allowFailAlert);

	}

	public void handleSaveButtonPressed(TextArea editor) {
		if (program.hasSavedFlag()) {
			simulator.saveProgram(program, program.getFile(), editor.getText());
		} else {
			File file = guiController.showSaveDialog();
			simulator.saveProgram(program, file, editor.getText());
		}
	}

	public void handleCompileButtonPressed(TextArea editor) {
		if (program.hasSavedFlag()) {
			simulator.saveProgram(program, program.getFile(), editor.getText());
		} else {
			File file = guiController.showSaveDialog();
			simulator.saveProgram(program, file, editor.getText());
		}
		boolean allowFailAlert = true;
		compile(allowFailAlert);
	}

	/**
	 * Compile user input code and load class
	 * 
	 * @param territory
	 * @param programName
	 */
	public void compile(boolean allowFailAlert) {
		final String fileName = "programs" + File.separator + program.getName() + ".java";

		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

		ByteArrayOutputStream answer = new ByteArrayOutputStream();
		boolean success = javac.run(null, null, answer, fileName) == 0;

		if (!success && allowFailAlert) {
			Alert alert = new Alert(Alert.AlertType.ERROR, answer.toString(), ButtonType.OK);
			alert.setTitle("U Sux!");
			alert.show();
		} else if (success && allowFailAlert) {
			Alert alert = new Alert(Alert.AlertType.NONE, "Die Rückmeldung bezüglich des Kompilieren ist 1 positive :)", ButtonType.OK);
			alert.setTitle("Alles Knorke!");
			alert.show();
		}
		try {
			loadClass();
		} catch (ClassNotFoundException e) {
			//compiling on start of Application will always fail, can be ignored
			if(program.hasSavedFlag()) {
				e.printStackTrace();
			}
		}
	}

	private void loadClass() throws ClassNotFoundException {
		try {
			File path = new File("programs" + File.separator);
			URL[] urls = new URL[] { path.toURI().toURL() };

			@SuppressWarnings("resource")
			ClassLoader cl = new URLClassLoader(urls);
			Class<?> cls = cl.loadClass(program.getName());

			cls = cl.loadClass(program.getName());
			Nicolaus Nick = (Nicolaus) cls.newInstance();

			territory.setNick(Nick);
		} catch(MalformedURLException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
