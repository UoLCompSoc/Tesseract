package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Enemy extends Component {
	public static final String	DEFAULT_SPECIES_NAME	= "Unidentified Scoundrel";

	public String				speciesName				= null;

	public Animation			deathAnimation			= null;

	public Enemy() {
		this(DEFAULT_SPECIES_NAME, null);
	}

	public Enemy(String speciesName) {
		this(speciesName, null);
	}

	public Enemy(String speciesName, Animation deathAnimation) {
		this.speciesName = speciesName;
		this.deathAnimation = deathAnimation;
	}

	public boolean hasDeathAnimation() {
		return deathAnimation != null;
	}
}
