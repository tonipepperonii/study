package nicolausSimulator.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import nicolausSimulator.model.Territory;
import nicolausSimulator.view.GUI;
import nicolausSimulator.view.NicolausContextMenu;
import nicolausSimulator.view.TerritoryPanel;

public class GuiController {
	private NicolausSimulator simulator;
	private Program program;

	private Territory territory;
	private TerritoryController territoryController;
	private GUI gui;
	private TerritoryPanel tp;

	public GuiController(NicolausSimulator simulator, Program program, Territory territory,
			TerritoryController territoryController, GUI gui, TerritoryPanel tp) {
		this.simulator = simulator;
		this.program = program;
		this.territory = territory;
		this.territoryController = territoryController;
		this.gui = gui;
		this.tp = tp;
	}

	public void showChangeSizeDialog() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.setTitle("Spielfeldgröße ändern");
		dialog.setHeaderText("Hier kannst du die Spielfeldgröße ändern.");

		dialog.setGraphic(new ImageView(this.getClass().getResource("/resources/Nick/house8.png").toString()));

		ButtonType applyButtonType = new ButtonType("Anwenden", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField rows = new TextField();
		rows.setPromptText(String.valueOf(territory.getField().length));
		TextField cols = new TextField();
		cols.setPromptText(String.valueOf(territory.getField()[0].length));

		grid.add(new Label("Zeilen:"), 0, 0);
		grid.add(rows, 1, 0);
		grid.add(new Label("Spalten:"), 0, 1);
		grid.add(cols, 1, 1);

		dialog.getDialogPane().setContent(grid);

		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent()) {
			territoryController.changeTerritorySize(rows.getText(), cols.getText());
		}
	}
	
	public void showGiveMoreWoodDialog() {
		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("Mehr Holz");
		dialog.setHeaderText("Gib mir mehr Holz");
		dialog.setContentText("Wie viel Holz gibst du mir dazu?");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			try {
				int wood = Integer.parseInt(result.get());
				territoryController.giveNickMoreWood(wood);
			} catch (Exception e) {
				showWrongInputDialog();
			}
		}
	}

	public void showWrongInputDialog() {
		Alert alert = new Alert(AlertType.ERROR, "Ungültige Eingabe", ButtonType.OK, ButtonType.CANCEL);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.OK) {
		}
	}

	public void showOpenDialog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(NicolausSimulator.DIRECTORY));
		fileChooser.setTitle("Programm öffnen");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JAVA files (*.java)", "*.java");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog(gui.getStage());
		if(file != null) {
			simulator.openProgram(file);
		}
	}

	public File showSaveDialog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(NicolausSimulator.DIRECTORY));
		fileChooser.setTitle("Programm speichern");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JAVA files (*.java)", "*.java");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialFileName(program.getName() + ".java");

		File file = fileChooser.showSaveDialog(gui.getStage());
		return file;
	}

	public void showNewFileDialog() {
		TextInputDialog dialog = new TextInputDialog(simulator.generateNewName());
		dialog.setTitle("Neues Programm");
		dialog.setHeaderText("Lege ein neues Programm an. (Es muss ein gültiger Java-Bezeichner sein)");
		dialog.setContentText("Dein neues Programm heißt:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (!simulator.startNewProgram(result.get())) {
				showWrongInputDialog();
			}
		}
	}

	public void showSerializeDialog() {
		String directory = "serializedTerritories" + File.separator;
		Path territoriesDir = Paths.get(directory);
		if (!Files.exists(territoriesDir)) {
			try {
				Files.createDirectory(territoriesDir);
			} catch (IOException e) {
				return; // TODO Fehlermeldung oderso
			}
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(directory));
		fileChooser.setTitle("Territorium serialisieren");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("territory files (*.ter)", "*.ter");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialFileName(program.getName() + ".ter");

		File file = fileChooser.showSaveDialog(gui.getStage());
		if(file != null) {
			territoryController.serializeTerritory(territory, file);
		}
		
	}

	public void showDeserializeDialog() {
		String directory = "serializedTerritories" + File.separator;
		Path territoriesDir = Paths.get(directory);
		if (!Files.exists(territoriesDir)) {
			try {
				Files.createDirectory(territoriesDir);
			} catch (IOException e) {
				return; // TODO Fehlermeldung oderso
			}
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(directory));
		fileChooser.setTitle("Programm öffnen");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("territory files (*.ter)", "*.ter");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog(gui.getStage());
		territoryController.deserializeTerritory(territory, file);
	}

	public void showContextMenu(ContextMenuEvent event) {
		if (!program.isSimulationThreadAlive()) {
			int x = (int) event.getX() / TerritoryPanel.TILESIZE;
			int y = (int) event.getY() / TerritoryPanel.TILESIZE;

			if (x == territory.getCol() && y == territory.getRow()) {
				NicolausContextMenu menu = new NicolausContextMenu(territory, territory.getNick());
				menu.setX(event.getScreenX());
				menu.setY(event.getScreenY());

				menu.show(gui.getStage());
			}
		}
	}

	/**
	 * invoke when a ToggleButton is pressed. select relating RadioMenuItem in
	 * Menubar
	 */
	public void selectToggleButton() {
		ToggleButton selected = (ToggleButton) gui.getToggleButtons().getSelectedToggle();
		String id = selected.getId();
		tp.setPlacingState(id);
		ObservableList<Toggle> toggles = gui.getToggleMenuItems().getToggles();
		for (Toggle menuItem : toggles) {
			RadioMenuItem toggleMenuItem = (RadioMenuItem) menuItem;
			if (id.equals(toggleMenuItem.getId())) {
				toggleMenuItem.setSelected(true);
				break;
			}
		}
	}

	/**
	 * invoke when RadioMenuItem is pressed. select realting ToggleButton in Toolbar
	 */
	public void selectToggleMenuItem() {
		RadioMenuItem selected = (RadioMenuItem) gui.getToggleMenuItems().getSelectedToggle();
		String id = selected.getId();
		tp.setPlacingState(id);
		ObservableList<Toggle> toggles = gui.getToggleButtons().getToggles();
		for (Toggle button : toggles) {
			ToggleButton toggleButton = (ToggleButton) button;
			if (id.equals(toggleButton.getId())) {
				toggleButton.setSelected(true);
				break;
			}
		}
	}
	
	
}
