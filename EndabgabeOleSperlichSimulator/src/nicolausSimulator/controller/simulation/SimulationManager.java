package nicolausSimulator.controller.simulation;

import java.util.Observable;

import nicolausSimulator.model.Territory;

public class SimulationManager extends Observable {
	Simulation simulation;
	Territory territory;
	private SimulationState simState;
	private int speed = 0;

	// TODO das ist nur ein controller, soll nur methoden in Simulation aufrufen

	public SimulationManager(Territory territory) {
		this.territory = territory;
		this.simState = SimulationState.STOPPED;
	}

	public void handlePlayButtonPressed() {
		if (simState == SimulationState.STOPPED) {
			simState = SimulationState.RUNNING;

			simulation = new Simulation(territory, this);
			simulation.start();
		} else {
			simulation.resumeSimulation();
		}
		setChanged();
		notifyObservers();
	}

	public void handlePauseButtonPressed() {
		simState = SimulationState.PAUSED;
		setChanged();
		notifyObservers();
		simulation.pauseSimulation();
	}

	public void handleStopButtonPressed() {
		simState = SimulationState.STOPPED;
		simulation.interrupt();
		setChanged();
		notifyObservers();
	}

	public SimulationState getSimulationState() {
		return this.simState;
	}

	public void setSimulationSpeed(int speed) {
		if (simulation != null) {
			simulation.setSpeed(speed);
		}
	}

	public int getSpeed() {
		return this.speed;
	}
	
	public void setSimulationState(SimulationState state) {
		this.simState = state;
	}
	
	public Simulation getSimulationThread() {
		return this.simulation;
	}
}
