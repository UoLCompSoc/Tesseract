package uk.org.ulcompsoc.tesseract.animations;

import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.components.Moving;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class PlayerAnimationFrameResolver extends AnimationFrameResolver {
	private boolean				isAnim			= false;
	private Animation			animCache		= null;
	private int					animFrame		= 0;
	private int					animDir			= 1;

	public static final float	FRAME_DURATION	= 0.2f;

	@Override
	public float resolveTime(float deltaTime) {
		animTime += deltaTime;

		return animTime;
	}

	@Override
	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		if (ComponentMapper.getFor(Moving.class).has(TesseractMain.worldPlayerEntity)) {
			if (isAnim) {
				if (anim != animCache) {
					animFrame = 0;
					animCache = anim;
					animTime = 0.0f;
					animDir = 1;
				}

				resolveTime(deltaTime);

				if (animTime >= FRAME_DURATION) {
					animTime -= FRAME_DURATION;
					animFrame += animDir;

					if (animFrame == animCache.getKeyFrames().length - 1) {
						animDir = -1;

					} else if (animFrame == 0) {
						isAnim = false;
						animTime = 0.0f;
						animDir = 1;
						animFrame = 0;
					}
				}
			} else {
				animCache = anim;
				animDir = 1;
				animTime = 0.0f;
				animFrame = 0;
				isAnim = true;
			}
		}

		return anim.getKeyFrames()[animFrame];
	}
}
