package uk.org.ulcompsoc.client;

import uk.org.ulcompsoc.tesseract.TesseractMain;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class HtmlLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(640 + 32, 640 + 32);
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return new TesseractMain();
	}
}