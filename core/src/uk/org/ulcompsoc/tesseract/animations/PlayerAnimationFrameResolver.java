package uk.org.ulcompsoc.tesseract.animations;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.TesseractMain;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class PlayerAnimationFrameResolver extends AnimationFrameResolver {
	public static final float	DEFAULT_FRAME_DURATION	= 0.08f;

	private Animation			lastAnim				= null;
	private int					animFrame				= 0;

	private boolean				always					= false;

	public float				frameDuration			= DEFAULT_FRAME_DURATION;

	public PlayerAnimationFrameResolver() {
		this(false, DEFAULT_FRAME_DURATION);
	}

	/**
	 * @param always
	 *        true if the animation should play even if the player is not moving
	 */
	public PlayerAnimationFrameResolver(boolean always) {
		this(always, DEFAULT_FRAME_DURATION);
	}

	public PlayerAnimationFrameResolver(float frameDuration) {
		this(false, frameDuration);
	}

	public PlayerAnimationFrameResolver(boolean always, float frameDuration) {
		this.always = always;
		this.frameDuration = frameDuration;
	}

	@Override
	public float resolveTime(float deltaTime) {
		animTime += deltaTime;

		return animTime;
	}

	@Override
	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		if (always || Mappers.moving.has(TesseractMain.worldPlayerEntity)) {
			if (anim != lastAnim) {
				animFrame = 0;
				lastAnim = anim;
				animTime = 0.0f;
			}

			resolveTime(deltaTime);

			if (animTime >= frameDuration) {
				animTime -= frameDuration;
				animFrame++;

				if (animFrame == lastAnim.getKeyFrames().length) {
					animFrame = 0;
				}
			}
		}

		// Gdx.app.debug("FRAME", "Frame is " + animFrame + ".");

		return anim.getKeyFrames()[animFrame];
	}

	/**
	 * @param always
	 *        true if the animation should play even if the player is not moving
	 * @return this for chaining
	 */
	public PlayerAnimationFrameResolver setAlways(boolean always) {
		this.always = always;
		return this;
	}
}
