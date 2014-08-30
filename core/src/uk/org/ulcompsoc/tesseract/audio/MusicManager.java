package uk.org.ulcompsoc.tesseract.audio;

import com.badlogic.gdx.utils.Disposable;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public interface MusicManager extends Disposable {
	public void play(int index);

	public void pause();

	public void stop();

	/**
	 * Fades the currently playing song over duration seconds.
	 * 
	 * @param duration
	 *        The duration of the fade.
	 */
	public void fadeOut(float duration);

	public void update(float deltaTime);

	public void dispose();
}
