package uk.org.ulcompsoc.tesseract.animations;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class SlimeFrameResolver extends AnimationFrameResolver {
	private Random			random		= new Random();

	private TextureRegion[]	keyFrames	= null;

	public SlimeFrameResolver() {
		this.animTime = -1.0f;
	}

	@Override
	public float resolveTime(float deltaTime) {
		if (animTime >= 0.0f) {
			animTime += deltaTime;

			if (animTime > 1.0f) {
				animTime = -1.0f;
			}
		} else {
			if (random.nextInt(250) <= 5) {
				animTime = 0.01f;
			}
		}

		return 0.0f;
	}

	@Override
	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		if (keyFrames == null) {
			keyFrames = anim.getKeyFrames();
		}

		resolveTime(deltaTime);

		if (animTime > 0.0f) {
			return keyFrames[1];
		} else {
			return keyFrames[0];
		}
	}
}
