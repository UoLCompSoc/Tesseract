package uk.org.ulcompsoc.tesseract.fonts;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

/**
 * <p>
 * Implements a simple font resolving interface; needed because HTML5 deployment
 * isn't supported by gdx-freetype.
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public interface FontResolver extends Disposable {
	public BitmapFont resolve(int size);
}
