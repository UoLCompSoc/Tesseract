package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Solid extends Component {
	public float	mass	= 1.0f;

	public Solid(float mass) {
		this.mass = mass;
	}

	public Solid setMass(float mass) {
		this.mass = mass;
		return this;
	}
}
