package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.MouseClickPerformer;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class MouseClickListener extends Component {
	public final Rectangle				rect;
	public final MouseClickPerformer	performer;

	public MouseClickListener(Rectangle rect, MouseClickPerformer performer) {
		this.rect = rect;
		this.performer = performer;
	}

	/**
	 * For convenience
	 * 
	 * @param invoker
	 *        The entity that was being iterated over when this invocation
	 *        happened.
	 */
	public void perform(Entity invoker) {
		performer.perform(invoker);
	}
}
