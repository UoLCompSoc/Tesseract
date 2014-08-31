package uk.org.ulcompsoc.tesseract.fonts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class HtmlFontResolver implements FontResolver {
	private String	base	= null;

	public HtmlFontResolver(String fontPath, String fontName) {
		this.base = fontPath + fontName;
	}

	@Override
	public BitmapFont resolve(int size) {
		String fontFile = base + size + ".fnt";
		String pngFile = base + size + ".png";

		return new BitmapFont(Gdx.files.internal(fontFile), Gdx.files.internal(pngFile), false);
	}

	@Override
	public void dispose() {
	}
}
