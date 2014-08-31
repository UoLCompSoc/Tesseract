package uk.org.ulcompsoc.tesseract.dialoguelisteners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public abstract class DialogueFinishListener implements Listener<Entity> {
	@Override
	public abstract void receive(Signal<Entity> signal, Entity object);
}
