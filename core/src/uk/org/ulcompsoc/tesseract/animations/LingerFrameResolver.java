package uk.org.ulcompsoc.tesseract.animations;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * <p>
 * "Lingers" on a given frame. That is, spends 10% of the total anim time
 * building up to that frame, then 80% on the frame, and 10% completing the
 * animation.
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class LingerFrameResolver extends AnimationFrameResolver {
	private int			lingerFrame			= -1;

	private float		elapsedTime			= -1.0f;

	private float		totalTime			= -1.0f;
	private float		lingerTime			= -1.0f;
	private float		buildUpTime			= -1.0f;

	private float		lingerTimePoint		= -1.0f;
	private float		tearDownTimePoint	= -1.0f;

	private LingerState	state				= LingerState.NONE;

	private enum LingerState {
		NONE, BUILDUP, LINGER, TEARDOWN;
	}

	public LingerFrameResolver(int lingerFrame) {
		this(lingerFrame, null);
	}

	public LingerFrameResolver(int lingerFrame, Listener<Boolean> listener) {
		super(listener);

		this.lingerFrame = lingerFrame;
	}

	@Override
	public float resolveTime(float deltaTime) {
		elapsedTime += deltaTime;

		switch (state) {
		case BUILDUP:
			if (elapsedTime > buildUpTime) {
				state = LingerState.LINGER;
				elapsedTime = lingerTimePoint;
			}

			break;

		case LINGER:
			if (elapsedTime > lingerTime) {
				state = LingerState.TEARDOWN;
				elapsedTime = tearDownTimePoint;

				break;
			} else {
				return lingerTimePoint;
			}

		case TEARDOWN:
			if (elapsedTime > totalTime) {
				completionSignal.dispatch(true);
				state = LingerState.NONE;
				elapsedTime = totalTime;
			}

			break;

		case NONE:
			break;
		default:
			break;
		}

		return elapsedTime;
	}

	@Override
	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		if (!isInit()) {
			init(anim);
		}

		return super.resolveFrame(anim, deltaTime);
	}

	private boolean isInit() {
		return state != LingerState.NONE;
	}

	private void init(Animation anim) {
		totalTime = anim.getAnimationDuration();

		buildUpTime = totalTime * 0.1f;
		lingerTime = buildUpTime + totalTime * 0.8f;

		lingerTimePoint = anim.getFrameDuration() * (lingerFrame + 0.5f);
		tearDownTimePoint = anim.getFrameDuration() * (lingerFrame + 1);

		elapsedTime = 0.0f;

		state = LingerState.BUILDUP;
	}
}
