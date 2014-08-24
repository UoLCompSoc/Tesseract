package uk.org.ulcompsoc.tesseract.tiled;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

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

	public static boolean isVisibleLayer(TiledMapTileLayer layer) {
		return (!isHiddenLayer(layer) && !isNPCLayer(layer));
	}

	public static boolean isValidTesseractMap(TiledMap map) {
		return (getMapTextPrefix(map) != null);
	}

	public static String getMapTextPrefix(TiledMap map) {
		return map.getProperties().get("textPrefix", null, String.class);
	}
}
