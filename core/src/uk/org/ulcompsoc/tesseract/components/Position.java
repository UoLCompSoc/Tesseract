package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.WorldConstants;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * <p>
 * Holds a position in world coordinates; the position member variable describes
 * the bottom left corner of an Entity, assuming that the Entity is in the
 * (+x,+y) quadrant of world space.
 * </p>
 * 
 * <p>
 * Also provides various utility methods for modifying and setting positions
 * relative to others.
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Position extends Component {
	public Vector2		position		= null;

	private GridPoint2	gridPosition	= null;

	public Position() {
		this(0.0f, 0.0f);
	}

	public Position(float x, float y) {
		this(new Vector2(x, y));
	}

	public Position(Vector2 position) {
		this.position = position;
		this.gridPosition = new GridPoint2();
	}

	public Position setFromGrid(int x, int y) {
		position.x = (float) (x * WorldConstants.TILE_WIDTH);
		position.y = (float) (y * WorldConstants.TILE_HEIGHT);

		return this;
	}

	public Position setFromGrid(GridPoint2 grid) {
		return setFromGrid(grid.x, grid.y);
	}

	public Position setCentredRelativeTo(Rectangle other) {
		return this;
	}

	public GridPoint2 getGridPosition() {
		return gridPosition.set((int) Math.floor(position.x / WorldConstants.TILE_WIDTH),
				(int) Math.floor(position.y / WorldConstants.TILE_HEIGHT));
	}
}
