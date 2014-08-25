package uk.org.ulcompsoc.tesseract.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class PingPongFrameResolver extends AnimationFrameResolver {
	boolean	first	= true;

	public PingPongFrameResolver() {
		this(0.0f);
	}

	public PingPongFrameResolver(float startTime) {
		this.animTime = startTime;
	}

	@Override
	public float resolveTime(float deltaTime) {
		animTime += deltaTime;
		return animTime;
	}

	@Override
	public TextureRegion resolveFrame(Animation anim, float deltaTime) {
		if (first) {
			anim.setPlayMode(PlayMode.LOOP_PINGPONG);
			first = false;
		}

		return super.resolveFrame(anim, deltaTime);
	}

}
