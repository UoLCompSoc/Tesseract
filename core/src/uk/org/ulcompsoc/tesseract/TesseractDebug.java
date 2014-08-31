package uk.org.ulcompsoc.tesseract;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

/**
 * <p>
 * Helper class containing static functions to assist debugging.
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class TesseractDebug {
	public static void debugCameraPosition(Camera camera) {
		Gdx.app.debug("CAMERA_POS", "Camera (x, y, z) = (" + camera.position.x + ", " + camera.position.y + ", "
				+ camera.position.z + ").");
	}
}
