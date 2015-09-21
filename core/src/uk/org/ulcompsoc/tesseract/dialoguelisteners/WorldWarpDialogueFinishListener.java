package uk.org.ulcompsoc.tesseract.dialoguelisteners;

import uk.org.ulcompsoc.tesseract.TesseractMain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Signal;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class WorldWarpDialogueFinishListener extends DialogueFinishListener {
	final private int	worldID;

	public WorldWarpDialogueFinishListener(int worldID) {
		this.worldID = worldID;
	}

	@Override
	public void receive(Signal<Entity> signal, Entity object) {
		TesseractMain.flagWorldChange(worldID);
	}
}
