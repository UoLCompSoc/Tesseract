package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.battle.AttackType;
import uk.org.ulcompsoc.tesseract.battle.BattleAttack;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleAISystem extends IteratingSystem {
	private Engine	engine	= null;

	@SuppressWarnings("unchecked")
	public BattleAISystem(int priority) {
		super(Family.getFor(Combatant.class, Enemy.class, Stats.class), priority);
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.engine = engine;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		this.engine = engine;
	}

	@SuppressWarnings("unused")
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Stats stats = Mappers.stats.get(entity);
		Combatant cm = Mappers.combatant.get(entity);

		if (cm.canAct()) {
			cm.thinkingTime = 0.0f;
			engine.getSystem(BattleAttackSystem.class).addAttack(
					new BattleAttack(entity, TesseractMain.battlePlayerEntity, AttackType.MELEE));
		}
	}
}
