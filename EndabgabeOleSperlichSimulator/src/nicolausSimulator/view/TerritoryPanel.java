package nicolausSimulator.view;

import java.util.ArrayList;

import com.sun.glass.ui.Application;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import nicolausSimulator.controller.PlacingState;
import nicolausSimulator.model.Territory;
import nicolausSimulator.utils.CustomObservable;
import nicolausSimulator.utils.CustomObserver;

/**
 * the graphic representation of the territory
 * 
 * @author Ole Sperlich
 *
 */
public class TerritoryPanel extends Region implements CustomObserver {
	public static final int TILESIZE = 32;
	private Territory territory;
	private Canvas canvas;

	private String placingState;
	private boolean dragFlag;

	private Image wall;
	private ArrayList<Image> nicks;
	private ArrayList<Image> houses;

	public TerritoryPanel(Territory territory) {
		this.territory = territory;
		territory.addObserver(this);
		this.canvas = new Canvas();
		this.placingState = null;
		initImages();
	}

	/**
	 * load images in memory to prevent performance issues through too many file
	 * accesses
	 */
	private void initImages() {
		wall = new Image(getClass().getResource("/resources/Wall32.png").toString());

		houses = new ArrayList<Image>();
		for (int i = 0; i <= 8; i++) {
			houses.add(new Image(getClass().getResource("/resources/Nick/house" + i + ".png").toString()));
		}

		nicks = new ArrayList<Image>();
		for (int i = 0; i <= 3; i++) {
			nicks.add(new Image(getClass().getResource("/resources/Nick/" + i + "Nick32.png").toString()));
		}

	}

	/**
	 * create territory graphic
	 */
	public void drawTerritory() {

		int[][] field = territory.getField();
		int height = field.length * TILESIZE;
		int width = field[0].length * TILESIZE;
		this.canvas.setWidth(width);
		this.canvas.setHeight(height);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.SNOW);
		for (int x = 0; x < field.length; x++) {
			for (int y = 0; y < field[0].length; y++) {
				int xCoordinate = x * TILESIZE;
				int yCoordinate = y * TILESIZE;
				gc.fillRect(yCoordinate, xCoordinate, TILESIZE, TILESIZE);
				gc.strokeRect(yCoordinate, xCoordinate, TILESIZE, TILESIZE);

				int item = field[x][y];
				if (item == -1) {
					gc.drawImage(wall, yCoordinate, xCoordinate);
				} else if (item > 8) {
					gc.drawImage(houses.get(8), yCoordinate, xCoordinate);
				} else {
					gc.drawImage(houses.get(item), yCoordinate, xCoordinate);
				}
				if (y == territory.getCol() && x == territory.getRow()) {
					gc.drawImage(nicks.get(territory.getDirection()), yCoordinate, xCoordinate);
				}
			}
		}

		this.getChildren().clear();
		this.getChildren().add(canvas);
	}

	@Override
	public void update(CustomObservable arg0, Object arg1) {
		if(Application.isEventThread()) {
			drawTerritory();
		} else {
			Platform.runLater(() -> drawTerritory());
		}
		
	}

	public void placeObject(int x, int y) {
		if (placingState == null) {
			return;
		}
		switch (placingState) {
		case PlacingState.NICK:
			territory.placeNick(y, x);
			break;
		case PlacingState.WALL:
			territory.placeWall(y, x);
			break;
		case PlacingState.WOOD:
			territory.placeWood(y, x);
			break;
		case PlacingState.DELETE:
			territory.deleteObject(y, x);
			break;
		default:
			break;
		}
	}

	public void setPlacingState(String state) {
		this.placingState = state;
	}

	public boolean isDragFlag() {
		return dragFlag;
	}

	public void setDragFlag(boolean dragFlag) {
		this.dragFlag = dragFlag;
	}

	public Canvas getCanvas() {
		return canvas;
	}

}
