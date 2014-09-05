package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.MouseClickPerformer;
import uk.org.ulcompsoc.tesseract.input.AlwaysOnPredicate;
import uk.org.ulcompsoc.tesseract.input.InputActivationPredicate;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class MouseClickListener extends Component {
	private final MouseClickPerformer		performer;
	private final InputActivationPredicate	activationPredicate;

	public MouseClickListener(MouseClickPerformer performer) {
		this(performer, null);
	}

	public MouseClickListener(MouseClickPerformer performer, InputActivationPredicate predicate) {
		this.performer = performer;

		if (predicate == null) {
			predicate = new AlwaysOnPredicate();
		}

		this.activationPredicate = predicate;
	}

	/**
	 * 
	 * 
	 * @param invoker
	 *        The entity that was being iterated over when this invocation
	 *        happened.
	 */
	public boolean perform(Entity invoker, Engine engine) {
		if (activationPredicate.isActive()) {
			performer.perform(invoker, engine);
			return true;
		}

		return false;
	}
}
