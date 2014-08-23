package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Player extends Component {
	public static final String	DEFAULT_HERO_NAME	= "Valiant Hero";

	public String				name;

	public Player() {
		this(DEFAULT_HERO_NAME);
	}

	public Player(String name) {
		this.name = name;
	}
}
