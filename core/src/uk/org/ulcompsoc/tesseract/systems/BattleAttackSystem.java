package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.battle.BattleAttack;
import uk.org.ulcompsoc.tesseract.battle.BattleMessage;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.Named;
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
	private ComponentMapper<Stats>	statsMapper		= ComponentMapper.getFor(Stats.class);
	private ComponentMapper<Named>	nameMapper		= ComponentMapper.getFor(Named.class);

	private Engine					engine			= null;

	private List<BattleAttack>		attacks			= new ArrayList<BattleAttack>();

	private BattleMessageSystem		messageSystem	= null;

	private Signal<Float>			battleEndSignal	= null;

	public BattleAttackSystem(BattleMessageSystem messageSystem, int priority) {
		super(priority);
		this.messageSystem = messageSystem;
		this.battleEndSignal = new Signal<Float>();
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
			Gdx.app.debug("BATT_ATK_UPDATE", "" + attacks.size() + " attacks left to process.");
			BattleAttack atk = attacks.get(0);
			Stats attackStats = statsMapper.get(atk.attacker);
			Stats defStats = statsMapper.get(atk.target);
			// AttackType attackType = atk.attackType;

			int dmg = BattleAttack.resolveDamage(attackStats, defStats);
			Gdx.app.debug("RESOLVED_DAMAGE", "Did " + dmg + " points of damage to " + nameMapper.get(atk.target).name
					+ ".");

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

		if (engine.getEntitiesFor(Family.getFor(Enemy.class)).size() == 0) {
			doVictory(TesseractStrings.getKilledMessage(nameMapper.get(target).name));
		} else {
			messageSystem.addMessage(TesseractStrings.getKilledMessage(nameMapper.get(target).name));
		}
	}

	public BattleAttackSystem addVictoryListener(Listener<Float> listener) {
		battleEndSignal.add(listener);
		return this;
	}

	public void doVictory(BattleMessage lastMessage) {
		messageSystem.clearAllMessages();
		messageSystem.addMessage(lastMessage);
		messageSystem.addMessage(TesseractStrings.getVictoryMessage());
		battleEndSignal.dispatch(5.0f);
	}
}
