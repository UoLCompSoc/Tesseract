package uk.org.ulcompsoc.tesseract.animations;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class CompletionSignalFrameResolver extends AnimationFrameResolver {
	public Signal<Boolean>	completedSignal	= new Signal<Boolean>();
	private float			signalTime		= -1.0f;

	public CompletionSignalFrameResolver() {
		this(-1.0f, null);
	}

	public CompletionSignalFrameResolver(float signalTime) {
		this(signalTime, null);
	}

	public CompletionSignalFrameResolver(float signalTime, Listener<Boolean> listener) {
		completedSignal.add(listener);

		if (signalTime <= 0.0f) {
			signalTime = -1.0f;
		}

		this.signalTime = signalTime;
	}

	@Override
	public float resolveTime(float deltaTime) {
		animTime += deltaTime;

		return animTime;
	}

	@Override
	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		if (signalTime < 0.0f) {
			signalTime = anim.getAnimationDuration();
		}

		if ((animTime + deltaTime) > signalTime) {
			completedSignal.dispatch(true);
		}

		return super.resolveFrame(anim, deltaTime);
	}
}
