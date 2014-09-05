package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.WorldConstants;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
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

	/**
	 * <p>
	 * Modifies the x position.
	 * </p>
	 * 
	 * @param x
	 *        the new x position
	 * @return this for chaining
	 */
	public Position setX(float x) {
		position.x = x;
		return this;
	}

	/**
	 * <p>
	 * Adds x to the the x position.
	 * </p>
	 * 
	 * @param x
	 *        the change in x
	 * @return this for chaining
	 */
	public Position adjustX(float x) {
		position.x += x;
		return this;
	}

	/**
	 * <p>
	 * Modifies the y position.
	 * </p>
	 * 
	 * @param y
	 *        the new y position
	 * @return this for chaining
	 */
	public Position setY(float y) {
		position.y = y;
		return this;
	}

	/**
	 * <p>
	 * Adds y to the the y position.
	 * </p>
	 * 
	 * @param y
	 *        the change in y
	 * @return this for chaining
	 */
	public Position adjustY(float y) {
		position.y += y;
		return this;
	}

	/**
	 * <p>
	 * Modifies the x and y positions.
	 * </p>
	 * 
	 * @param x
	 *        the new x position
	 * @param y
	 *        the new y position
	 * @return this for chaining
	 */
	public Position set(float x, float y) {
		position.x = x;
		position.y = y;
		return this;
	}

	/**
	 * <p>
	 * Adds x to the x position and y to the the y position.
	 * </p>
	 * 
	 * @param x
	 *        the change in x
	 * @param y
	 *        the change in y
	 * @return this for chaining
	 */
	public Position adjust(float x, float y) {
		position.x += x;
		position.y += y;
		return this;
	}

	/**
	 * <p>
	 * Sets this position centred in other's X axis, leaving this.position.y
	 * unmodified.
	 * </p>
	 * 
	 * <p>
	 * Note that this method does not respect the size of this {@link Entity},
	 * and so the position might seem off for sized components. Use
	 * smartCentreX(float, Rectangle) if this matters.
	 * </p>
	 * 
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining
	 */
	public Position centreX(Rectangle other) {
		this.position.x = other.x + (other.width / 2.0f);
		return this;
	}

	/**
	 * <p>
	 * Sets this position centred in other's X axis, leaving this.position.y
	 * unmodified. thisWidth is used to make this position seem properly centred
	 * in the target's x axis.
	 * </p>
	 * 
	 * @param thisWidth
	 *        the width of the entity being repositioned.
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining.
	 */
	public Position smartCentreX(float thisWidth, Rectangle other) {
		return smartCentreX(thisWidth, other.x, other.width);
	}

	/**
	 * <p>
	 * Sets this position centred in a given X axis, leaving this.position.y
	 * unmodified. thisWidth is used to make this position seem properly centred
	 * in the target's x axis.
	 * </p>
	 * 
	 * @param thisWidth
	 *        the width of the entity being repositioned.
	 * @param x
	 *        the x position of the rect to centre in (we don't need y because
	 *        it's ignored)
	 * @param width
	 *        the width of the rect
	 * @return this for chaining
	 */
	public Position smartCentreX(float thisWidth, float x, float width) {
		this.position.x = 0.5f * (2.0f * x + width - thisWidth);
		return this;
	}

	/**
	 * <p>
	 * Sets this position centred in other's Y axis, leaving this.position.x
	 * unmodified.
	 * </p>
	 * 
	 * <p>
	 * Note that this method does not respect the size of this {@link Entity},
	 * and so the position might seem off for sized components. Use
	 * smartCentre(float, float, Rectangle) if this matters.
	 * </p>
	 * 
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining
	 */
	public Position centreY(Rectangle other) {
		this.position.y = other.y + (other.height / 2.0f);
		return this;
	}

	/**
	 * <p>
	 * Sets this position centred in other's Y axis, leaving this.position.x
	 * unmodified. thisHeight is used to make this position seem properly
	 * centred in the target's y axis.
	 * </p>
	 * 
	 * @param thisHeight
	 *        the height of the entity being repositioned.
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining.
	 */
	public Position smartCentreY(float thisHeight, Rectangle other) {
		return smartCentreY(thisHeight, other.y, other.height);
	}

	/**
	 * <p>
	 * Sets this position centred in a given Y axis, leaving this.position.x
	 * unmodified. thisHeight is used to make this position seem properly
	 * centred in the target's y axis.
	 * </p>
	 * 
	 * @param thisHeight
	 *        the height of the entity being repositioned.
	 * @param y
	 *        the y coordinate of the space in which we're centring
	 * @param height
	 *        the height of the space in which we're centring
	 * @return this for chaining
	 */
	public Position smartCentreY(float thisHeight, float y, float height) {
		this.position.y = 0.5f * (2.0f * y + height + thisHeight);
		return this;
	}

	/**
	 * <p>
	 * Sets this position centred in other's X and Y axes.
	 * </p>
	 * 
	 * <p>
	 * Note that this method does not respect the size of this {@link Entity},
	 * and so the position might seem off for sized components. Use
	 * smartCentre(float, float, Rectangle) if this matters.
	 * </p>
	 * 
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining
	 */
	public Position centre(Rectangle other) {
		return centreX(other).centreY(other);
	}

	/**
	 * <p>
	 * Sets this position centred in other's X and Y axes. thisWidth and
	 * thisHeight are used to make this position seem properly centred in the
	 * target's axes.
	 * </p>
	 * 
	 * @param thisWidth
	 *        the width of the entity being repositioned.
	 * @param thisHeight
	 *        the height of the entity being repositioned.
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining.
	 */
	public Position smartCentre(float thisWidth, float thisHeight, Rectangle other) {
		return smartCentreX(thisWidth, other).smartCentreY(thisHeight, other);
	}

	/**
	 * <p>
	 * Sets this position centred in given X and Y axes. thisWidth and
	 * thisHeight are used to make this position seem properly centred in the
	 * target's axes.
	 * </p>
	 * 
	 * @param thisWidth
	 *        the width of the entity being repositioned
	 * @param thisHeight
	 *        the height of the entity being repositioned
	 * @param x
	 *        the x coordinate of the rectangle in which we're centring
	 * @param y
	 *        the y coordinate of the rectangle in which we're centring
	 * @param width
	 *        the width of the rectangle in which we're centring
	 * @param height
	 *        the height of the rectangle in which we're centring
	 * @return this for chaining.
	 */
	public Position smartCentre(float thisWidth, float thisHeight, float x, float y, float width, float height) {
		return smartCentreX(thisWidth, x, width).smartCentreY(thisHeight, y, height);
	}

	/**
	 * <p>
	 * Sets this position above other (the y position of this will be set to
	 * other.y + other.height).
	 * </p>
	 * 
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining.
	 */
	public Position above(Rectangle other) {
		this.position.x = other.x;
		this.position.y = other.y + other.height;
		return this;
	}

	public Position above(Position other, float otherHeight) {
		this.position.x = other.position.x;
		this.position.y = other.position.y + otherHeight;

		return this;
	}

	public Position below(Rectangle other, float thisHeight) {
		this.position.x = other.x;
		this.position.y = other.y - thisHeight;

		return this;
	}

	public Position below(Position other, float thisHeight) {
		this.position.x = other.position.x;
		this.position.y = other.position.y - thisHeight;

		return this;
	}

	public Position left(Rectangle other, float thisWidth) {
		this.position.x = other.x - thisWidth;
		this.position.y = other.y;
		return this;
	}

	public Position left(Position other, float thisWidth) {
		this.position.x = other.position.x - thisWidth;
		this.position.y = other.position.y;
		return this;
	}

	public Position bottomRightOf(Rectangle other, float thisWidth) {
		this.position.x = other.width - thisWidth;
		this.position.y = other.y;

		return this;
	}

	/**
	 * <p>
	 * Sets this position right of other (the x position of this will be set to
	 * other.x + other.width).
	 * </p>
	 * 
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining.
	 */
	public Position right(Rectangle other) {
		this.position.x = other.x + other.width;
		this.position.y = other.y;

		return this;
	}

	/**
	 * <p>
	 * Sets this position above and right of other (the x position of this will
	 * be set to other.x + other.width, the y position of this will be set to
	 * other.y + other.height).
	 * </p>
	 * 
	 * @param other
	 *        A rectangle describing the other position; x, y in world
	 *        coordinates, w, h the size of the Entity.
	 * @return this for chaining.
	 */
	public Position aboveAndRight(Rectangle other) {
		this.position.x = other.x + other.width;
		this.position.y = other.y + other.height;

		return this;
	}

	/**
	 * <p>
	 * Sets this position expressed in integer grid coordinates, based on
	 * {@link WorldConstants#TILE_WIDTH} and {@link WorldConstants#TILE_HEIGHT}.
	 * </p>
	 * 
	 * @param x
	 *        The x grid coordinate to use to set this position.
	 * @param y
	 *        The y grid coordinate to use to set this position.
	 * @return this for chaining.
	 */
	public Position setFromGrid(int x, int y) {
		position.x = (float) (x * WorldConstants.TILE_WIDTH);
		position.y = (float) (y * WorldConstants.TILE_HEIGHT);

		return this;
	}

	/**
	 * <p>
	 * Sets this position expressed in integer grid coordinates, based on
	 * {@link WorldConstants#TILE_WIDTH} and {@link WorldConstants#TILE_HEIGHT}.
	 * </p>
	 * 
	 * @param grid
	 *        The grid coordinates to use to set this position.
	 * @return this for chaining.
	 */
	public Position setFromGrid(GridPoint2 grid) {
		return setFromGrid(grid.x, grid.y);
	}

	/**
	 * @return this position expressed in integer grid coordinates, based on
	 *         {@link WorldConstants#TILE_WIDTH} and
	 *         {@link WorldConstants#TILE_HEIGHT}.
	 */
	public GridPoint2 getGridPosition() {
		return gridPosition.set((int) Math.floor(position.x / WorldConstants.TILE_WIDTH),
				(int) Math.floor(position.y / WorldConstants.TILE_HEIGHT));
	}

	/**
	 * <p>
	 * Should be called when the window is resized to adjust positions
	 * appropriately.
	 * </p>
	 * 
	 * <p>
	 * The scaling factor is the old window size divided by the new window size.
	 * </p>
	 * 
	 * @param xScale
	 *        The scaling factor of the change in window size in the x axis.
	 * @param yScale
	 *        The scaling factor of the change in window size in the y axis.
	 */
	public void handleResize(float xScale, float yScale) {
		position.x *= xScale;
		position.y *= yScale;
	}
}
