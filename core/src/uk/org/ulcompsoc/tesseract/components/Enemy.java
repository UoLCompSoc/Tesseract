package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Enemy extends Component {
	public static final String	DEFAULT_SPECIES_NAME	= "Unidentified Scoundrel";

	public String				speciesName				= null;

	public Enemy() {
		this(DEFAULT_SPECIES_NAME);
	}

	public Enemy(String speciesName) {
		this.speciesName = speciesName;
	}
}
