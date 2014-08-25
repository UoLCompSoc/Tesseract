package uk.org.ulcompsoc.tesseract.tiled;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Dialogue;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
	public final boolean[]			monsterTiles;
	public final Entity[]			NPCs;
	public final Entity[]			torches;
	public final Entity				baseLayerEntity;
	public final Entity				zLayerEntity;
	public final TiledMapRenderer	renderer;

	final int						widthInTiles;
	final int						heightInTiles;

	public TesseractMap(TiledMap map, Batch batch, Animation torchAnim) {
		this.map = map;

		if (!TiledUtil.isValidTesseractMap(map)) {
			Gdx.app.debug("INVALID_MAP", "Map contains no text prefix, exiting.");
		}

		widthInTiles = TiledUtil.getMapWidthInTiles(map);
		heightInTiles = TiledUtil.getMapHeightInTiles(map);

		this.renderer = new OrthogonalTiledMapRenderer(map, batch);

		this.collisionArray = generateCollisionArray(map);
		this.torches = generateTorchEntities(map, torchAnim);
		this.NPCs = generateNPCEntities(map);
		this.baseLayerEntity = generateBaseLayerEntity(map, renderer);
		this.zLayerEntity = generateZLayerEntity(map, renderer);
		this.monsterTiles = generateMonsterTiles(map);
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

	public boolean isMonsterTile(int x, int y) {
		return monsterTiles[y * widthInTiles + x];
	}

	public boolean isMonsterTile(GridPoint2 pos) {
		return isMonsterTile(pos.x, pos.y);
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

	public static Entity generateBaseLayerEntity(TiledMap map, TiledMapRenderer renderer) {
		Entity e = new Entity();

		List<TiledMapTileLayer> baseLayers = new ArrayList<TiledMapTileLayer>();

		for (MapLayer layer_ : map.getLayers()) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isVisibleLayer(layer) && !TiledUtil.isZLayer(layer)) {
				baseLayers.add(layer);
			}
		}

		TiledMapTileLayer[] layerArray = baseLayers.toArray(new TiledMapTileLayer[baseLayers.size()]);

		e.add(new Position(0, 0));
		e.add(new Renderable(renderer, layerArray).setPrioritity(0));

		return e;
	}

	public static Entity generateZLayerEntity(TiledMap map, TiledMapRenderer renderer) {
		Entity e = new Entity();

		List<TiledMapTileLayer> zLayers = new ArrayList<TiledMapTileLayer>();

		for (MapLayer layer_ : map.getLayers()) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isVisibleLayer(layer) && TiledUtil.isZLayer(layer)) {
				zLayers.add(layer);
			}
		}

		TiledMapTileLayer[] layerArray = zLayers.toArray(new TiledMapTileLayer[zLayers.size()]);
		e.add(new Position(0, 0));
		e.add(new Renderable(renderer, layerArray).setPrioritity(1000));

		return e;
	}

	public static Entity[] generateNPCEntities(TiledMap map) {
		List<Entity> npcs = new ArrayList<Entity>();
		MapLayers layers = map.getLayers();
		String mapTextPrefix = TiledUtil.getMapTextPrefix(map);

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isNPCLayer(layer)) {
				String prop = layer.getName() + ".txt";
				Entity e = new Entity();

				prop = mapTextPrefix + prop;

				GridPoint2 pos = TiledUtil.findFirstCell(layer);

				e.add(new Position().setFromGrid(pos));
				e.add(new Renderable(layer.getCell(pos.x, pos.y).getTile().getTextureRegion()));

				FileHandle fh = Gdx.files.internal(prop);

				if (fh.exists()) {
					String fileContents = fh.readString();
					String lines[] = Dialogue.parseDialogueLines(fileContents);
					Gdx.app.debug("PARSE_DIALOGUE",
							"Found " + lines.length + " line(s) of dialogue for " + layer.getName() + ".");

					e.add(new Dialogue(lines));
				} else {
					Gdx.app.debug("LOAD_NPC", "Could not find file " + prop + ".");
					continue;
				}

				// if we got here, entity has been made and is valid.
				npcs.add(e);
			}
		}

		Entity[] ret = npcs.toArray(new Entity[npcs.size()]);
		Gdx.app.debug("LOAD_NPCS", "Loaded " + ret.length + " NPCs.");

		return ret;
	}

	public static boolean[] generateMonsterTiles(TiledMap map) {
		final int width = TiledUtil.getMapWidthInTiles(map);
		final int height = TiledUtil.getMapHeightInTiles(map);

		boolean[] retVal = new boolean[width * height];

		for (MapLayer layer_ : map.getLayers()) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isMonsterLayer(layer)) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (layer.getCell(x, y) != null) {
							retVal[(y * width) + x] = true;
						}
					}
				}
			}
		}

		return retVal;
	}

	@Override
	public void dispose() {
		if (map != null) {
			map.dispose();
		}
	}
}
