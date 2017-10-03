package nicolausSimulator.controller;

import java.io.File;

import javafx.stage.Stage;
import nicolausSimulator.controller.simulation.SimulationManager;
import nicolausSimulator.model.Territory;
import nicolausSimulator.view.GUI;

public class Program {
	private String programName;
	private boolean savedFlag = false;
	private File fileIfAlreadySaved;

	private NicolausSimulator simulator;
	private GUI gui;
	private Stage stage;
	private Territory territory;
	private Compiling compiler;
	private SimulationManager simulationManager;

	public Program(String name, NicolausSimulator simulator) {
		this.programName = name;
		this.simulator = simulator;
	}

	/**
	 * create Stage, Territory, Compiler and GUI
	 */
	public void start() {
		this.stage = new Stage();
		this.territory = new Territory();
		this.compiler = new Compiling();
		this.simulationManager = new SimulationManager(territory);
		this.gui = new GUI(stage, territory, simulator, this, compiler, simulationManager);
		gui.createGUI();
	}

	/**
	 * delegate compile command to compiler
	 */
	public void beCompiled(boolean allowFailAlert) {
		compiler.compile(territory, programName, allowFailAlert);
	}
	
	/**
	 * close the stage belonging to this program
	 */
	public void close() {
		stage.close();
	}
	
	public boolean isSimulationThreadAlive() {
		if(simulationManager.getSimulationThread() != null && simulationManager.getSimulationThread().isAlive()) {
			return true;
		} else {
			return false;
		}
	}

	public String getName() {
		return programName;
	}

	public void setName(String name) {
		this.programName = name;
		this.gui.refreshTitle();
	}

	public void setCode(String userInputCode) {
		gui.setEditorsText(userInputCode);
	}

	public boolean hasSavedFlag() {
		return savedFlag;
	}

	public File getFile() {
		return fileIfAlreadySaved;
	}

	/**
	 * set File. Set also savedFlag = true to prevent setting the savedFlag without
	 * setting the file
	 * 
	 * @param file
	 */
	public void setFileAndSavedFlag(File file) {
		this.fileIfAlreadySaved = file;
		this.savedFlag = true;
	}
}
