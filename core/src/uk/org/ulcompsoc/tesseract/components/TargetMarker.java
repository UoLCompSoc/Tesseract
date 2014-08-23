package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TargetMarker extends Component {
	public Color	color	= Color.RED;

	public TargetMarker() {
		this(Color.RED);
	}

	public TargetMarker(Color color) {
		this.color = color;
	}
}
