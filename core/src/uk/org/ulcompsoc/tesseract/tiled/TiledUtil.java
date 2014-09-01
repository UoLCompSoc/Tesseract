package uk.org.ulcompsoc.tesseract.tiled;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.SerializationException;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TiledUtil {
	public static int getMapWidthInTiles(TiledMap map) {
		return ((TiledMapTileLayer) map.getLayers().get(0)).getWidth();
	}

	public static int getMapHeightInTiles(TiledMap map) {
		return ((TiledMapTileLayer) map.getLayers().get(0)).getHeight();
	}

	/**
	 * <p>
	 * Finds the first non-null cell in a layer.
	 * </p>
	 * <p>
	 * Cells are searched across x before y is incremented.
	 * </p>
	 * 
	 * @param layer
	 *        The layer whose first cell we'll find.
	 * @return An initialised {@link Vector2} with the x and y coordinates (in
	 *         tile coordinates), or null if the layer is empty.
	 */
	public static GridPoint2 findFirstCell(TiledMapTileLayer layer) {
		for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				if (layer.getCell(x, y) != null) {
					GridPoint2 retVal = new GridPoint2(x, y);

					return retVal;
				}
			}
		}

		return null;
	}

	public static boolean isSolidLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("solid", null, String.class) != null);
	}

	public static boolean isZLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("z", null, String.class) != null);
	}

	public static boolean isMonsterLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("monsters", null, String.class) != null);
	}

	public static boolean isTorchLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("torch", null, String.class) != null);
	}

	public static boolean isNPCLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("npc", null, String.class) != null);
	}

	public static boolean isHiddenLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("hidden", null, String.class) != null);
	}

	public static boolean isPlayerLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("playerspawn", null, String.class) != null);
	}

	/**
	 * <p>
	 * A layer is visible if it doesn't have some property which makes it
	 * invisible. JSON layers default to being invisible.
	 * </p>
	 * 
	 * @param layer
	 *        the layer whose visibility is being checked.
	 * @return true if the layer is visible, false otherwise.
	 */
	public static boolean isVisibleLayer(TiledMapTileLayer layer) {
		return (!isHiddenLayer(layer) && !isNPCLayer(layer) && !isBossLayer(layer) && !isJSONLayer(layer));
	}

	public static boolean isBossLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("boss", null, String.class) != null);
	}

	public static boolean isDoorLayer(TiledMapTileLayer layer) {
		return (layer.getProperties().get("door", null, String.class) != null);
	}

	/**
	 * <p>
	 * Checks if the given layer is a JSON layer, but performs no validation on
	 * the file nor checks to see if it even exists.
	 * </p>
	 * 
	 * @param layer
	 *        the layer to check
	 * @return true if the layer is a JSON layer, false otherwise.
	 */
	public static boolean isJSONLayer(TiledMapTileLayer layer) {
		return layer.getProperties().get("json", null, String.class) != null;
	}

	/**
	 * <p>
	 * Gets an array containing all of the JSON layers in the given map. No
	 * validation takes place.
	 * </p>
	 * 
	 * @param map
	 *        the map whose JSON layers are to be found.
	 * @return an array, possibly with length == 0, of JSON layers.
	 */
	public static List<TiledMapTileLayer> getJSONLayers(TiledMap map) {
		List<TiledMapTileLayer> jsonLayers = new ArrayList<TiledMapTileLayer>();
		MapLayers layers = map.getLayers();

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (isJSONLayer(layer)) {
				jsonLayers.add(layer);
			}
		}

		return jsonLayers;
	}

	/**
	 * @param layer
	 *        a JSON layer
	 * @return the json file in the JSON layer's properties or null if no such
	 *         property exists (i.e. the layer is invalid)
	 */
	public static String getJSONFile(TiledMapTileLayer layer) {
		return layer.getProperties().get("json", null, String.class);
	}

	/**
	 * <p>
	 * Validates the given JSON layer; that is, checks to make sure that the
	 * layer's "json" property points to an existing JSON file and then
	 * validates that JSON file.
	 * </p>
	 * 
	 * <p>
	 * Note that this validation requires LibGDX to be active, and so this
	 * function can only be used in/after the create() function of the
	 * ApplicationListener.
	 * </p>
	 * 
	 * @param layer
	 *        the layer to validate
	 * @return true if the layer is valid, false otherwise.
	 */
	public static boolean isValidJSONLayer(TiledMapTileLayer layer) {
		final String fileName = layer.getProperties().get("json", String.class);
		final FileHandle fh = Gdx.files.internal(fileName);

		if (!fh.exists()) {
			Gdx.app.debug("IS_VALID_JSON_LAYER", "JSON file: " + fileName + " could not be located.");
			return false;
		}

		try {
			new JsonReader().parse(fh);
		} catch (SerializationException se) {
			Gdx.app.debug("IS_VALID_JSON_LAYER", "JSON file: " + fileName
					+ " is not formatted as valid JSON, parse exception: \n" + se);
			return false;
		}

		return true;
	}

	/**
	 * <p>
	 * A Tesseract map is valid if it has a the following map properties:
	 * <ul>
	 * <li>"textPrefix" - The location of the dialogue texts for interactible
	 * entities in the map.</li>
	 * <li>"mapR" - The red component of the map's colour.</li>
	 * <li>"mapG" - The green component of the map's colour.</li>
	 * <li>"mapB" - The blue component of the map's colour.</li>
	 * <li>"mapA" - The alpha component of the map's colour.</li>
	 * </ul>
	 * 
	 * and if all "JSON layers" (layers which have an associated JSON file with
	 * further information about them) point to valid JSON files which
	 * themselves validate as correct JSON.
	 * </p>
	 * 
	 * @param map
	 *        The map whose validity is being checked.
	 * @return true if the map is valid.
	 */
	public static boolean isValidTesseractMap(TiledMap map) {
		if (!(getMapTextPrefix(map) != null && getMapColor(map) != null)) {
			return false;
		}

		List<TiledMapTileLayer> jsonLayers = TiledUtil.getJSONLayers(map);

		for (TiledMapTileLayer layer : jsonLayers) {
			if (!TiledUtil.isValidJSONLayer(layer)) {
				return false;
			}
		}

		return true;
	}

	public static String getMapTextPrefix(TiledMap map) {
		return map.getProperties().get("textPrefix", null, String.class);
	}

	/**
	 * <p>
	 * Parses the map's properties to retrieve the map colour.
	 * </p>
	 * 
	 * @param map
	 *        The map whose properties are to be parsed.
	 * @return the map's colour if the properties exist and are parseable as
	 *         floats, or null otherwise.
	 */
	public static Color getMapColor(TiledMap map) {
		final MapProperties props = map.getProperties();

		final String rS = props.get("mapR", null, String.class);
		final String gS = props.get("mapG", null, String.class);
		final String bS = props.get("mapB", null, String.class);
		final String aS = props.get("mapA", null, String.class);

		if (rS == null || gS == null || bS == null || aS == null) {
			Gdx.app.debug("GET_MAP_COLOR", "Missing colour component for map.");
			return null;
		} else {
			try {
				final float r = Float.parseFloat(rS) / 255.0f;
				final float g = Float.parseFloat(gS) / 255.0f;
				final float b = Float.parseFloat(bS) / 255.0f;
				final float a = Float.parseFloat(aS) / 255.0f;

				Color ret = new Color(r, g, b, a);
				return ret;
			} catch (NumberFormatException nfe) {
				Gdx.app.debug("GET_MAP_COLOUR", "Map contains ill-formed colour component, trace:\n" + nfe);
				return null;
			}

		}
	}
}
