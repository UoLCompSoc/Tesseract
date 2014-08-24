package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.WorldConstants;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

/**
 * <p>
 * Holds a position in tile coordinates
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Position extends Component {
	public GridPoint2	position		= null;

	private Vector2		worldPosition	= null;

	public Position() {
		this(0, 0);
	}

	public Position(int x, int y) {
		this(new GridPoint2(x, y));
	}

	public Position(GridPoint2 position) {
		this.position = position;
		this.worldPosition = new Vector2();
	}

	public Vector2 getWorldPosition() {
		return worldPosition.set((float) position.x * WorldConstants.TILE_WIDTH, (float) position.y
				* WorldConstants.TILE_HEIGHT);
	}
}
