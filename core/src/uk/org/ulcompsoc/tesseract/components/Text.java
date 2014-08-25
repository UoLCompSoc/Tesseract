package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Text extends Component {
	public String	baseText	= "foo";
	public String	val			= "";
	public Color	color		= Color.WHITE;

	public Text(String text) {
		this(text, Color.WHITE, null);
	}

	public Text(String text, Color color) {
		this(text, color, null);
	}

	public Text(String text, Color color, Signal<Stats> signal) {
		this.baseText = text;
		this.color = color;

		if (signal != null) {
			signal.add(new Listener<Stats>() {
				@Override
				public void receive(Signal<Stats> signal, Stats object) {
					val = object.getHP() + "/" + object.maxHP;
				}
			});
		}
	}

	public String getText() {
		return baseText + val;
	}

	public static float getTextWidth(Text text, BitmapFont font) {
		TextBounds bounds = font.getMultiLineBounds(text.getText());
		return bounds.width;
	}

	public static float getTextHeight(Text text, BitmapFont font) {
		TextBounds bounds = font.getMultiLineBounds(text.getText());
		return bounds.height;
	}

	public static Rectangle getTextRectangle(float x, float y, Text text, BitmapFont font) {
		return getTextRectangle(x, y, text.getText(), font);
	}

	public static Rectangle getTextRectangle(Text text, BitmapFont font) {
		return getTextRectangle(0.0f, 0.0f, text.getText(), font);
	}

	public static Rectangle getTextRectangle(String str, BitmapFont font) {
		return getTextRectangle(0.0f, 0.0f, str, font);
	}

	public static Rectangle getTextRectangle(float x, float y, String str, BitmapFont font) {
		TextBounds bounds = font.getMultiLineBounds(str);

		return new Rectangle(x, y, bounds.width, bounds.height);
	}
}
