package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.battle.BattleAttack;
import uk.org.ulcompsoc.tesseract.components.Named;
import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
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

	public BattleAttackSystem(BattleMessageSystem messageSystem, int priority) {
		super(priority);
		this.messageSystem = messageSystem;
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
			Gdx.app.debug("RESOLVED_DAMAGE", "Did " + dmg + " points of damage.");

			defStats.currentHP -= dmg;

			if (defStats.currentHP <= 0) {
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

	protected void killTarget(Entity target) {
		messageSystem.addMessage(TesseractStrings.getKilledMessage(nameMapper.get(target).name));
		engine.removeEntity(target);
	}
}
