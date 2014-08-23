package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Text extends Component {
	public String	text	= "foo";
	public Color	color	= Color.WHITE;

	public Text(String text) {
		this(text, Color.WHITE);
	}

	public Text(String text, Color color) {
		this.text = text;
		this.color = color;
	}

	public static float getTextWidth(Text text, BitmapFont font) {
		TextBounds bounds = font.getMultiLineBounds(text.text);
		return bounds.width;
	}

	public static float getTextHeight(Text text, BitmapFont font) {
		TextBounds bounds = font.getMultiLineBounds(text.text);
		return bounds.height;
	}

	public static Rectangle getTextRectangle(float x, float y, Text text, BitmapFont font) {
		TextBounds bounds = font.getMultiLineBounds(text.text);

		return new Rectangle(x, y, bounds.width, bounds.height);
	}

	public static Rectangle getTextRectangle(Text text, BitmapFont font) {
		return getTextRectangle(0.0f, 0.0f, text, font);
	}
}
