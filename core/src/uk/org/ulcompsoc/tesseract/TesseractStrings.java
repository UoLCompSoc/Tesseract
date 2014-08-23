package uk.org.ulcompsoc.tesseract;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TesseractStrings {
	private static BattleMessage	chooseTarget	= new BattleMessage("Choose a target!", 1.5f);

	public static BattleMessage getChooseTargetMessage() {
		return chooseTarget;
	}

	private static BattleMessage	killed	= new BattleMessage(" was killed!", 3.0f);

	public static BattleMessage getKilledMessage(String victim) {
		killed.message = victim + killed.message;
		return killed;
	}

	private static BattleMessage	jumpMessage	= new BattleMessage(
														"You jump into the air!\n...what did you think that would do?",
														2.5f);

	public static BattleMessage getJumpMessage() {
		return jumpMessage;
	}

	private static BattleMessage	fleeMessage	= new BattleMessage(
														"You attempt to flee!\nOh, it's just going to chase you...",
														2.5f);

	public static BattleMessage getFleeMessage() {
		return fleeMessage;
	}
}
