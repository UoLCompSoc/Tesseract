package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Dialogue extends Component {
	public String[]			dialogueLines		= null;

	public Signal<Entity>	finishSignal		= null;

	public int				interactionWidth	= 0;
	public int				interactionHeight	= 0;

	public Dialogue(String[] dialogueLines) {
		this.dialogueLines = dialogueLines;
		this.finishSignal = new Signal<Entity>();
	}

	public Dialogue addFinishListener(Listener<Entity> listener) {
		finishSignal.add(listener);
		return this;
	}

	public static String[] parseDialogueLines(String raw) {
		String[] retVal = raw.split("\n");

		return retVal;
	}
}
