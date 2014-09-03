package uk.org.ulcompsoc.tesseract.tiled;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TiledUtil {
	public static final Color	DEFAULT_UI_COLOR	= Color.NAVY;

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
	 * layer's "json" property points to an existing JSON file, validates that
	 * JSON file, then checks to see if an entry in the "layerData" object of
	 * the file exists for this layer.
	 * </p>
	 * 
	 * @param layer
	 *        the layer to validate
	 * @param jsonFile
	 *        the json file loaded along with the map.
	 * @return true if the layer is valid and is a JSON layer, false otherwise.
	 */
	public static boolean isValidJSONLayer(TiledMapTileLayer layer, String jsonFile) {
		if (!isJSONLayer(layer)) {
			Gdx.app.debug("IS_VALID_JSON_LAYER", "Called with layer that isn't a JSON layer: " + layer.getName());
			return false;
		}

		JsonValue value = null;

		try {
			value = new JsonReader().parse(jsonFile);
		} catch (SerializationException se) {
			Gdx.app.debug("IS_VALID_JSON_LAYER", "JSON file is not formatted as valid JSON, parse exception: \n" + se);
			return false;
		}

		value = value.get("layerData");

		if (value == null) {
			Gdx.app.debug("IS_VALID_JSON_LAYER", "Could not find \"layerData\" in JSON.");
			return false;
		}

		value = value.get(layer.getName());

		if (value == null) {
			Gdx.app.debug("IS_VALID_JSON_LAYER", "JSON \"layerData\" contains no entry for layer " + layer.getName()
					+ ".");
			return false;
		}

		return true;
	}

	/**
	 * <p>
	 * A Tesseract map is valid if it has a the following map properties in
	 * JSON:
	 * <ul>
	 * <li>"textPrefix" - The location of the dialogue texts for interactible
	 * entities in the map.</li>
	 * <li>"color" - The map's colour value, drawn as a background when there is
	 * no tile to draw over it.</li>
	 * </ul>
	 * 
	 * and if all "JSON layers" (layers which have an associated JSON file with
	 * further information about them) point to valid JSON entities in the given
	 * JSON file.
	 * </p>
	 * 
	 * @param map
	 *        The map whose validity is being checked.
	 * @param jsonFile
	 *        a JSON file associated with the given map
	 * @return true if the map is valid.
	 */
	public static boolean isValidTesseractMap(TiledMap map, String jsonFile) {
		if (getMapTextPrefix(jsonFile) == null || getMapColor(jsonFile) == null) {
			return false;
		}

		List<TiledMapTileLayer> jsonLayers = TiledUtil.getJSONLayers(map);

		for (TiledMapTileLayer layer : jsonLayers) {
			if (!TiledUtil.isValidJSONLayer(layer, jsonFile)) {
				return false;
			}
		}

		return true;
	}

	public static String getMapTextPrefix(String jsonFile) {
		JsonValue val = new JsonReader().parse(jsonFile);

		val = val.get("textPrefix");

		return (val != null ? val.asString() : null);
	}

	public static Color getMapColor(String jsonString) {
		JsonReader reader = new JsonReader();
		JsonValue val = reader.parse(jsonString);

		if (val.hasChild("color")) {
			JsonValue child = val.get("color");

			Float r = null;
			Float g = null;
			Float b = null;
			Float a = 1.0f;

			for (JsonValue colourVal : child) {
				try {
					if (colourVal.name.equalsIgnoreCase("r")) {
						r = Float.parseFloat(colourVal.asString());
					} else if (colourVal.name.equalsIgnoreCase("g")) {
						g = Float.parseFloat(colourVal.asString());
					} else if (colourVal.name.equalsIgnoreCase("b")) {
						b = Float.parseFloat(colourVal.asString());
					} else if (colourVal.name.equalsIgnoreCase("a")) {
						a = Float.parseFloat(colourVal.asString());
					}
				} catch (NumberFormatException nfe) {
					Gdx.app.debug("GET_MAP_COLOR", "Ill formed colour value \"" + colourVal.name + "\" in JSON.");
				}
			}

			if (r == null || g == null || b == null) {
				Gdx.app.debug("GET_MAP_COLOR", "Missing mandatory colour component in JSON:\nr found = " + (r != null)
						+ ".\ng found = " + (g != null) + ".\nb found = " + (b != null) + ".");

				return null;
			} else {
				return new Color(r.floatValue(), g.floatValue(), b.floatValue(), a.floatValue());
			}
		}

		Gdx.app.debug("GET_MAP_COLOR", "Couldn't find map \"color\" object in JSON.");
		return null;
	}

	public static Color getUIColor(String jsonString) {
		JsonReader reader = new JsonReader();
		JsonValue val = reader.parse(jsonString);

		if (val.hasChild("uiColor")) {
			JsonValue child = val.get("uiColor");
			// found the colors!

			Float r = null;
			Float g = null;
			Float b = null;
			Float a = 1.0f;

			for (JsonValue colourVal : child) {
				try {
					if (colourVal.name.equalsIgnoreCase("r")) {
						r = Float.parseFloat(colourVal.asString());
					} else if (colourVal.name.equalsIgnoreCase("g")) {
						g = Float.parseFloat(colourVal.asString());
					} else if (colourVal.name.equalsIgnoreCase("b")) {
						b = Float.parseFloat(colourVal.asString());
					} else if (colourVal.name.equalsIgnoreCase("a")) {
						a = Float.parseFloat(colourVal.asString());
					}
				} catch (NumberFormatException nfe) {
					Gdx.app.debug("GET_UI_COLOR", "Ill formed ui colour value \"" + colourVal.name + "\" in JSON.");
				}
			}

			if (r == null || g == null || b == null) {
				Gdx.app.debug("GET_UI_COLOR", "Missing UI colour component in JSON:\nr found = " + (r != null)
						+ ".\ng found = " + (g != null) + ".\nb found = " + (b != null) + ".");

				return DEFAULT_UI_COLOR;
			} else {
				return new Color(r.floatValue(), g.floatValue(), b.floatValue(), a.floatValue());
			}
		}

		Gdx.app.debug("GET_UI_COLOR", "Couldn't find map \"uiColor\" object in JSON.");
		return DEFAULT_UI_COLOR;
	}

	public static float clampFloat(float value, float minimum, float maximum) {
		return Math.max(minimum, Math.max(maximum, value));
	}

	public static float clampColour(float value) {
		return clampFloat(value, 0.0f, 1.0f);
	}
}
