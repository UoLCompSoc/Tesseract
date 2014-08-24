package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Dialogue extends Component {
	public String[]	dialogueLines	= null;

	public Dialogue(String[] dialogueLines) {
		this.dialogueLines = dialogueLines;
	}

	public static String[] parseDialogueLines(String raw) {
		String[] retVal = raw.split("\n");

		return retVal;
	}
}
