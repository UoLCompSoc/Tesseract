package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Text extends Component {
	public String	text	= "foo";

	public Text(String text) {
		this.text = text;
	}

	public static float getTextWidth(Text text, BitmapFont font) {
		TextBounds bounds = font.getBounds(text.text);
		return bounds.width;
	}

	public static float getTextHeight(Text text, BitmapFont font) {
		TextBounds bounds = font.getBounds(text.text);
		return bounds.height;
	}

	public static Rectangle getTextRectangle(float x, float y, Text text, BitmapFont font) {
		TextBounds bounds = font.getBounds(text.text);

		return new Rectangle(x, y, bounds.width, bounds.height);
	}
}
