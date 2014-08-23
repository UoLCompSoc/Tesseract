package uk.org.ulcompsoc.desktop;

import uk.org.ulcompsoc.tesseract.TesseractMain;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640 + 32;
		config.height = 640 + 32;

		boolean debug = false;

		for (String s : args) {
			if ("--debug".equals(s)) {
				debug = true;
			}
		}
		new LwjglApplication(new TesseractMain(debug), config);
	}
}
