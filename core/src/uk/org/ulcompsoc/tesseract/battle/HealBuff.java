package uk.org.ulcompsoc.tesseract.battle;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.Engine;
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

	private Engine		engine		= null;

	public HealBuff(Engine engine, int ticks) {
		this(engine, 5.0f, ticks);
	}

	public HealBuff(Engine engine, float duration, int ticks) {
		this.duration = duration;
		this.ticks = ticks;
		this.timePerTick = duration / ticks;
		this.engine = engine;
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

			addHealAnimation();

			stats.restoreHP(healPerTick);

			if (ticksDone == (ticks - 1)) {
				undoBuff(target);
				return true;
			}
		}

		return false;
	}

	private void addHealAnimation() {
		Entity e = new Entity();

		Position playerPos = Mappers.position.get(target);

		e.add(new Position(playerPos.position.x + WorldConstants.TILE_WIDTH, playerPos.position.y
				+ WorldConstants.TILE_HEIGHT));
		e.add(TesseractMain.getTempHealRenderable(e));

		engine.addEntity(e);
	}

	@Override
	public void undoBuff(Entity target) {
	}

	@Override
	public String getName() {
		return "Buff of Restoration";
	}

}
