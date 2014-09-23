package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.animations.CompletionSignalFrameResolver;
import uk.org.ulcompsoc.tesseract.battle.BattleAttack;
import uk.org.ulcompsoc.tesseract.battle.BattleMessage;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleAttackSystem extends EntitySystem {
	private Engine				engine				= null;

	private List<BattleAttack>	attacks				= new ArrayList<BattleAttack>();

	private BattleMessageSystem	messageSystem		= null;

	private Signal<Boolean>		battleEndSignal		= null;
	private Signal<Boolean>		battleDefeatSignal	= null;

	private Random				random				= null;

	public BattleAttackSystem(BattleMessageSystem messageSystem, int priority) {
		super(priority);

		this.messageSystem = messageSystem;
		this.battleEndSignal = new Signal<Boolean>();
		this.battleDefeatSignal = new Signal<Boolean>();
		this.random = new Random();
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		this.engine = null;
	}

	@Override
	public void update(float deltaTime) {
		while (attacks.size() > 0) {
			BattleAttack atk = attacks.get(0);

			createBattleAttackAnimation(atk.target);

			Stats attackStats = Mappers.stats.get(atk.attacker);
			Stats defStats = Mappers.stats.get(atk.target);

			int dmg = BattleAttack.resolveDamage(attackStats, defStats);
			Gdx.app.debug("RESOLVED_DAMAGE", Mappers.named.get(atk.attacker).name + " did " + dmg
					+ " point(s) of damage to " + Mappers.named.get(atk.target).name + ".");

			defStats.damageHP(dmg);

			if (defStats.getHP() <= 0) {
				attackStats.addExperience(defStats.getLevel() * 2);
				killTarget(atk.target);
			}

			attacks.remove(0);
		}
	}

	@Override
	public boolean checkProcessing() {
		return attacks.size() > 0;
	}

	public void addAttack(BattleAttack attack) {
		attacks.add(attack);
	}

	@SuppressWarnings("unchecked")
	protected void killTarget(Entity target) {
		if (Mappers.player.has(target)) {
			engine.removeEntity(target);
			doDefeat(target, TesseractStrings.getKilledMessage(Mappers.named.get(target).name));
		} else {
			Gdx.app.debug("ENEMY_KILLED", "Enemy killed; "
					+ (engine.getEntitiesFor(Family.getFor(Enemy.class)).size() - 1) + " remain.");

			Enemy enemy = Mappers.enemy.get(target);

			if (enemy.hasDeathAnimation()) {
				Renderable newEnemyRenderable = new Renderable(enemy.deathAnimation)
						.setAnimationResolver(new CompletionSignalFrameResolver(enemy.deathAnimation
								.getAnimationDuration(), new TesseractMain.AnimationCompleteListener(target)));
				newEnemyRenderable.color = Mappers.renderable.get(target).color;

				target.add(newEnemyRenderable);
			} else {
				engine.removeEntity(target);
			}
		}

		// == 1 because the entity won't actually be deleted until the engine
		// finishes updating
		if (engine.getEntitiesFor(Family.getFor(Enemy.class)).size() == 1) {
			doVictory(target, TesseractStrings.getKilledMessage(Mappers.named.get(target).name));
		} else {
			messageSystem.clearAllMessages();
			messageSystem.addMessage(TesseractStrings.getKilledMessage(Mappers.named.get(target).name));
		}
	}

	private void createBattleAttackAnimation(Entity target) {
		final Position targetPosition = Mappers.position.get(target);

		final float targetWidth = Mappers.renderable.get(target).width;
		final float targetHeight = Mappers.renderable.get(target).height;

		final float xOffset = random.nextFloat() * targetWidth;
		final float yOffset = random.nextFloat() * targetHeight;

		Entity attackEntity = new Entity();

		attackEntity.add(new Position(targetPosition.position.x + xOffset, targetPosition.position.y + yOffset));
		attackEntity.add(TesseractMain.getTempAttackRenderable(attackEntity));
		engine.addEntity(attackEntity);
	}

	public BattleAttackSystem addVictoryListener(Listener<Boolean> listener) {
		battleEndSignal.add(listener);
		return this;
	}

	public BattleAttackSystem addDefeatListener(Listener<Boolean> listener) {
		battleDefeatSignal.add(listener);
		return this;
	}

	public void doVictory(Entity target, BattleMessage lastMessage) {
		messageSystem.clearAllMessages();
		messageSystem.addMessage(lastMessage);
		messageSystem.addMessage(TesseractStrings.getVictoryMessage());

		battleEndSignal.dispatch(Mappers.boss.has(target));
	}

	public void doDefeat(Entity target, BattleMessage battleMessage) {
		messageSystem.clearAllMessages();
		messageSystem.addMessage(battleMessage);
		messageSystem.addMessage(TesseractStrings.getDefeatMessage());

		battleDefeatSignal.dispatch(true);
	}
}
