package uk.org.ulcompsoc.tesseract.input;

import uk.org.ulcompsoc.tesseract.components.Combatant;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class ThinkTimePredicate implements InputActivationPredicate {
	private final Combatant	linkedCombatant;

	public ThinkTimePredicate(Combatant linkedCombatant) {
		this.linkedCombatant = linkedCombatant;
	}

	@Override
	public boolean isActive() {
		return linkedCombatant.canAct();
	}

}
