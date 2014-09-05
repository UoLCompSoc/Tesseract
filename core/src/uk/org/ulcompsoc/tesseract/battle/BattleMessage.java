package uk.org.ulcompsoc.tesseract.battle;


/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleMessage {
	public String	message;
	public float	time;

	public BattleMessage(String message) {
		this.message = message;
		this.time = guessTime(message);
	}

	public BattleMessage(String message, float time) {
		this.message = message;
		this.time = time;
	}

	public static float guessTime(String message) {
		return 0.05f * (float) message.length();
	}
}
