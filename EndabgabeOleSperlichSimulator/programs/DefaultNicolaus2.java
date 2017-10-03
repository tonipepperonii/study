public class DefaultNicolaus2 extends nicolausSimulator.model.Nicolaus {
public void main() {
    go();
}

private void go() {
  while(istVornFrei()) {
    vor();
  }
}

}