package nicolausSimulator.view;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import nicolausSimulator.controller.simulation.SimulationManager;
import nicolausSimulator.controller.simulation.SimulationState;

public class SimulationButton extends Button implements Observer {
	public final static String STOP = "stop";
	public final static String PLAY = "play";
	public final static String PAUSE = "pause";

	String buttonType;

	public SimulationButton(String string, ImageView img, String buttonType) {
		super(string, img);
		this.buttonType = buttonType;
		if (buttonType != PLAY) {
			setDisabled(true);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		SimulationState simState = ((SimulationManager) o).getSimulationState();

		switch (buttonType) {
		case PLAY: {
			if (simState == SimulationState.RUNNING) {
				setDisabled(true);
			} else {
				setDisabled(false);
			}
			break;
		}
		case PAUSE: {
			if (simState == SimulationState.RUNNING) {
				setDisabled(false);
			} else {
				setDisabled(true);
			}
			break;
		}
		case STOP: {
			if (simState == SimulationState.STOPPED) {
				super.setDisabled(true);
			} else {
				super.setDisabled(false);
			}
			break;
		}
		}
		System.out.println("SimulationButton.update - Button pressed");

	}

}
