package uk.org.ulcompsoc.tesseract;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public interface MouseClickPerformer {
	void perform(Entity invoker, Engine engine);
}
