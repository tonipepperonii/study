package nicolausSimulator.view;

import java.io.File;

import javafx.beans.binding.When;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import nicolausSimulator.controller.EditorController;
import nicolausSimulator.controller.GuiController;
import nicolausSimulator.controller.NicolausSimulator;
import nicolausSimulator.controller.PlacingState;
import nicolausSimulator.controller.Program;
import nicolausSimulator.controller.TerritoryController;
import nicolausSimulator.controller.simulation.SimulationManager;
import nicolausSimulator.model.Territory;

public class GUI {
	private final static String TITLE_PREFIX = "Haus vom Nikolaus - ";
	private NicolausSimulator simulator;
	private Stage stage;
	private Program program;

	private TerritoryPanel tp;
	private MenuBar menuBar;
	private ToolBar toolBar;
	private ToggleGroup toggleMenuItems;
	private ToggleGroup toggleButtons;
	private TextArea editor;

	private EditorController editorController;
	private GuiController guiController;
	private TerritoryController territoryController;
	private SimulationManager simulationManager;

	public GUI(Stage stage, Territory territory, NicolausSimulator simulator, Program program,
			SimulationManager simManager) {
		this.program = program;
		this.stage = stage;
		this.simulator = simulator;
		this.tp = new TerritoryPanel(territory);
		this.editorController = new EditorController(program, simulator, guiController);
		this.guiController = new GuiController(simulator, program, territory, territoryController, this, tp);
		this.territoryController = new TerritoryController(territory, tp, this);
		this.simulationManager = simManager;
	}

	public void createGUI() {
		tp.getCanvas().setOnMousePressed(mouseEvent -> territoryController.handleMousePressed(mouseEvent));
		tp.getCanvas().setOnMouseDragged(mouseEvent -> territoryController.handleMouseDragged(mouseEvent));
		tp.getCanvas().setOnMouseClicked(mouseEvent -> territoryController.handleMouseReleased(mouseEvent));
		tp.getCanvas().setOnContextMenuRequested(
				contextMenuEvent -> guiController.showContextMenu(contextMenuEvent));
		tp.setId("territoryPanel");

		BorderPane root = new BorderPane();

		Scene scene = new Scene(root, 1600, 500);
		scene.getStylesheets().addAll(this.getClass().getResource("/style/Style.css").toExternalForm());

		this.menuBar = createMenuBar();
		root.setTop(menuBar);

		BorderPane contentPane = new BorderPane();
		this.toolBar = createToolbar();
		contentPane.setTop(toolBar);

		SplitPane workspace = new SplitPane();

		editor = new TextArea();
		editor.setText("void main() {" + System.getProperty("line.separator") + "    "
				+ System.getProperty("line.separator") + "}");
		StackPane textEditor = new StackPane(editor);
		ScrollPane scrollPane = new ScrollPane();
		Canvas canvas = tp.getCanvas();
		canvas.layoutXProperty()
				.bind(new When(scrollPane.widthProperty().subtract(canvas.widthProperty()).divide(2).greaterThan(0))
						.then(scrollPane.widthProperty().subtract(canvas.widthProperty()).divide(2)).otherwise(0));
		canvas.layoutYProperty()
				.bind(new When(scrollPane.heightProperty().subtract(canvas.heightProperty()).divide(2).greaterThan(0))
						.then(scrollPane.heightProperty().subtract(canvas.heightProperty()).divide(2)).otherwise(0));
		scrollPane.setId("scrollPane");

		tp.drawTerritory();
		
		scrollPane.setContent(tp);
		workspace.getItems().addAll(textEditor, scrollPane);

		contentPane.setCenter(workspace);
		Label message = new Label("Herzlich willkommen");
		contentPane.setBottom(message);
		BorderPane.setAlignment(toolBar, Pos.CENTER_LEFT);
		BorderPane.setAlignment(workspace, Pos.CENTER);
		BorderPane.setAlignment(message, Pos.CENTER_LEFT);
		root.setCenter(contentPane);
		BorderPane.setAlignment(root, Pos.CENTER);

		this.stage.setTitle(TITLE_PREFIX + program.getName());
		this.stage.getIcons().add(new Image(getClass().getResource("/resources/Nick/2Nick32.png").toString()));
		this.stage.setOnCloseRequest(e -> closeProgram());

		this.stage.setScene(scene);
		// this.stage.setMaximized(true);
		this.stage.show();
	}

	private ToolBar createToolbar() {
		Button newFileButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/icons/file.png").toString())));
		newFileButton.setTooltip(new Tooltip("Neue Datei"));
		newFileButton.setOnAction(e -> guiController.showNewFileDialog());
		
		Button openFileButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/icons/folder.png").toString())));
		openFileButton.setTooltip(new Tooltip("Datei öffnen"));
		openFileButton.setOnAction(e -> guiController.showOpenDialog());
		
		Button saveButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/icons/save.png").toString())));
		saveButton.setTooltip(new Tooltip("Datei speichern"));
		saveButton.setOnAction(e -> editorController.handleSaveButtonPressed(editor));
		
		Button compileButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/icons/compile.png").toString())));
		compileButton.setTooltip(new Tooltip("Code kompilieren"));
		compileButton.setOnAction(e -> editorController.handleCompileButtonPressed(editor));

		
		toggleButtons = new ToggleGroup();
		Button terrainButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/Terrain24.gif").toString())));
		terrainButton.setTooltip(new Tooltip("Welche Größe soll mein Terrain haben?"));
		terrainButton.setOnAction(e -> guiController.showChangeSizeDialog());
		
		ToggleButton nickButton = new ToggleButton("",
				new ImageView(new Image(getClass().getResource("/resources/Nick/Nick24.png").toString())));
		nickButton.setId(PlacingState.NICK);
		nickButton.setTooltip(new Tooltip("Hi, ich bin Nick"));
		nickButton.setToggleGroup(toggleButtons);
		nickButton.setOnAction(e -> guiController.selectToggleButton());
		
		ToggleButton woodButton = new ToggleButton("",
				new ImageView(new Image(getClass().getResource("/resources/Nick/wood24.png").toString())));
		woodButton.setId(PlacingState.WOOD);
		woodButton.setToggleGroup(toggleButtons);
		woodButton.setOnAction(e -> guiController.selectToggleButton());
		woodButton.setTooltip(new Tooltip("Lege Holz zum Sammeln für mich aus"));
		
		ToggleButton wallButton = new ToggleButton("",
				new ImageView(new Image(getClass().getResource("/resources/Wall24.gif").toString())));
		wallButton.setId(PlacingState.WALL);
		wallButton.setToggleGroup(toggleButtons);
		wallButton.setOnAction(e -> guiController.selectToggleButton());
		wallButton.setTooltip(new Tooltip("Stelle mir Wände in den Weg, wenn du was gegen mich hast"));
		
		ToggleButton deleteButton = new ToggleButton("",
				new ImageView(new Image(getClass().getResource("/resources/Delete24.gif").toString())));
		deleteButton.setId(PlacingState.DELETE);
		deleteButton.setToggleGroup(toggleButtons);
		deleteButton.setOnAction(e -> guiController.selectToggleButton());
		deleteButton.setTooltip(new Tooltip("Lösche Gegenstände vom Terrain"));
		
		Button nickWithWoodButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/Nick/NickWithWood24.png").toString())));
		nickWithWoodButton.setTooltip(new Tooltip("Gib mir mehr Holz in die Hand"));
		nickWithWoodButton.setOnAction(e -> guiController.showGiveMoreWoodDialog());
		
		Button nickLeftButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/Nick/turnLeft24.png").toString())));
		nickLeftButton.setTooltip(new Tooltip("Drehe mich nach links"));
		nickLeftButton.setOnAction(e -> territoryController.linksUmButtonClicked());
		
		Button nickMoveButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/Nick/move24.png").toString())));
		nickMoveButton.setTooltip(new Tooltip("Lass mich einen Schritt gehen"));
		nickMoveButton.setOnAction(e -> territoryController.vorButtonClicked());
		
		Button nickPickButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/Nick/pick24.png").toString())));
		nickPickButton.setTooltip(new Tooltip("Lass mich ein Stück Holz aufheben"));
		nickPickButton.setOnAction(e -> territoryController.nimmButtonClicked());
		
		Button nickPutButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/Nick/put24.png").toString())));
		nickPutButton.setTooltip(new Tooltip("Lass mich mein Haus (weiter)bauen"));
		nickPutButton.setOnAction(e -> territoryController.baueButtonClicked());
		
		Button generateNewFieldButton = new Button("",
				new ImageView(new Image(getClass().getResource("/resources/NewTerritory24.png").toString())));
		generateNewFieldButton.setTooltip(new Tooltip("Generiere ein neues Territorium"));
		generateNewFieldButton.setOnAction(e -> territoryController.mapGeneratorClicked());

		SimulationButton playButton = new SimulationButton("",
				new ImageView(new Image(getClass().getResource("/resources/Play24.gif").toString())), 
				SimulationButton.PLAY);
		simulationManager.addObserver(playButton);
		playButton.setTooltip(new Tooltip("Play"));
		playButton.setOnAction(e -> simulationManager.handlePlayButtonPressed());

		SimulationButton pauseButton = new SimulationButton("",
				new ImageView(new Image(getClass().getResource("/resources/Pause24.gif").toString())),
				SimulationButton.PAUSE);
		simulationManager.addObserver(pauseButton);
		pauseButton.setTooltip(new Tooltip("Pause"));
		pauseButton.setOnAction(e -> simulationManager.handlePauseButtonPressed());

		SimulationButton stopButton = new SimulationButton("",
				new ImageView(new Image(getClass().getResource("/resources/Stop24.gif").toString())), 
				SimulationButton.STOP);
		simulationManager.addObserver(stopButton);
		stopButton.setTooltip(new Tooltip("Stop"));
		stopButton.setOnAction(e -> simulationManager.handleStopButtonPressed());

		Slider simulationSlider = new Slider();
		simulationSlider.setMin(1);
		simulationSlider.setMax(100);
		simulationSlider.valueProperty()
				.addListener((observable, oldValue, newValue) -> simulationManager.setSimulationSpeed(newValue.intValue()));

		ToolBar toolBar = new ToolBar(newFileButton, openFileButton, saveButton, compileButton, new Separator(),
				terrainButton, generateNewFieldButton, new Separator(), nickButton, woodButton, wallButton,
				deleteButton, new Separator(), nickWithWoodButton, nickLeftButton, nickMoveButton, nickPickButton,
				nickPutButton, new Separator(), playButton, pauseButton, stopButton, new Separator(), simulationSlider);
		return toolBar;
	}

	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = createFileMenu();
		Menu territoryMenu = createTerritoryMenu();
		Menu nicolausMenu = createNicolausMenu();
		Menu simulationMenu = createSimulationMenu();

		menuBar.getMenus().addAll(fileMenu, territoryMenu, nicolausMenu, simulationMenu);

		return menuBar;
	}

	private Menu createFileMenu() {
		Menu fileMenu = new Menu("_Editor");
		MenuItem newMenuItem = new MenuItem("_Neu");
		newMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + N"));
		newMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("/resources/New16.gif").toString())));
		MenuItem openMenuItem = new MenuItem("_Öffnen");
		openMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + O"));
		openMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("/resources/Open16.gif").toString())));
		MenuItem compileMenuItem = new MenuItem("_Kompilieren");
		compileMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + K"));
		MenuItem printMenuItem = new MenuItem("_Drucken");
		printMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + P"));
		printMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("/resources/Print16.gif").toString())));
		MenuItem quitMenuItem = new MenuItem("_Beenden");
		quitMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + Q"));
		quitMenuItem.setOnAction(e -> closeProgram());
		fileMenu.getItems().addAll(newMenuItem, openMenuItem, new SeparatorMenuItem(), compileMenuItem, printMenuItem,
				new SeparatorMenuItem(), quitMenuItem);
		return fileMenu;
	}

	private Menu createTerritoryMenu() {
		Menu territoryMenu = new Menu("_Territorium");

		Menu safeSubmenur = new Menu("_Speichern");
		MenuItem xmlMenuItem = new MenuItem("_XML");
		MenuItem jaxbMenuItem = new MenuItem("_JAXB");
		MenuItem serializeMenuItem = new MenuItem("_Serialisieren");
		safeSubmenur.getItems().addAll(xmlMenuItem, jaxbMenuItem, serializeMenuItem);
		serializeMenuItem.setOnAction(e -> guiController.showSerializeDialog());

		Menu loadSubmenu = new Menu("_Laden");
		MenuItem deserializeMenuItem = new MenuItem("_Deserialisieren");
		deserializeMenuItem.setOnAction(e -> guiController.showDeserializeDialog());
		loadSubmenu.getItems().add(deserializeMenuItem);
		
		Menu safeAsPictureSubmenu = new Menu("Als _Bild speichern");
		MenuItem printSubMenuItem = new MenuItem("_Drucken");
		MenuItem changeSizeMenuItem = new MenuItem("_Größe ändern");
		toggleMenuItems = new ToggleGroup();
		RadioMenuItem placeProtagonistMenuItem = new RadioMenuItem("_Nick platzieren");
		placeProtagonistMenuItem.setId(PlacingState.NICK);
		placeProtagonistMenuItem.setToggleGroup(toggleMenuItems);
		placeProtagonistMenuItem.setOnAction(e -> guiController.selectToggleMenuItem());
		RadioMenuItem placeWoodMenuItem = new RadioMenuItem("_Holz platzieren");
		placeWoodMenuItem.setId(PlacingState.WOOD);
		placeWoodMenuItem.setToggleGroup(toggleMenuItems);
		placeWoodMenuItem.setOnAction(e -> guiController.selectToggleMenuItem());
		RadioMenuItem placeWallMenuItem = new RadioMenuItem("_Mauer platzieren");
		placeWallMenuItem.setId(PlacingState.WALL);
		placeWallMenuItem.setToggleGroup(toggleMenuItems);
		placeWallMenuItem.setOnAction(e -> guiController.selectToggleMenuItem());
		RadioMenuItem deleteCellMenuItem = new RadioMenuItem("_Kachel löschen");
		deleteCellMenuItem.setId(PlacingState.DELETE);
		deleteCellMenuItem.setToggleGroup(toggleMenuItems);
		deleteCellMenuItem.setOnAction(e -> guiController.selectToggleMenuItem());
		territoryMenu.getItems().addAll(safeSubmenur, loadSubmenu, safeAsPictureSubmenu, printSubMenuItem,
				changeSizeMenuItem, new SeparatorMenuItem(), placeProtagonistMenuItem, placeWoodMenuItem,
				placeWallMenuItem, deleteCellMenuItem);
		return territoryMenu;
	}

	private static Menu createNicolausMenu() {
		Menu nicolausMenu = new Menu("_Nikolaus");
		MenuItem woodInHandMenuItem = new MenuItem("_Hölzer im Besitz...");
		MenuItem turnLeftMenuItem = new MenuItem("links _drehen");
		turnLeftMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + SHIFT + L"));
		MenuItem goMenuItem = new MenuItem("_vor");
		goMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + SHIFT + V"));
		MenuItem takeMenuItem = new MenuItem("_nimm");
		takeMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + SHIFT + N"));
		MenuItem giveMenuItem = new MenuItem("_gib");
		giveMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + SHIFT + G"));
		nicolausMenu.getItems().setAll(woodInHandMenuItem, turnLeftMenuItem, goMenuItem, takeMenuItem, giveMenuItem);
		return nicolausMenu;
	}

	private Menu createSimulationMenu() {
		Menu simulationMenu = new Menu("_Simulation");
		MenuItem startMenuItem = new MenuItem("Start/_Fortsetzen");
		startMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + F11"));
		startMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("/resources/Play16.gif").toString())));
		MenuItem pauseMenuItem = new MenuItem("_Pause");
		pauseMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("/resources/Pause16.gif").toString())));
		MenuItem stopMenuItem = new MenuItem("_Stopp");
		stopMenuItem.setGraphic(new ImageView(new Image(getClass().getResource("/resources/Stop16.gif").toString())));
		stopMenuItem.setAccelerator(KeyCombination.keyCombination("SHORTCUT + F12"));
		simulationMenu.getItems().setAll(startMenuItem, pauseMenuItem, stopMenuItem);
		return simulationMenu;
	}

	private void closeProgram() {
		if (program.hasSavedFlag()) {
			simulator.saveProgram(program, program.getFile(), editor.getText());
		} else {
			File file = guiController.showSaveDialog();
			simulator.saveProgram(program, file, editor.getText());
		}
		simulator.closeProgram(program);
	}
	
	public void refreshTitle() {
		this.stage.setTitle(TITLE_PREFIX + program.getName());
	}

	public ToggleGroup getToggleMenuItems() {
		return this.toggleMenuItems;
	}

	public ToggleGroup getToggleButtons() {
		return this.toggleButtons;
	}

	public void setEditorsText(String text) {
		editor.setText(text);
	}
	
	public Stage getStage() {
		return this.stage;
	}
}
