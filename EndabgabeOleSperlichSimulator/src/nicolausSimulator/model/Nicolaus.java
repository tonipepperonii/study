package nicolausSimulator.model;

/**
 * class to be replaced by child class, which the user immplements. contains
 * methods to delegate Invocations to territory
 * 
 * @author Ole Sperlich
 *
 */
public class Nicolaus {

	Territory territory;

	/**
	 * empty constructor to be called by ClassLoader
	 */
	public Nicolaus() {

	}

	@Invisible
	public void main() {

	}

	public void vor() {
		territory.vor();
	}

	public void linksUm() {
		territory.linksUm();
	}

	public void nimm() {
		territory.nimm();
	}

	public void baue() {
		territory.baue();
	}

	public boolean istHolzInHandAmSein() {
		return territory.istHolzInNicksHandAmSein();
	}

	public boolean istVornFrei() {
		return territory.istVornFrei();
	}

	public boolean istHierHolzAmLiegen() {
		return territory.istHierHolzAmLiegen();
	}

	@Invisible
	public void setTerritory(Territory territory) {
		this.territory = territory;
	}
}
