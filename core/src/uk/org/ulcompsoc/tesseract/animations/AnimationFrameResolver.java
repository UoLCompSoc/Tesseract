package uk.org.ulcompsoc.tesseract.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public abstract class AnimationFrameResolver {
	public float	animTime	= 0.0f;

	public abstract float resolveTime(float deltaTime);

	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		return anim.getKeyFrame(resolveTime(deltaTime));
	}
}
