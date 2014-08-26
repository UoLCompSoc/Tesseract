package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.battle.BattleAttack;
import uk.org.ulcompsoc.tesseract.battle.BattleMessage;
import uk.org.ulcompsoc.tesseract.components.Boss;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.Named;
import uk.org.ulcompsoc.tesseract.components.Player;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.ComponentMapper;
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
	private ComponentMapper<Stats>	statsMapper			= ComponentMapper.getFor(Stats.class);
	private ComponentMapper<Named>	nameMapper			= ComponentMapper.getFor(Named.class);

	private Engine					engine				= null;

	private List<BattleAttack>		attacks				= new ArrayList<BattleAttack>();

	private BattleMessageSystem		messageSystem		= null;

	private Signal<Boolean>			battleEndSignal		= null;
	private Signal<Boolean>			battleDefeatSignal	= null;

	public BattleAttackSystem(BattleMessageSystem messageSystem, int priority) {
		super(priority);
		this.messageSystem = messageSystem;
		this.battleEndSignal = new Signal<Boolean>();
		this.battleDefeatSignal = new Signal<Boolean>();
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
			Stats attackStats = statsMapper.get(atk.attacker);
			Stats defStats = statsMapper.get(atk.target);
			// AttackType attackType = atk.attackType;

			int dmg = BattleAttack.resolveDamage(attackStats, defStats);
			Gdx.app.debug("RESOLVED_DAMAGE", nameMapper.get(atk.attacker).name + " did " + dmg
					+ " point(s) of damage to " + nameMapper.get(atk.target).name + ".");

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
		engine.removeEntity(target);

		if (ComponentMapper.getFor(Player.class).has(target)) {
			doDefeat(target, TesseractStrings.getKilledMessage(nameMapper.get(target).name));
		}

		if (engine.getEntitiesFor(Family.getFor(Enemy.class)).size() == 0) {
			doVictory(target, TesseractStrings.getKilledMessage(nameMapper.get(target).name));
		} else {
			messageSystem.clearAllMessages();
			messageSystem.addMessage(TesseractStrings.getKilledMessage(nameMapper.get(target).name));
		}
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

		battleEndSignal.dispatch(ComponentMapper.getFor(Boss.class).has(target));
	}

	public void doDefeat(Entity target, BattleMessage battleMessage) {
		messageSystem.clearAllMessages();
		messageSystem.addMessage(battleMessage);
		messageSystem.addMessage(TesseractStrings.getDefeatMessage());

		battleDefeatSignal.dispatch(true);
	}
}
