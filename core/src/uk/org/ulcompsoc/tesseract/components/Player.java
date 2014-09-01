package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Player extends Component {
	public static final String	DEFAULT_HERO_NAME	= "Valiant Hero";
	public static final int		DEFAULT_POWER_LEVEL	= 0;

	public String				name;
	public int					powerLevel;

	public Player() {
		this(DEFAULT_HERO_NAME, DEFAULT_POWER_LEVEL);
	}

	public Player(String name) {
		this(name, DEFAULT_POWER_LEVEL);
	}

	public Player(int powerLevel) {
		this(DEFAULT_HERO_NAME, powerLevel);
	}

	public Player(String name, int powerLevel) {
		this.name = name;
		this.powerLevel = powerLevel;
	}
}
