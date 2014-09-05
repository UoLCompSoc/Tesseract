package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Scaled extends Component {
	public final float	scaleAmt;

	public Scaled() {
		this.scaleAmt = 1.0f;
	}

	public Scaled(float scaleAmt) {
		this.scaleAmt = scaleAmt;
	}
}
