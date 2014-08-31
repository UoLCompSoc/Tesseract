package uk.org.ulcompsoc.tesseract;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Bits;

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

	public static void debugAllInEngine(Engine engine) {
		debugAllInEngine(engine, false);
	}

	public static void debugAllInEngine(Engine engine, boolean namedOnly) {
		final String tag = "DEBUG_ALL_IN_ENGINE";
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.getFor(new Bits(), new Bits(), new Bits()));

		Gdx.app.debug(tag, "Starting debugging all entities in engine: " + engine.toString() + "\n-----");
		StringBuilder builder = new StringBuilder("\n");

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);

			builder.append("Entity ");

			if (Mappers.named.has(e)) {
				final String name = Mappers.named.get(e).name;
				builder.append(name).append(" has ").append(e.getComponents().size()).append(" entities:\n");
				ImmutableArray<Component> coms = e.getComponents();

				for (int j = 0; j < coms.size(); j++) {
					builder.append("  ").append(j).append(": ").append(coms.get(j)).append(".\n");
				}
			} else {
				if (!namedOnly) {
					builder.append(e.getIndex()).append(" has ").append(e.getComponents().size())
							.append(" entities.\n");
				}
			}
		}

		builder.append("-----");

		Gdx.app.debug(tag, builder.toString());
	}
}
