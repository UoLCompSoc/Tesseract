package uk.org.ulcompsoc.tesseract.tiled;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TesseractMap implements Disposable {
	public final TiledMap			map;
	public final boolean[]			collisionArray;
	public final Entity[]			torches;
	public final Entity				baseLayerEntity;
	public final Entity				zLayerEntity;
	public final TiledMapRenderer	renderer;

	final int						widthInTiles;
	final int						heightInTiles;

	public TesseractMap(TiledMap map, Batch batch, Animation torchAnim) {
		this.map = map;

		widthInTiles = TiledUtil.getMapWidthInTiles(map);
		heightInTiles = TiledUtil.getMapHeightInTiles(map);

		this.renderer = new OrthogonalTiledMapRenderer(map, batch);

		this.collisionArray = generateCollisionArray(map);
		this.torches = generateTorchEntities(map, torchAnim);
		this.baseLayerEntity = generateBaseLayerEntity(map, renderer);
		this.zLayerEntity = generateZLayerEntity(map, renderer);
	}

	public boolean isTileSolid(int x, int y) {
		return collisionArray[y * widthInTiles + x];
	}

	public boolean isTileSolid(GridPoint2 point) {
		return isTileSolid(point.x, point.y);
	}

	public boolean isCoordSolid(float x, float y) {
		return isTileSolid((int) x / WorldConstants.TILE_WIDTH, (int) y / WorldConstants.TILE_HEIGHT);
	}

	public boolean isCoordSolid(Vector2 vec) {
		return isCoordSolid(vec.x, vec.y);
	}

	public Position findPlayerPosition() {
		Position retVal = null;
		MapLayers layers = map.getLayers();

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isPlayerLayer(layer)) {
				retVal = new Position().setFromGrid(TiledUtil.findFirstCell(layer));
			}
		}

		return retVal;
	}

	public static boolean[] generateCollisionArray(TiledMap map) {
		boolean[] retVal = null;
		MapLayers layers = map.getLayers();

		final int width = TiledUtil.getMapWidthInTiles(map);
		final int height = TiledUtil.getMapHeightInTiles(map);
		retVal = new boolean[width * height];

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isSolidLayer(layer)) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (layer.getCell(x, y) != null) {
							retVal[(y * width) + x] = true;
							Gdx.app.debug("FOUND_SOLID", "Found solid at (x,y) = (" + x + ", " + y + ").");
						}
					}
				}
			}
		}

		return retVal;
	}

	public static Entity[] generateTorchEntities(TiledMap map, Animation torchAnim) {
		Entity[] retVal = null;

		List<GridPoint2> torchPos = new ArrayList<GridPoint2>();

		final int width = TiledUtil.getMapWidthInTiles(map);
		final int height = TiledUtil.getMapHeightInTiles(map);

		for (MapLayer layer_ : map.getLayers()) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isTorchLayer(layer)) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (layer.getCell(x, y) != null) {
							torchPos.add(new GridPoint2(x, y));
						}
					}
				}
			}
		}

		Gdx.app.debug("LOAD_TORCHES", "Found " + (torchPos.size() == 0 ? "no" : "" + torchPos.size())
				+ " torch(es) in map.");

		if (torchPos.size() == 0) {
			return null;
		}

		retVal = new Entity[torchPos.size()];
		for (int i = 0; i < torchPos.size(); i++) {
			GridPoint2 point = torchPos.get(i);

			Entity e = new Entity();
			Renderable r = new Renderable(torchAnim).setPrioritity(25);

			e.add(new Position().setFromGrid(point)).add(r);
			retVal[i] = e;
		}

		return retVal;
	}

	public Entity generateBaseLayerEntity(TiledMap map, TiledMapRenderer renderer) {
		Entity e = new Entity();

		List<TiledMapTileLayer> baseLayers = new ArrayList<TiledMapTileLayer>();

		for (MapLayer layer_ : map.getLayers()) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (!TiledUtil.isHiddenLayer(layer) && !TiledUtil.isZLayer(layer)) {
				baseLayers.add(layer);
			}
		}

		TiledMapTileLayer[] layerArray = baseLayers.toArray(new TiledMapTileLayer[baseLayers.size()]);

		e.add(new Position(0, 0));
		e.add(new Renderable(renderer, layerArray).setPrioritity(0));

		return e;
	}

	public Entity generateZLayerEntity(TiledMap map, TiledMapRenderer renderer) {
		Entity e = new Entity();

		List<TiledMapTileLayer> zLayers = new ArrayList<TiledMapTileLayer>();

		for (MapLayer layer_ : map.getLayers()) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isZLayer(layer)) {
				zLayers.add(layer);
			}
		}

		TiledMapTileLayer[] layerArray = zLayers.toArray(new TiledMapTileLayer[zLayers.size()]);
		e.add(new Position(0, 0));
		e.add(new Renderable(renderer, layerArray).setPrioritity(1000));

		return e;
	}

	@Override
	public void dispose() {
		if (map != null) {
			map.dispose();
		}
	}
}
