package uk.org.ulcompsoc.tesseract.battle;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.Entity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class HealBuff implements BuffPerformer {
	private Entity		target		= null;
	private Stats		stats		= null;

	public final int	ticks;
	public final float	duration;
	public final float	timePerTick;

	private float		timeElapsed	= 0.0f;
	private float		ticksDone	= 0;

	private int			healPerTick	= 0;

	public HealBuff(int ticks) {
		this(5.0f, ticks);
	}

	public HealBuff(float duration, int ticks) {
		this.duration = duration;
		this.ticks = ticks;
		this.timePerTick = duration / ticks;
	}

	@Override
	public void doBuff(Entity target) {
		this.target = target;
		this.stats = Mappers.stats.get(target);

		this.healPerTick = (int) Math.floor(stats.maxHP * 0.05f);
		this.timeElapsed = 0.0f;
	}

	@Override
	public boolean buffUpdate(float deltaTime) {
		timeElapsed += deltaTime;

		if (timeElapsed > timePerTick) {
			timeElapsed -= timePerTick;

			ticksDone++;

			stats.restoreHP(healPerTick);

			if (ticksDone == (ticks - 1)) {
				undoBuff(target);
				return true;
			}
		}

		return false;
	}

	@Override
	public void undoBuff(Entity target) {
	}

	@Override
	public String getName() {
		return "Buff of Restoration";
	}

}
