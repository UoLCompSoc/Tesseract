package uk.org.ulcompsoc.desktop;

import uk.org.ulcompsoc.tesseract.Difficulty;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.fonts.RegularFontResolver;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] args) {
		DisplayMode[] modes = LwjglApplicationConfiguration.getDisplayModes();
		boolean resFound = false;

		for (DisplayMode mode : modes) {
			if (mode.width == 1280 && mode.height == 720) {
				System.out.println("1280x720 found.");
				resFound = true;
				break;
			}
		}

		if (!resFound) {
			throw new IllegalStateException("Could not find expected resolution, sorry.");
		}

		boolean debug = false;
		boolean silent = false;

		for (String s : args) {
			if ("--debug".equals(s)) {
				debug = true;
			}

			if ("--silent".equalsIgnoreCase(s) || "--shut-up".equalsIgnoreCase(s)) {
				silent = true;
			}
		}

		Difficulty diff = Difficulty.parseCommandLineArgs(args);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;

		config.useGL30 = false;
		LwjglApplicationConfiguration.disableAudio = silent;

		config.addIcon("icons/icon16.png", FileType.Internal);
		config.addIcon("icons/icon32.png", FileType.Internal);
		config.addIcon("icons/icon128.png", FileType.Internal);

		new LwjglApplication(new TesseractMain(new RegularFontResolver(), diff, debug, silent), config);
	}
}
