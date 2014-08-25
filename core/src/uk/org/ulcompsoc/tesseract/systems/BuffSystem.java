package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.battle.BuffPerformer;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BuffSystem extends IteratingSystem {
	private ComponentMapper<Combatant>	combatantMapper	= ComponentMapper.getFor(Combatant.class);
	private ComponentMapper<Stats>		statsMapper		= ComponentMapper.getFor(Stats.class);

	private List<BuffPerformer>			toRemove		= new ArrayList<BuffPerformer>();

	@SuppressWarnings("unchecked")
	public BuffSystem(int priority) {
		super(Family.getFor(Combatant.class), priority);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Combatant cm = combatantMapper.get(entity);
		Stats stats = statsMapper.get(entity);

		for (BuffPerformer performer : cm.updatingBuffs) {
			if (performer.buffUpdate(deltaTime)) {
				toRemove.add(performer);
			}
		}

		for (BuffPerformer toRem : toRemove) {
			cm.updatingBuffs.remove(toRem);
		}

		toRemove.clear();

		for (BuffPerformer newBuff : cm.addedBuffs) {
			newBuff.doBuff(entity);
			cm.updatingBuffs.add(newBuff);
		}

		cm.addedBuffs.clear();

		if (!cm.canAct()) {
			cm.thinkingTime += deltaTime;

			if (cm.thinkingTime > stats.getThinkTime()) {
				cm.thinkingTime = -1.0f;
			}
		}
	}
}
