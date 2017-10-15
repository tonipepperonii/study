package nicolausSimulator.controller.simulation;

import nicolausSimulator.model.Territory;
import nicolausSimulator.model.exceptions.SimulatorException;
import nicolausSimulator.utils.CustomObservable;
import nicolausSimulator.utils.CustomObserver;
import nicolausSimulator.view.DeathSound;

public class SimulationThread extends Thread implements CustomObserver {
	volatile private int speed;
	private SimulationManager manager;

	private Territory territory;
	private Object lockObject;
	
	private DeathSound deathSound;

	boolean paused;

	public SimulationThread(Territory territory, SimulationManager manager) {
		this.territory = territory;
		this.manager = manager;
		this.deathSound = new DeathSound();
		System.out.println("SimulationThread - started");
	}

	@Override
	public void run() {
		try {
			territory.addObserver(this);
			territory.getNick().main();
			System.out.println("SimulationThread.run - ok");
		} catch (SimulatorException ex) {
			deathSound.play();
		} catch (RuntimeException e) {
			System.out.println("Simulation.run - RuntimeException catched.");
			return;
		} finally {
			manager.setSimulationState(SimulationState.STOPPED);
			territory.deleteObserver(this);
		}
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
	public void update(CustomObservable o, Object arg) {
		System.out.println("SimulationThread.update - ok");
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
