package uk.org.ulcompsoc.tesseract;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class MusicManager implements Disposable {
	public static final float	DEFAULT_VOLUME		= 0.75f;
	public final Music[]		loadedMusic;

	private int					playingIndex		= -1;
	private boolean				isPaused			= false;

	private float				fadeDuration		= -1.0f;
	private float				remainingFadeTime	= -1.0f;

	public MusicManager(String[] files) {
		Music[] temp = new Music[files.length];

		for (int i = 0; i < files.length; i++) {
			temp[i] = Gdx.audio.newMusic(Gdx.files.internal(files[i]));
			temp[i].setLooping(true);
			temp[i].setVolume(DEFAULT_VOLUME);
		}

		loadedMusic = temp;
	}

	public void play(int index) {
		Gdx.app.debug("PLAY", "");
		if (playingIndex != -1) {
			loadedMusic[playingIndex].stop();
		}

		if (index < 0) {
			throw new GdxRuntimeException("Invalid index in MusicManager.play");
		} else if (index >= loadedMusic.length) {
			index = index % loadedMusic.length;
		}

		loadedMusic[index].setVolume(DEFAULT_VOLUME);
		loadedMusic[index].play();
		playingIndex = index;
	}

	public void pause() {
		if (playingIndex == -1) {
			return;
		}

		isPaused = true;
		loadedMusic[playingIndex].pause();
	}

	public void stop() {
		if (playingIndex == -1) {
			return;
		}

		loadedMusic[playingIndex].stop();
		playingIndex = -1;
		isPaused = false;
	}

	/**
	 * Fades the currently playing song over duration seconds.
	 * 
	 * @param duration
	 *        The duration of the fade.
	 */
	public void fadeOut(float duration) {
		fadeDuration = duration;
		remainingFadeTime = fadeDuration;
	}

	public void update(float deltaTime) {
		if (fadeDuration >= 0.0f) {
			remainingFadeTime -= deltaTime;

			if (remainingFadeTime <= 0.0f) {
				Gdx.app.debug("FADE_COMPLETE", "");
				fadeDuration = -1.0f;
				remainingFadeTime = -1.0f;
				loadedMusic[playingIndex].setVolume(DEFAULT_VOLUME);
			} else {
				loadedMusic[playingIndex].setVolume(Math.min(DEFAULT_VOLUME, remainingFadeTime / fadeDuration));
			}
		}
	}

	@Override
	public void dispose() {
		for (Music m : loadedMusic) {
			m.dispose();
		}
	}
}
