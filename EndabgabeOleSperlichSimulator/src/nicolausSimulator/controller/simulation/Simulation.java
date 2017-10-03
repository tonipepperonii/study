package nicolausSimulator.controller.simulation;

import java.util.Observable;
import java.util.Observer;

import nicolausSimulator.model.Territory;

public class Simulation extends Thread implements Observer {
	volatile private int speed;
	private SimulationManager manager;

	private Territory territory;
	private Object lockObject;

	boolean paused;

	public Simulation(Territory territory, SimulationManager manager) {
		this.territory = territory;
		this.manager = manager;
	}

	@Override
	public void run() {
		try {
			territory.addObserver(this);
			territory.getNick().main();
		} catch (RuntimeException e) {
			System.out.println("Simulation.run - RuntimeException catched.");
			return;
		} finally {
			manager.setSimulationState(SimulationState.STOPPED);
			territory.deleteObserver(this);
		}
	}

	public void startSimulation() {

	}

	public void pauseSimulation() {
		paused = true;

	}

	public void resumeSimulation() {
		synchronized (lockObject) {
			lockObject.notify();
		}
	}

	public int getSpeed() {
		return this.speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (Thread.currentThread().getClass() == this.getClass()) {
			while (paused) {
				try {
					territory.deleteObserver(this);
					lockObject.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					territory.addObserver(this);
				}
			}
			try {
				System.out.println("Simulation.update - Speed: " + speed);
				sleep(500 - (this.speed * 5));
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
		}

	}

}
