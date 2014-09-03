package uk.org.ulcompsoc.tesseract.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class UIPopup {
	public final Texture	texture;
	public final Rectangle	innerRectangle;

	public UIPopup(Texture texture, Rectangle innerRectangle) {
		this.texture = texture;
		this.innerRectangle = innerRectangle;
	}
}
