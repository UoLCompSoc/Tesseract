package uk.org.ulcompsoc.tesseract.animations;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public abstract class AnimationFrameResolver {
	public final Signal<Boolean>	completionSignal	= new Signal<Boolean>();
	public float					animTime			= 0.0f;

	public AnimationFrameResolver() {
		this(null);
	}

	public AnimationFrameResolver(Listener<Boolean> listener) {
		if (listener != null) {
			completionSignal.add(listener);
		}
	}

	public abstract float resolveTime(float deltaTime);

	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		return anim.getKeyFrame(resolveTime(deltaTime));
	}
}
