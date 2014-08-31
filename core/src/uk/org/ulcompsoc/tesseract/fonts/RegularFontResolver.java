package uk.org.ulcompsoc.tesseract.fonts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class RegularFontResolver implements FontResolver {
	private FreeTypeFontGenerator	fontGenerator	= null;
	private FreeTypeFontParameter	para			= null;

	public RegularFontResolver() {
		// can't init here because needs to be passed to Main class which inits
		// LibGdx; init here will cause NullPtrException
	}

	@Override
	public BitmapFont resolve(int size) {
		if (fontGenerator == null) {
			fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoRegular.ttf"));
		}

		if (para == null) {
			para = new FreeTypeFontParameter();
		}

		para.size = size;
		return fontGenerator.generateFont(para);
	}

	@Override
	public void dispose() {
		if (fontGenerator != null) {
			fontGenerator.dispose();
			para = null;
		}
	}
}
