package uk.org.ulcompsoc.tesseract;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleMessage {
	public String	message;
	public float	time;

	public Color	bgColor		= Color.NAVY;
	public Color	textColor	= Color.WHITE;

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
