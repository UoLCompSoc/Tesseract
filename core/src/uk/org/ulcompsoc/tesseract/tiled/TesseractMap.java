package uk.org.ulcompsoc.tesseract.tiled;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Dialogue;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.NPC;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.dialoguelisteners.DialogueFinishListener;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	public final Entity				bossEntity;
	public final Entity				doorEntity;
	public final Entity				openDoorEntity;
	public final TiledMapRenderer	renderer;

	public final int				widthInTiles;
	public final int				heightInTiles;

	public boolean					bossBeaten	= false;

	public TesseractMap(TiledMap map, Batch batch, Animation torchAnim, TextureRegion openDoorSprite,
			TextureRegion closedDoorSprite, DialogueFinishListener healListener, DialogueFinishListener bossListener,
			DialogueFinishListener doorOpenListener) {
		this.map = map;

		if (!TiledUtil.isValidTesseractMap(map)) {
			Gdx.app.debug("INVALID_MAP", "Map contains no text prefix, exiting.");
		}

		widthInTiles = TiledUtil.getMapWidthInTiles(map);
		heightInTiles = TiledUtil.getMapHeightInTiles(map);

		this.renderer = new OrthogonalTiledMapRenderer(map, batch);

		this.collisionArray = generateCollisionArray(map);
		this.torches = generateTorchEntities(map, torchAnim);
		this.NPCs = generateNPCEntities(map, healListener);
		this.baseLayerEntity = generateBaseLayerEntity(map, renderer);
		this.zLayerEntity = generateZLayerEntity(map, renderer);
		this.monsterTiles = generateMonsterTiles(map);
		this.bossEntity = generateBossEntity(map, bossListener);

		this.doorEntity = generateDoorEntity(map, closedDoorSprite);
		this.openDoorEntity = generateOpenDoorEntity(map, openDoorSprite, doorOpenListener);
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

	public void setBossBeaten() {
		this.bossBeaten = true;
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

	public static Entity[] generateNPCEntities(TiledMap map, DialogueFinishListener listener) {
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
				e.add(new NPC());

				FileHandle fh = Gdx.files.internal(prop);

				if (fh.exists()) {
					String fileContents = fh.readString();
					String lines[] = Dialogue.parseDialogueLines(fileContents);

					Dialogue dia = new Dialogue(lines);

					if (layer.getName().equals("npc3")) {
						// dirty hack for queen healing
						dia = dia.addFinishListener(listener);
					}

					e.add(dia);
				} else {
					Gdx.app.debug("LOAD_NPC", "Could not find file " + prop + ".");
					continue;
				}

				// if we got here, entity has been made and is valid.
				npcs.add(e);
			}
		}

		Entity[] ret = npcs.toArray(new Entity[npcs.size()]);

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

	public static Entity generateBossEntity(TiledMap map, DialogueFinishListener dfl) {
		Entity boss = new Entity();

		MapLayers layers = map.getLayers();
		GridPoint2 pos = null;
		String name = null;

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isBossLayer(layer)) {
				pos = TiledUtil.findFirstCell(layer);
				name = layer.getProperties().get("boss", String.class);
			}
		}

		if (pos == null || name == null) {
			return null;
		}

		boss.add(new Position().setFromGrid(pos));
		boss.add(new Enemy(name));

		final String[] dia = Dialogue.parseDialogueLines(Gdx.files.internal(
				map.getProperties().get("textPrefix") + "boss_start.txt").readString());
		boss.add(new Dialogue(dia).addFinishListener(dfl));

		return boss;
	}

	public static Entity generateDoorEntity(TiledMap map, TextureRegion closedDoorSprite) {
		Entity e = new Entity();

		MapLayers layers = map.getLayers();
		GridPoint2 pos = null;

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isDoorLayer(layer)) {
				pos = TiledUtil.findFirstCell(layer);
			}
		}

		if (pos == null) {
			return null;
		}
		e.add(new Position().setFromGrid(pos));
		final String[] doorDia = { "Ah, this is the door the wizard mentioned...",
				"It opens when all the bosses are dead, right?" };
		e.add(new Dialogue(doorDia));
		e.add(new Renderable(closedDoorSprite).setPrioritity(0));

		return e;
	}

	public static Entity generateOpenDoorEntity(TiledMap map, TextureRegion openDoorSprite, DialogueFinishListener dfl) {
		Entity e = new Entity();

		MapLayers layers = map.getLayers();
		GridPoint2 pos = null;

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isDoorLayer(layer)) {
				pos = TiledUtil.findFirstCell(layer);
			}
		}

		if (pos == null) {
			return null;
		}
		e.add(new Position().setFromGrid(pos));
		final String[] doorDia = { "This looks... ominous.", "Well, I guess I've come this far..." };
		e.add(new Dialogue(doorDia).addFinishListener(dfl));
		e.add(new Renderable(openDoorSprite).setPrioritity(0));

		return e;
	}

	@Override
	public void dispose() {
		if (map != null) {
			map.dispose();
		}
	}
}
