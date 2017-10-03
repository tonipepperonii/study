package nicolausSimulator;

import java.util.Scanner;

import nicolausSimulator.model.Nicolaus;
import nicolausSimulator.model.Territory;

/**
 * class to test model
 * @author Ole Sperlich
 *
 */
public class TestAnwendung {

	public static void main(String[] args) {
		Territory territory = new Territory();
		Nicolaus nick = new Nicolaus();
		nick.setTerritory(territory);
		
		int[][] fieldToPutSomeWallsAndWoodInIt = territory.getField();
		fieldToPutSomeWallsAndWoodInIt[3][5] = -1;
		fieldToPutSomeWallsAndWoodInIt[4][4] = 3;
		fieldToPutSomeWallsAndWoodInIt[0][0] = 2;
		
		territory.changeTerritorySize(6, 6);
		
		Scanner s = new Scanner(System.in);
		
		while(true) {
			printField(territory);
			System.out.println("Choose: q -> quit, m -> move, t -> turn left, p -> pick up wood, b -> build house");
			String answer = s.next();
			switch (answer) {
			case "q": System.exit(0);
			case "m": nick.vor(); break;
			case "t": nick.linksUm(); break;
			case "p": nick.nimm(); break;
			case "b": nick.baue(); break;
			
			}
		}

	}
	
	static void printField(Territory territory) {
		int[][] field = territory.getField();
		for(int x = 0; x < field.length; x++) {
			for(int y = 0; y < field[0].length; y++) {
				if(field[x][y] == -1) {
					System.out.print("#");
				} else if(x == territory.getRow() && y == territory.getCol()) {
					switch (territory.getDirection()) {
					case 0: System.out.print("^"); break;
					case 1: System.out.print(">"); break;
					case 2: System.out.print("v"); break;
					case 3: System.out.print("<"); break;
					}
				} else {
					System.out.print(field[x][y]);
				}
				System.out.print("  ");
			}
			System.out.println();
		}
	}

}
