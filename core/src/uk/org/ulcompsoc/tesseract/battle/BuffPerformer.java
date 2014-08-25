package uk.org.ulcompsoc.tesseract.battle;

import com.badlogic.ashley.core.Entity;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public interface BuffPerformer {
	public void doBuff(Entity target);

	/**
	 * @return true if done, false otherwise.
	 */
	public boolean buffUpdate(float deltaTime);

	public void undoBuff(Entity target);

	public String getName();
}
