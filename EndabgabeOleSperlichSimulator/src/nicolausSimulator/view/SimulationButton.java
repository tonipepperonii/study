package nicolausSimulator.view;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import nicolausSimulator.controller.simulation.SimulationManager;
import nicolausSimulator.controller.simulation.SimulationState;
import nicolausSimulator.utils.CustomObservable;
import nicolausSimulator.utils.CustomObserver;

public class SimulationButton extends Button implements CustomObserver {
	public final static String STOP = "stop";
	public final static String PLAY = "play";
	public final static String PAUSE = "pause";

	String buttonType;

	public SimulationButton(String string, ImageView img, String buttonType) {
		super(string, img);
		this.buttonType = buttonType;
		if (this.buttonType != PLAY) {
			this.setDisable(true);
		}
	}

	@Override
	public void update(CustomObservable o, Object arg) {
		SimulationState simState = ((SimulationManager) o).getSimulationState();

		switch (buttonType) {
		case PLAY: {
			if (simState == SimulationState.RUNNING) {
				setDisable(true);
			} else {
				setDisable(false);
			}
			break;
		}
		case PAUSE: {
			if (simState == SimulationState.RUNNING) {
				setDisable(false);
			} else {
				setDisable(true);
			}
			break;
		}
		case STOP: {
			if (simState == SimulationState.STOPPED) {
				super.setDisable(true);
			} else {
				super.setDisable(false);
			}
			break;
		}
		}
		System.out.println("SimulationButton.update - Button pressed, SimState: " + simState);

	}

}
