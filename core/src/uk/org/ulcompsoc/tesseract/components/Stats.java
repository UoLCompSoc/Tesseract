package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Stats extends Component {
	public int				maxHP			= 0;

	private int				currentHP		= 0;

	public int				strength		= 10;
	public int				intelligence	= 10;
	public int				fortitude		= 5;

	private int				level			= 1;
	private int				totalExp		= 0;

	public Signal<Stats>	hpChangeSignal	= new Signal<Stats>();
	public Signal<Integer>	levelUpListener	= new Signal<Integer>();

	public Stats() {
		this(100, 10, 5, 10);
	}

	public Stats(int maxHP, int strength, int fortitude, int intelligence) {
		this.maxHP = maxHP;
		this.currentHP = maxHP;

		this.strength = strength;
		this.fortitude = fortitude;
		this.intelligence = intelligence;
	}

	public void restoreHP(int amt) {
		currentHP += amt;

		if (currentHP > maxHP) {
			currentHP = maxHP;
		}

		hpChangeSignal.dispatch(this);
	}

	public void damageHP(int amt) {
		currentHP -= amt;

		if (currentHP < 0) {
			currentHP = 0;
		}

		hpChangeSignal.dispatch(this);
	}

	public int getHP() {
		return currentHP;
	}

	public int getAttack() {
		return resolveAttack(strength);
	}

	public int getDefence() {
		return resolveDefence(fortitude);
	}

	public float getThinkTime() {
		final float min = 0.4f;
		final float t = min + Math.max((100 - intelligence), 0) * 0.05f;
		return t;
	}

	public void addExperience(int exp) {
		totalExp += exp;
		Gdx.app.debug("ADD_EXP", "Gained " + exp + " experience points; " + (levelUpExperience(level) - totalExp)
				+ " until next level.");

		if (totalExp >= levelUpExperience(level)) {
			doLevelUp();
		}
	}

	public int experienceNeededForLevelUp() {
		return levelUpExperience(level) - totalExp;
	}

	public int getLevel() {
		return level;
	}

	public void doLevelUp() {
		level++;
		Gdx.app.debug("LEVEL_UP", "Levelled up to level " + level + ".");

		strength++;
		intelligence++;
		fortitude++;

		maxHP += 10;
		currentHP += 10;
		levelUpListener.dispatch(level);
	}

	public static int resolveAttack(int strength) {
		return strength * 2;
	}

	public static int resolveDefence(int fortitude) {
		return fortitude;
	}

	public static int levelUpExperience(int level) {
		int total = 0;

		for (int i = 0; i < level; i++) {
			total += i * 11 + (i + 1) * 5;
		}

		return total;
	}
}
