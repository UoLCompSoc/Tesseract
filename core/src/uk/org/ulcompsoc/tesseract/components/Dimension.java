package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Dimension extends Component {
	public float	width	= 0;
	public float	height	= 0;

	public Dimension() {
		this(0, 0);
	}

	public Dimension(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public static Dimension relativeDimension(Rectangle parent, float relWidth, float relHeight) {
		return relativeDimension(parent.width, parent.height, relWidth, relHeight);
	}

	public static Dimension relativeDimension(float parentWidth, float parentHeight, float relWidth, float relHeight) {
		return new Dimension(parentWidth * relWidth, parentHeight * relHeight);
	}
}
