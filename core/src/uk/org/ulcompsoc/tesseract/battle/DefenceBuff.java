package uk.org.ulcompsoc.tesseract.battle;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.Entity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class DefenceBuff implements BuffPerformer {
	public final float	duration;
	public float		timeRemaining	= -1.0f;

	private Entity		target			= null;
	private Stats		stats			= null;
	private int			buffAmt			= 0;

	public DefenceBuff() {
		this(5.0f);
	}

	public DefenceBuff(float duration) {
		this.duration = duration;
	}

	@Override
	public void doBuff(Entity target) {
		this.target = target;
		stats = Mappers.stats.get(target);

		buffAmt = stats.getDefence();

		stats.fortitude += buffAmt;

		this.timeRemaining = duration;
	}

	@Override
	public boolean buffUpdate(float deltaTime) {
		timeRemaining -= deltaTime;

		if (timeRemaining <= 0.0f) {
			undoBuff(target);
			return true;
		}

		return false;
	}

	@Override
	public void undoBuff(Entity target) {
		timeRemaining = -1.0f;
		stats.fortitude -= buffAmt;
	}

	@Override
	public String getName() {
		return "Buff of Fortitude";
	}
}
