package uk.org.ulcompsoc.tesseract.battle;

import uk.org.ulcompsoc.tesseract.components.Stats;

import com.badlogic.ashley.core.Entity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleAttack {
	public Entity		attacker	= null;
	public Entity		target		= null;
	public AttackType	attackType;

	public BattleAttack(Entity attacker, Entity target, AttackType attackType) {
		this.attacker = attacker;
		this.target = target;
		this.attackType = attackType;
	}

	public static int resolveDamage(Stats attacker, Stats defender) {
		double atk = attacker.getAttack() - defender.getDefence();
		// atk = atk + atk * (new Random().nextInt(11) - 5) * 0.1;

		return Math.max((int) Math.floor(atk), 1);
	}
}
