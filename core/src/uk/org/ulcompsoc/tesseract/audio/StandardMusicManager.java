package uk.org.ulcompsoc.tesseract.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class StandardMusicManager implements MusicManager, Disposable {
	public static final float DEFAULT_VOLUME = 0.25f;
	public final Music[] loadedMusic;

	private int playingIndex = -1;

	private float fadeDuration = -1.0f;
	private float remainingFadeTime = -1.0f;

	public StandardMusicManager(final String[] files) {
		final Music[] temp = new Music[files.length];

		for (int i = 0; i < files.length; i++) {
			temp[i] = Gdx.audio.newMusic(Gdx.files.internal(files[i]));
			temp[i].setLooping(true);
			temp[i].setVolume(DEFAULT_VOLUME);
		}

		loadedMusic = temp;
	}

	@Override
	public void play(int index) {
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

	@Override
	public void pause() {
		if (playingIndex == -1) {
			return;
		}

		loadedMusic[playingIndex].pause();
	}

	@Override
	public void stop() {
		if (playingIndex == -1) {
			return;
		}

		loadedMusic[playingIndex].stop();
		playingIndex = -1;

	}

	@Override
	public void fadeOut(float duration) {
		fadeDuration = duration;
		remainingFadeTime = fadeDuration;
	}

	@Override
	public void update(float deltaTime) {
		if (fadeDuration >= 0.0f) {
			remainingFadeTime -= deltaTime;

			if (remainingFadeTime <= 0.0f) {
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
		for (final Music m : loadedMusic) {
			m.dispose();
		}
	}
}
