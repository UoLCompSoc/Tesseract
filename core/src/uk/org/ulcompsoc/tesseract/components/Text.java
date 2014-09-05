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

		// TODO: Fix dirty hack
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

	public static float getTextWidth(String text, float wrapWidth, BitmapFont font) {
		TextBounds bounds = font.getMultiLineBounds(text);
		return bounds.width;
	}

	public static float getTextHeight(String text, float wrapWidth, BitmapFont font) {
		TextBounds bounds = font.getWrappedBounds(text, wrapWidth);
		return bounds.height;
	}

	public static float getTextWidth(Text text, float wrapWidth, BitmapFont font) {
		return getTextWidth(text.getText(), wrapWidth, font);
	}

	public static float getTextHeight(Text text, float wrapWidth, BitmapFont font) {
		return getTextHeight(text.getText(), wrapWidth, font);
	}

	public static Rectangle getTextRectangle(float x, float y, float wrapWidth, Text text, BitmapFont font) {
		return getTextRectangle(x, y, wrapWidth, text.getText(), font);
	}

	public static Rectangle getTextRectangle(Text text, float wrapWidth, BitmapFont font) {
		return getTextRectangle(0.0f, 0.0f, wrapWidth, text.getText(), font);
	}

	public static Rectangle getTextRectangle(String str, float wrapWidth, BitmapFont font) {
		return getTextRectangle(0.0f, 0.0f, wrapWidth, str, font);
	}

	public static Rectangle getTextRectangle(float x, float y, float wrapWidth, String str, BitmapFont font) {
		TextBounds bounds = font.getWrappedBounds(str, wrapWidth);

		return new Rectangle(x, y, bounds.width, bounds.height);
	}
}
