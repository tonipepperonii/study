package nicolausSimulator.controller;

import java.io.File;
import java.util.Observable;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import nicolausSimulator.model.Territory;
import nicolausSimulator.view.GUI;
import nicolausSimulator.view.TerritoryPanel;

public class TerritoryController extends Observable {
	private Territory territory;
	private TerritoryPanel tp;
	private GuiController popUpShower;

	private Image nickCursor;
	private Image nopeCursor;

	public TerritoryController(Territory territory, TerritoryPanel tp, GUI gui) {
		this.territory = territory;
		this.tp = tp;
		nickCursor = new Image(getClass().getResource("/resources/Nick/2Nick32.png").toString());
		nopeCursor = new Image(getClass().getResource("/resources/Delete24.gif").toString());
	}

	public void playButtonClicked() {
		territory.getNick().main();
	}

	public void mapGeneratorClicked() {
		int rows = territory.getField().length;
		int cols = territory.getField()[0].length;
		territory.generateNewField(rows, cols);
	}

	public void linksUmButtonClicked() {
		territory.linksUm();
	}

	public void vorButtonClicked() {
		territory.vor();
	}

	public void nimmButtonClicked() {
		territory.nimm();
	}

	public void baueButtonClicked() {
		territory.baue();
	}

	/**
	 * try to run Territory.generateNewField(int rows, int cols)
	 * 
	 * @param rowString
	 * @param colString
	 */
	public void changeTerritorySize(String rowString, String colString) {
		int rowInt;
		int colInt;

		try {
			rowInt = Integer.parseInt(rowString);
			colInt = Integer.parseInt(colString);
			if (rowInt < 1 || colInt < 1) {
				popUpShower.showWrongInputDialog();
			} else {
				territory.generateNewField(rowInt, colInt);
			}
		} catch (NumberFormatException e) {
			popUpShower.showWrongInputDialog();
		}

	}

	/**
	 * invoke on any key pressed. if key is ENTER, change territory size
	 * 
	 * @param e
	 * @param rows
	 * @param cols
	 */
	public void saveDialogOnEnter(KeyEvent e, String rows, String cols) {
		if (KeyCode.ENTER.equals(e.getCode())) {
			changeTerritorySize(rows, cols);
		}
	}

	/**
	 * when mouse is pressed while over nick, start dragging process
	 * 
	 * @param mouseEvent
	 */
	public void handleMousePressed(MouseEvent mouseEvent) {
		int x = (int) mouseEvent.getX() / TerritoryPanel.TILESIZE;
		int y = (int) mouseEvent.getY() / TerritoryPanel.TILESIZE;
		try {
			if (x == territory.getCol() && y == territory.getRow()) {
				tp.setDragFlag(true);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}

	}

	/**
	 * while dragging, always set nick on tile above which the mouse is (except
	 * there is a wall)
	 * 
	 * @param mouseEvent
	 */
	public void handleMouseDragged(MouseEvent mouseEvent) {

		int x = (int) mouseEvent.getX() / TerritoryPanel.TILESIZE;
		int y = (int) mouseEvent.getY() / TerritoryPanel.TILESIZE;

		if (tp.isDragFlag()) {
			try {
				if (territory.getField()[y][x] != -1) {
					ImageCursor dragCursor = new ImageCursor(nickCursor, nickCursor.getWidth() / 2,
							nickCursor.getHeight() / 2);
					((Node) mouseEvent.getSource()).setCursor(dragCursor);
				} else {
					ImageCursor dragCursor = new ImageCursor(nopeCursor, nickCursor.getWidth() / 2,
							nickCursor.getHeight() / 2);
					((Node) mouseEvent.getSource()).setCursor(dragCursor);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return;
			}
		}

	}

	/**
	 * on mouse release, end dragging process
	 * 
	 * @param mouseEvent
	 */
	public void handleMouseReleased(MouseEvent mouseEvent) {
		int x = (int) mouseEvent.getX() / TerritoryPanel.TILESIZE;
		int y = (int) mouseEvent.getY() / TerritoryPanel.TILESIZE;

		if (tp.isDragFlag()) {
			try {
				if (territory.getField()[y][x] != -1) {
					territory.placeNick(y, x);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return;
			}

			tp.setDragFlag(false);
			((Node) mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
		} else {
			tp.placeObject(x, y);
		}
	}

	// TODO
	public void giveNickMoreWood(int wood) {
		territory.giveNickMoreWood(wood);

	}
	
	public void serializeTerritory(Territory territory, File file) {
		territory.serialize(file);
	}
	
	public void deserializeTerritory(Territory territory, File file) {
		territory.deserialize(file);
	}
}
