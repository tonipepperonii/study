public class DefaultNicolaus1 extends nicolausSimulator.model.Nicolaus {
public void main() {
	int i = 0;
	while(i < 1000) {
	doSth();
	i++;
	}
}

private void doSth() {
	while(istVornFrei()) { 
		vor();
	}
	linksUm();

}
}