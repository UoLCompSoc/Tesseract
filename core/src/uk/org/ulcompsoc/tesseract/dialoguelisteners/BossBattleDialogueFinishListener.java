package uk.org.ulcompsoc.tesseract.dialoguelisteners;

import uk.org.ulcompsoc.tesseract.TesseractMain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Signal;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BossBattleDialogueFinishListener extends DialogueFinishListener {
	@Override
	public void receive(Signal<Entity> signal, Entity object) {
		TesseractMain.flagBattleChange(true);
	}
}
