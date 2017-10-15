package nicolausSimulator.view;

import java.net.URL;

import javafx.scene.media.AudioClip;

public class DeathSound {
	AudioClip clip;

	public DeathSound() {
		URL resource = getClass().getResource("/resources/death.wav");
		clip = new AudioClip(resource.toString());
	}

	public void play() {
		clip.play();
	}
}
