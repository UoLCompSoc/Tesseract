package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Named extends Component {
	public static final String	DEFAULT_NAME	= "Unnamed Combatant";
	public String				name			= null;

	public Named() {
		this(DEFAULT_NAME);
	}

	public Named(String name) {
		this.name = name;
	}
}
