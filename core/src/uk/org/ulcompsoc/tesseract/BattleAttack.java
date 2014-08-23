package uk.org.ulcompsoc.tesseract;

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
		return (int) Math.floor(attacker.attack - defender.defence);
	}
}
