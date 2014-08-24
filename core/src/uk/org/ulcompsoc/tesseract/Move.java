package uk.org.ulcompsoc.tesseract;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Move {
	public enum Type {
		TILE, ABSOLUTE;
	}

	public final Entity		mover;

	public final Vector2	start;

	public final float		nx;
	public final float		ny;
	public final Type		type;

	public final int		stepCount;
	public final float		timePerStep;

	public int				currentStep;

	public float			durationRemaining;
	public float			stepTime;

	public final boolean	isSolid;

	// alternates between x and y coords; 0 = x, 1 = y, 2 = x, etc.
	public final float[]	steps;

	public Move(Entity mover, Type type, Vector2 start, float nx, float ny, float duration, boolean isSolid) {
		this.mover = mover;

		this.type = type;

		this.start = start;

		this.nx = nx;
		this.ny = ny;

		this.currentStep = 0;
		this.stepCount = calcStepCount();

		this.durationRemaining = duration;
		this.stepTime = 0.0f;
		this.timePerStep = durationRemaining / (float) this.stepCount;

		this.isSolid = isSolid;

		float[] stepsTemp = new float[2 * stepCount];

		final float xDiff = nx - start.x;
		final float yDiff = ny - start.y;

		final float xStepSize = xDiff / stepCount;
		final float yStepSize = yDiff / stepCount;

		for (int i = 0; i < 2 * stepCount; i += 2) {
			if (i == 0) {
				stepsTemp[i] = start.x;
				stepsTemp[i + 1] = start.y;
			} else if (i == (2 * stepCount) - 2) {
				stepsTemp[i] = nx;
				stepsTemp[i + 1] = ny;
			} else {
				stepsTemp[i] = start.x + xStepSize * (i / 2);
				stepsTemp[i + 1] = start.y + yStepSize * (i / 2);
			}
		}

		steps = stepsTemp;
	}

	public boolean update(float deltaTime) {
		durationRemaining -= deltaTime;
		stepTime += deltaTime;

		final int stepsThisTick = (int) (stepTime / timePerStep);

		if (stepsThisTick > 0) {
			stepTime = stepTime % timePerStep;
			currentStep += stepsThisTick;

			if (currentStep >= stepCount) {
				currentStep = stepCount - 1;
			}
		}

		return isDone();
	}

	public boolean isDone() {
		return durationRemaining <= 0.0f;
	}

	public float getCurrentX() {
		return steps[currentStep * 2];
	}

	public float getCurrentY() {
		return steps[currentStep * 2 + 1];
	}

	// only meaningful at constructor, returns min of 1
	private int calcStepCount() {
		float dist = (float) Math.sqrt(Math.pow(ny - start.y, 2) + Math.pow(nx - start.x, 2));
		int stepCount = (int) Math.ceil(dist / WorldConstants.MOVE_STEP_LENGTH);

		return (stepCount == 0 ? 1 : stepCount);
	}
}
