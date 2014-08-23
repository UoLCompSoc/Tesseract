package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Stats extends Component {
	public int	maxHP		= 0;
	public int	currentHP	= 0;
	public int	attack		= 0;
	public int	defence		= 0;

	public Stats() {
		this(1, 0, 0);
	}

	public Stats(int maxHP, int attack, int defence) {
		this.attack = attack;
		this.defence = defence;
		this.maxHP = maxHP;
		this.currentHP = maxHP;
	}
}
