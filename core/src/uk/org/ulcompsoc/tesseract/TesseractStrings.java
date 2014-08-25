package uk.org.ulcompsoc.tesseract;

import uk.org.ulcompsoc.tesseract.battle.BattleMessage;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TesseractStrings {
	private static BattleMessage	chooseTarget	= new BattleMessage("Choose a target!", 0.75f);

	public static BattleMessage getChooseTargetMessage() {
		return chooseTarget;
	}

	private static float	killedMessageDuration	= 1.5f;
	private static String	wasKilled				= "\nwas killed!";

	public static BattleMessage getKilledMessage(String victim) {
		BattleMessage killedMessage = new BattleMessage(victim + wasKilled, killedMessageDuration);
		return killedMessage;
	}

	private static BattleMessage	defendMessage	= new BattleMessage("You brace yourself against attacks!", 2.5f);

	public static BattleMessage getDefendMessage() {
		return defendMessage;
	}

	private static BattleMessage	quaffMessage	= new BattleMessage("You quaff a delicious potion!", 2.5f);

	public static BattleMessage getQuaffMessage() {
		return quaffMessage;
	}

	private static BattleMessage	fleeMessage	= new BattleMessage(
														"You attempt to flee!\nOh, it's just going to chase you...",
														2.5f);

	public static BattleMessage getFleeMessage() {
		return fleeMessage;
	}

	public static final float		VICTORY_MESSAGE_TIME	= 2.0f;
	private static BattleMessage	victoryMessage1			= new BattleMessage("You are victorious!",
																	VICTORY_MESSAGE_TIME);
	private static BattleMessage	victoryMessage2			= new BattleMessage("A staggering victory!",
																	VICTORY_MESSAGE_TIME);
	private static BattleMessage	victoryMessage3			= new BattleMessage(
																	"You can almost hear\na victory trumpet playing!",
																	VICTORY_MESSAGE_TIME);

	public static BattleMessage getVictoryMessage() {
		final double rand = Math.random();

		if (rand < 0.1) {
			return victoryMessage3;
		} else if (rand < 0.55) {
			return victoryMessage2;
		} else {
			return victoryMessage1;
		}
	}

	private static BattleMessage	attackNotReady	= new BattleMessage("Not ready yet!", 0.5f);

	public static BattleMessage getAttackNotReadyMessage() {
		return attackNotReady;
	}
}
