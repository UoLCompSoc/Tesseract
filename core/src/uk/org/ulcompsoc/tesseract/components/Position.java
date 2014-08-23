package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;

/**
 * <p>
 * Holds a position in tile coordinates
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Position extends Component {
	public GridPoint2	position	= null;

	public Position() {
		this(0, 0);
	}

	public Position(int x, int y) {
		position = new GridPoint2(x, y);
	}
}
