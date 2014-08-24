package uk.org.ulcompsoc.tesseract.animations;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class PingPongFrameResolver extends AnimationFrameResolver {

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

}
