package uk.org.ulcompsoc.client;

import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.fonts.HtmlFontResolver;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class HtmlLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(1280, 768);
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return new TesseractMain(new HtmlFontResolver("fonts/", "robotobm"));
	}
}