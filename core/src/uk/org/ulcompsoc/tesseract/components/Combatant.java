package uk.org.ulcompsoc.tesseract.components;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.battle.BuffPerformer;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Combatant extends Component {
	public boolean				hasHitAnimation			= false;
	public boolean				hasDefendAnimation		= false;
	public boolean				hasHealAnimation		= false;
	public boolean				hasThinkingAnimation	= false;

	public List<BuffPerformer>	buffs					= new ArrayList<BuffPerformer>();

	// changed in buffsystem
	public float				thinkingTime			= -1.0f;

	public boolean canAct() {
		return thinkingTime < -0.5f;
	}
}
