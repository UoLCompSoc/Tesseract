package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.MouseClickPerformer;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class MouseClickListener extends Component implements MouseClickPerformer {
	public final MouseClickPerformer	performer;

	public MouseClickListener(MouseClickPerformer performer) {
		this.performer = performer;
	}

	/**
	 * For convenience
	 * 
	 * @param invoker
	 *        The entity that was being iterated over when this invocation
	 *        happened.
	 */
	@Override
	public void perform(Entity invoker, Engine engine) {
		performer.perform(invoker, engine);
	}
}
