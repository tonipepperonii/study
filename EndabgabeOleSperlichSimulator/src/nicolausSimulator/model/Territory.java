package nicolausSimulator.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import nicolausSimulator.model.exceptions.DuKommstHierNichtVorbeiException;
import nicolausSimulator.model.exceptions.HierLiegtKeinHolzMehrException;
import nicolausSimulator.model.exceptions.NickHatKeinHolzMehrException;
import nicolausSimulator.utils.CustomObservable;

public class Territory extends CustomObservable implements Serializable {
	private final static long serialVersionUID = 5236211796831879530L;
	
	private final static int NORTH = 0;
	private final static int EAST = 1;
	private final static int SOUTH = 2;
	private final static int WEST = 3;

	private int[][] field;

	private int nicksRow;
	private int nicksCol;
	private int direction;
	private int woodsInNicksHand;

	transient private Nicolaus Nick;

	/**
	 * constructor which initializes a 10x10 territory and creates a default
	 * Nicolaus
	 */
	public Territory() {
		generateNewField(10, 10);
		this.Nick = new Nicolaus();
	}

	/**
	 * generates a new field, based on random integers
	 */
	public void generateNewField(int rows, int cols) {
		synchronized (this) {

			if (rows < 3 || cols < 3) {
				nicksRow = 0;
				nicksCol = 0;
			} else {
				nicksRow = 3;
				nicksCol = 3;
			}
			direction = EAST;
			field = new int[rows][cols];

			for (int y = 0; y < field.length; y++) {
				for (int x = 0; x < field[0].length; x++) {
					int random1 = (int) (Math.random() * 5);
					if (random1 == 1 && (x != nicksCol || y != nicksRow)) {
						field[y][x] = -1;
					} else if (random1 == 2) {
						int random2 = (int) (Math.random() * 4);
						field[y][x] = random2;
					}
				}
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * move Nick one tile in current direction
	 */
	public void vor() {
		synchronized (this) {
			if (istVornFrei()) {
				switch (direction) {
				case NORTH:
					nicksRow--;
					break;
				case EAST:
					nicksCol++;
					break;
				case SOUTH:
					nicksRow++;
					break;
				case WEST:
					nicksCol--;
					break;
				}

			} else {
				throw new DuKommstHierNichtVorbeiException();
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * turn Nick 90° left
	 */
	public void linksUm() {
		synchronized (this) {
			direction = (direction - 1 + 4) % 4;
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * grab some wood if there is some
	 */
	public void nimm() {
		synchronized (this) {
			if (istHierHolzAmLiegen()) {
				field[nicksRow][nicksCol]--;
				woodsInNicksHand++;

			} else {
				throw new HierLiegtKeinHolzMehrException();
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * build home if Nick has wood in his hand
	 */
	public void baue() {
		synchronized (this) {
			if (istHolzInNicksHandAmSein()) {
				field[nicksRow][nicksCol]++;
				woodsInNicksHand--;
			} else {
				throw new NickHatKeinHolzMehrException();
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * @return true, if Nick has wood in hand
	 */
	synchronized boolean istHolzInNicksHandAmSein() {
		if (woodsInNicksHand > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return true, if tile in front of Nick is inside the territory and is not
	 *         blocked by a wall
	 */
	synchronized public boolean istVornFrei() {
		try {
			switch (direction) {
			case NORTH:
				if (field[nicksRow - 1][nicksCol] == -1) {
					return false;
				}
				break;
			case EAST:
				if (field[nicksRow][nicksCol + 1] == -1) {
					return false;
				}
				break;
			case SOUTH:
				if (field[nicksRow + 1][nicksCol] == -1) {
					return false;
				}
				break;
			case WEST:
				if (field[nicksRow][nicksCol - 1] == -1) {
					return false;
				}
				break;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	/**
	 * @return true, if wood/ a house is on the current tile
	 */
	synchronized public boolean istHierHolzAmLiegen() {
		if (field[nicksRow][nicksCol] > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * resize territory. If Nick would be out of bounds, he is placed at (0/0)
	 * removing a wall if there is one. Other items in territory stay.
	 * 
	 * @param rows
	 * @param cols
	 */
	public void changeTerritorySize(int rows, int cols) {
		synchronized (this) {
			int[][] nextField = new int[rows][cols];
			for (int x = 0; x < rows; x++) {
				for (int y = 0; y < cols; y++) {
					if (x <= this.field.length && y <= this.field[0].length) {
						nextField[x][y] = this.field[x][y];
					} else {
						nextField[x][y] = 0;
					}

				}
			}
			if (this.nicksRow > rows || this.nicksCol > cols) {
				this.nicksRow = 0;
				this.nicksCol = 0;
				if (nextField[0][0] == -1) {
					nextField[0][0] = 0;
				}
			}
			this.field = nextField;
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * place Nick in territory
	 * 
	 * @param row
	 * @param col
	 */
	public void placeNick(int row, int col) {
		synchronized (this) {
			if (this.field[row][col] != -1) {
				this.nicksRow = row;
				this.nicksCol = col;
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * add 1 wood to a tile (max. 8)
	 * 
	 * @param row
	 * @param col
	 */
	public void placeWood(int row, int col) {
		synchronized (this) {
			if (field[row][col] < 8) {
				field[row][col]++;

			} else {
				// TODO Fehlermeldung oder so
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * remove 1 wood if possible
	 * 
	 * @param row
	 * @param col
	 */
	public void removeWood(int row, int col) {
		synchronized (this) {
			if (field[row][col] > 0) {
				field[row][col]--;
			} else {
				// TODO Fehlermeldung oder so
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * place a wall. Overrides wood/house on a tile
	 * 
	 * @param row
	 * @param col
	 */
	public void placeWall(int row, int col) {
		synchronized (this) {
			if (this.nicksRow != row || this.nicksCol != col) {
				field[row][col] = -1;
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * increase woodInNicksHand by the value of woods
	 * 
	 * @param woods
	 */
	synchronized public void giveNickMoreWood(int woods) {
		woodsInNicksHand = woodsInNicksHand + woods;
	}

	/**
	 * clear a tile (remove wall or house)
	 * 
	 * @param row
	 * @param col
	 */
	public void deleteObject(int row, int col) {
		synchronized (this) {
			if (this.nicksRow != row || this.nicksCol != col) {
				this.field[row][col] = 0;
			}
		}
		setChanged();
		notifyObservers();
	}

	synchronized public boolean serialize(File file) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
			out.writeObject(this);
			
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;// TODO
	}

	synchronized public boolean deserialize(File file) {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			Territory ter = (Territory) in.readObject();
			this.direction = ter.direction;
			this.field = ter.field;
			this.nicksCol = ter.nicksCol;
			this.nicksRow = ter.nicksRow;
			this.woodsInNicksHand = ter.woodsInNicksHand;
			setChanged();
			notifyObservers();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false; // TODO
	}

	synchronized public Nicolaus getNick() {
		return Nick;
	}

	synchronized public void setNick(Nicolaus Nick) {
		this.Nick = Nick;
		this.Nick.setTerritory(this);
	}

	synchronized public int[][] getField() {
		return field;
	}

	synchronized public int getRow() {
		return nicksRow;
	}

	synchronized public int getCol() {
		return nicksCol;
	}

	synchronized public int getDirection() {
		return direction;
	}

	synchronized public int getWoodsInNicksHand() {
		return woodsInNicksHand;
	}

}
