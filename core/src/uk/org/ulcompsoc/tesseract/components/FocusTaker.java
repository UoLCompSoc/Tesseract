package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class FocusTaker extends Component {
	public Camera	camera	= null;

	public FocusTaker(Camera camera) {
		this.camera = camera;
	}
}
