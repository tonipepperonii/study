package nicolausSimulator.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
	
	public EditorController(Program program, NicolausSimulator simulator, GuiController guiController) {
		this.program = program;
		this.simulator = simulator;
		this.guiController = guiController;
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
		boolean allowFailAlert = true;
		if (program.hasSavedFlag()) {
			simulator.saveProgram(program, program.getFile(), editor.getText());
			program.beCompiled(allowFailAlert);
		} else {
			File file = guiController.showSaveDialog();
			simulator.saveProgram(program, file, editor.getText());
			program.beCompiled(allowFailAlert);
		}
	}

	/**Compile user input code and load class
	 * @param territory
	 * @param programName
	 */
	public void compile(Territory territory, String programName, boolean allowFailAlert) {
		final String fileName = "programs" + File.separator + programName + ".java";

		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

		ByteArrayOutputStream answer = new ByteArrayOutputStream();
		boolean success = javac.run(null, null, answer, fileName) == 0;

		if (!success && allowFailAlert) {
			Alert alert = new Alert(Alert.AlertType.ERROR, answer.toString(), ButtonType.OK);
			alert.setTitle("U Sux!");
			alert.show();
		}
		loadClass(territory, programName);
	}

	private void loadClass(Territory ter, String programName) {
		try {
			File path = new File("programs" + File.separator);
			URL[] urls = new URL[] { path.toURI().toURL() };

			@SuppressWarnings("resource")
			ClassLoader cl = new URLClassLoader(urls);
			Class<?> cls = cl.loadClass(programName);

			cls = cl.loadClass(programName);
			Nicolaus Nick = (Nicolaus) cls.newInstance();

			ter.setNick(Nick);
		} catch (Throwable exc) {
			exc.printStackTrace();
		}
	}
}
