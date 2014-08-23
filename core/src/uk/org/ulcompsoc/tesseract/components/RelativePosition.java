package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class RelativePosition extends Component {
	// values in range 0.0f -> 1.0f represent how far along the screen to render
	// eg 0.5f = halfway
	public final Rectangle	rect;

	// parent rectangle, with coordinates and sizes in window pixels
	public final Rectangle	parent;

	public final Rectangle	pos;

	public RelativePosition(Rectangle rect, Rectangle parent) {
		this.rect = rect;
		this.parent = parent;
		pos = new Rectangle((parent.width - parent.x) * rect.x, (parent.height - parent.y) * rect.y,
				(parent.width - parent.x) * rect.width, (parent.height - parent.y) * rect.height);
	}

	public RelativePosition(Rectangle rect, Entity parentEntity) {
		ComponentMapper<RelativePosition> relPosMapper = ComponentMapper.getFor(RelativePosition.class);

		if (!relPosMapper.has(parentEntity)) {
			throw new GdxRuntimeException(
					"Call to RelativePosition(Rectangle, Entity) with parent which has no RelativePosition.");
		} else {
			this.rect = rect;
			this.parent = relPosMapper.get(parentEntity).pos;
			pos = new Rectangle((parent.width - parent.x) * rect.x, (parent.height - parent.y) * rect.y,
					(parent.width - parent.x) * rect.width, (parent.height - parent.y) * rect.height);
		}
	}

	/**
	 * <p>
	 * Centres rect in parent's x axis and returns this centring as a relative
	 * position.
	 * </p>
	 * <p>
	 * Must have rect.width < parent.width
	 * </p>
	 * 
	 * @param rect
	 *        rectangle using window coordinates describing the object to centre
	 * @param parent
	 *        rectangle in which to centre rect, in window coordinates, probably
	 *        from an existing RelativePosition's pos member.
	 * @return A relative position representing an x-centred rect relative to
	 *         parent.
	 */
	public static RelativePosition makeCentredX(Rectangle rect, Rectangle parent) {
		if (rect.width > parent.width) {
			throw new GdxRuntimeException("rect.width > parent.width when centring on x.");
		}

		final float parentXCentre = parent.width / 2.0f;
		final float childXAbs = parentXCentre - (rect.width / 2.0f);
		final float childXRel = childXAbs / parent.width;

		final float childY = rect.height / parent.height;

		final float childWidth = rect.width / parent.width;
		final float childHeight = rect.height / parent.height;

		return new RelativePosition(new Rectangle(childXRel, childY, childWidth, childHeight), parent);
	}

	/**
	 * <p>
	 * Centres rect in parent's x axis and returns this centring as a relative
	 * position.
	 * </p>
	 * <p>
	 * Must have rect.width < parent.pos.width
	 * </p>
	 * 
	 * @param rect
	 *        rectangle using window coordinates describing the object to centre
	 * @param parent
	 *        entity containing a RelativePosition component in which to centre
	 *        rect, in window coordinates
	 * @return A relative position representing an x-centred rect relative to
	 *         parent.
	 */
	public static RelativePosition makeCentredX(Rectangle rect, Entity parent) {
		ComponentMapper<RelativePosition> relPosMapper = ComponentMapper.getFor(RelativePosition.class);

		if (!relPosMapper.has(parent)) {
			throw new GdxRuntimeException(
					"Call to makeCentredX(Rectangle, Entity) with parent which has no RelativePosition.");
		} else {
			return RelativePosition.makeCentredX(rect, relPosMapper.get(parent).pos);
		}
	}

	/**
	 * <p>
	 * Centres rect in parent's y axis and returns this centring as a relative
	 * position.
	 * </p>
	 * <p>
	 * Must have rect.height < parent.height
	 * </p>
	 * 
	 * @param rect
	 *        rectangle using window coordinates describing the object to centre
	 * @param parent
	 *        rectangle in which to centre rect, in window coordinates, probably
	 *        from an existing RelativePosition's pos member.
	 * @return A relative position representing an y-centred rect relative to
	 *         parent.
	 */
	public static RelativePosition makeCentredY(Rectangle rect, Rectangle parent) {
		if (rect.height > parent.height) {
			throw new GdxRuntimeException("rect.height > parent.height when centring on y.");
		}

		final float parentYCentre = parent.height / 2.0f;
		final float childYAbs = parentYCentre + (rect.height / 2.0f);
		final float childYRel = childYAbs / parent.height;

		final float childX = rect.width / parent.width;

		final float childWidth = rect.width / parent.width;
		final float childHeight = rect.height / parent.height;

		return new RelativePosition(new Rectangle(childX, childYRel, childWidth, childHeight), parent);
	}

	/**
	 * <p>
	 * Centres rect in parent's y axis and returns this centring as a relative
	 * position.
	 * </p>
	 * <p>
	 * Must have rect.height < parent.pos.height
	 * </p>
	 * 
	 * @param rect
	 *        rectangle using window coordinates describing the object to centre
	 * @param parent
	 *        entity containing a RelativePosition component in which to centre
	 *        rect, in window coordinates
	 * @return A relative position representing an y-centred rect relative to
	 *         parent.
	 */
	public static RelativePosition makeCentredY(Rectangle rect, Entity parent) {
		ComponentMapper<RelativePosition> relPosMapper = ComponentMapper.getFor(RelativePosition.class);

		if (!relPosMapper.has(parent)) {
			throw new GdxRuntimeException(
					"Call to makeCentredY(Rectangle, Entity) with parent which has no RelativePosition.");
		} else {
			return RelativePosition.makeCentredY(rect, relPosMapper.get(parent).pos);
		}
	}

	/**
	 * <p>
	 * Centres rect in parent's x and y axes and returns this centring as a
	 * relative position.
	 * </p>
	 * <p>
	 * Must have rect.height < parent.height and rect.width < parent.width
	 * </p>
	 * 
	 * @param rect
	 *        rectangle using window coordinates describing the object to centre
	 * @param parent
	 *        rectangle in which to centre rect, in window coordinates, probably
	 *        from an existing RelativePosition's pos member.
	 * @return A relative position representing an y-centred rect relative to
	 *         parent.
	 */
	public static RelativePosition makeCentred(Rectangle rect, Rectangle parent) {
		if (rect.width > parent.width) {
			throw new GdxRuntimeException("rect.width > parent.width when centring on both.");
		} else if (rect.height > parent.height) {
			throw new GdxRuntimeException("rect.height > parent.height when centring on both.");
		}

		final float parentXCentre = parent.width / 2.0f;
		final float childXAbs = parentXCentre - (rect.width / 2.0f);
		final float childXRel = childXAbs / parent.width;

		final float parentYCentre = parent.height / 2.0f;
		final float childYAbs = parentYCentre + (rect.height / 2.0f);
		final float childYRel = childYAbs / parent.height;

		final float childWidth = rect.width / parent.width;
		final float childHeight = rect.height / parent.height;

		return new RelativePosition(new Rectangle(childXRel, childYRel, childWidth, childHeight), parent);
	}

	/**
	 * <p>
	 * Centres rect in parent's x and y axes and returns this centring as a
	 * relative position.
	 * </p>
	 * <p>
	 * Must have rect.height < parent.pos.height
	 * </p>
	 * 
	 * @param rect
	 *        rectangle using window coordinates describing the object to centre
	 * @param parent
	 *        entity containing a RelativePosition component in which to centre
	 *        rect, in window coordinates
	 * @return A relative position representing an y-centred rect relative to
	 *         parent.
	 */
	public static RelativePosition makeCentred(Rectangle rect, Entity parent) {
		ComponentMapper<RelativePosition> relPosMapper = ComponentMapper.getFor(RelativePosition.class);

		if (!relPosMapper.has(parent)) {
			throw new GdxRuntimeException(
					"Call to makeCentred(Rectangle, Entity) with parent which has no RelativePosition.");
		} else {
			return RelativePosition.makeCentred(rect, relPosMapper.get(parent).pos);
		}
	}
}
