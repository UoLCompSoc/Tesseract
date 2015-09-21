package uk.org.ulcompsoc.tesseract.tiled;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.animations.AnimationJSONParser;
import uk.org.ulcompsoc.tesseract.components.Dialogue;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.Interactible;
import uk.org.ulcompsoc.tesseract.components.NPC;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.dialoguelisteners.DialogueFinishListener;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TesseractMap implements Disposable {
	public final TiledMap map;

	public final boolean[] collisionArray;
	public final boolean[] monsterTiles;

	public final Entity[] interactibleTiles;
	public final Entity[] NPCs;
	public final Entity[] jsonEntites;

	public final Entity baseLayerEntity;
	public final Entity zLayerEntity;
	public final Entity bossEntity;
	public final Entity doorEntity;
	public final Entity openDoorEntity;
	public final TiledMapRenderer renderer;

	public final String mapJson;
	public final Color color;
	public final Color uiColor;

	public final int widthInTiles;
	public final int heightInTiles;

	public final List<Texture> ownedTextures;

	private final List<GridPoint2> bossSolids = new ArrayList<GridPoint2>();
	private final List<GridPoint2> doorSolids = new ArrayList<GridPoint2>();
	public boolean bossBeaten = false;
	public boolean doorOpen = false;

	/**
	 * <p>
	 * Loads a new TesseractMap from TMX and JSON files. The TMX file is
	 * resolved to be:<br />
	 * path + mapName + "/" + mapName + ".tmx"<br />
	 * and the json file is resolved to be:<br />
	 * path + mapName + "/" + mapName + ".json"
	 * </p>
	 * 
	 * @param path
	 *            The path to the folder containing the TMX and JSON files.
	 * @param mapName
	 *            The name of the map.
	 * @param batch
	 *            A sprite batch to use when rendering the map.
	 * @param openDoorSprite
	 *            temp hack for LD
	 * @param closedDoorSprite
	 *            temp hack for LD
	 * @param healListener
	 *            temp hack for LD
	 * @param bossListener
	 *            temp hack for LD
	 * @param doorOpenListener
	 *            temp hack for LD
	 */
	public TesseractMap(String path, String mapName, Batch batch, TextureRegion openDoorSprite,
	        TextureRegion closedDoorSprite, DialogueFinishListener healListener, DialogueFinishListener bossListener,
	        DialogueFinishListener doorOpenListener, Animation bossAnimation) {
		final String tmxFileLocation = path + mapName + "/" + mapName + ".tmx";
		final String jsonFileLocation = path + mapName + "/" + mapName + ".json";
		this.map = new TmxMapLoader().load(tmxFileLocation);
		this.mapJson = Gdx.files.internal(jsonFileLocation).readString();

		if (!TiledUtil.isValidTesseractMap(map, mapJson)) {
			Gdx.app.debug("INVALID_MAP", "Map is in an invalid format, exiting.");
			throw new GdxRuntimeException("Invalid file: " + mapName);
		}

		this.color = TiledUtil.getMapColor(mapJson);
		this.uiColor = TiledUtil.getUIColor(mapJson);

		this.ownedTextures = new ArrayList<Texture>();

		this.widthInTiles = TiledUtil.getMapWidthInTiles(map);
		this.heightInTiles = TiledUtil.getMapHeightInTiles(map);

		this.interactibleTiles = new Entity[widthInTiles * heightInTiles];

		this.renderer = new OrthogonalTiledMapRenderer(map, batch);

		this.collisionArray = generateCollisionArray(map);
		this.jsonEntites = generateEntitiesFromJSON(map, ownedTextures);
		this.NPCs = generateNPCEntities(map, mapJson, healListener);
		this.baseLayerEntity = generateBaseLayerEntity(map, renderer);
		this.zLayerEntity = generateZLayerEntity(map, renderer);
		this.monsterTiles = generateMonsterTiles(map);

		this.bossEntity = generateBossEntity(map, mapJson, bossListener, bossAnimation);
		setBossSolids();

		this.doorEntity = generateDoorEntity(map, closedDoorSprite);
		this.openDoorEntity = generateOpenDoorEntity(map, openDoorSprite, doorOpenListener);
		setDoorSolids();

		parseInteractible();
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

	public void setBossSolids() {
		if (bossEntity != null) {
			if(Mappers.renderable.get(bossEntity) == null) {
				Gdx.app.error("NO_BOSS_RENDERABLE", "Couldn't find renderable for boss.");
			}
			
			final int width = (int) Mappers.renderable.get(bossEntity).getRenderableWidth() / WorldConstants.TILE_WIDTH;
			final GridPoint2 bossPos = Mappers.position.get(bossEntity).getGridPosition();

			for (int i = 0; i < width; i++) {
				collisionArray[bossPos.y * widthInTiles + bossPos.x + i] = true;

				GridPoint2 thisPos = new GridPoint2(bossPos.x + i, bossPos.y);
				bossSolids.add(thisPos);
			}
		}
	}

	public void setBossBeaten() {
		this.bossBeaten = true;

		for (final GridPoint2 point : bossSolids) {
			collisionArray[point.y * widthInTiles + point.x] = false;
			setInteractibleAt(null, point);
		}
	}
	
	public void setDoorSolids() {
		if(doorEntity != null) {
			if(Mappers.renderable.get(doorEntity) == null) {
				Gdx.app.error("NO_DOOR_RENDERABLE", "Couldn't find renderable for door.");
			}
			
			final int width = (int) Mappers.renderable.get(doorEntity).getRenderableWidth() / WorldConstants.TILE_WIDTH;
			final GridPoint2 doorPos = Mappers.position.get(doorEntity).getGridPosition();
			
			for(int i = 0; i < width; ++i) {
				collisionArray[doorPos.y * widthInTiles + doorPos.x + i] = true;
				
				final GridPoint2 thisPos = new GridPoint2(doorPos.x + i, doorPos.y);
				doorSolids.add(thisPos);
			}
		}
	}
	
	public void setDoorOpen() {
		doorOpen = true;
		parseInteractible();
	}

	public void setInteractibleAt(final Entity entity, final GridPoint2 pos) {
		setInteractibleAt(entity, pos.x, pos.y);
	}

	public void setInteractibleAt(final Entity entity, int xGrid, int yGrid) {
		final int arrPos = widthInTiles * yGrid + xGrid;

		if (arrPos < 0 || arrPos > interactibleTiles.length) {
			return;
		} else {
			interactibleTiles[arrPos] = entity;
		}
	}

	public Entity getInteractibleAt(int gridX, int gridY) {
		final int arrPos = widthInTiles * gridY + gridX;

		if (arrPos < 0 || arrPos > interactibleTiles.length) {
			return null;
		} else {
			return interactibleTiles[arrPos];
		}

	}

	public Entity getInteractibleAt(final GridPoint2 pos) {
		return getInteractibleAt(pos.x, pos.y);
	}

	public void parseInteractible() {
		for (final Entity entity : NPCs) {
			final GridPoint2 gridPos = Mappers.position.get(entity).getGridPosition();

			setInteractibleAt(entity, gridPos);
		}

		if (bossEntity != null) {
			for (final GridPoint2 bossPos : bossSolids) {
				setInteractibleAt(bossEntity, bossPos);
			}
		}

		if (doorEntity != null) {
			for (final GridPoint2 doorPos : doorSolids) {
				Gdx.app.debug("DOOR_SOLID", "Door solid at (x, y) = (" + doorPos.x + ", " + doorPos.y + ").");
				if(doorOpen) {
					setInteractibleAt(openDoorEntity, doorPos);
				} else {					
					setInteractibleAt(doorEntity, doorPos);
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (ownedTextures != null) {
			for (Texture t : ownedTextures) {
				if (t != null) {
					t.dispose();
				}
			}
		}

		if (map != null) {
			map.dispose();
		}
	}

	public static boolean[] generateCollisionArray(final TiledMap map) {
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

	public static Entity[] generateEntitiesFromJSON(final TiledMap map, final List<Texture> ownedTextures) {
		final List<TiledMapTileLayer> layers = TiledUtil.getJSONLayers(map);
		final List<Entity> entities = new ArrayList<Entity>();
		final JsonReader reader = new JsonReader();

		for (TiledMapTileLayer layer : layers) {
			if (TiledUtil.isJSONLayer(layer)) {
				final String jsonString = Gdx.files.internal(TiledUtil.getJSONFile(layer)).readString();
				JsonValue val = reader.parse(jsonString);

				if (val.child.name.equals("animation")) {
					for (int y = 0; y < layer.getHeight(); y++) {
						for (int x = 0; x < layer.getWidth(); x++) {
							if (layer.getCell(x, y) != null) {
								Entity e = new Entity();
								e.add(new Position().setFromGrid(x, y));
								e.add(AnimationJSONParser.parseAnimation(jsonString, ownedTextures));
								entities.add(e);
							}
						}
					}
				}
			}
		}

		Entity[] retVal = new Entity[entities.size()];
		return entities.toArray(retVal);
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

	public static Entity[] generateNPCEntities(TiledMap map, String mapJson, DialogueFinishListener listener) {
		List<Entity> npcs = new ArrayList<Entity>();
		MapLayers layers = map.getLayers();
		String mapTextPrefix = TiledUtil.getMapTextPrefix(mapJson);

		for (MapLayer layer_ : layers) {
			TiledMapTileLayer layer = (TiledMapTileLayer) layer_;

			if (TiledUtil.isNPCLayer(layer)) {
				final Entity e = new Entity();
				String prop = layer.getName() + ".txt";

				prop = mapTextPrefix + prop;

				GridPoint2 pos = TiledUtil.findFirstCell(layer);

				e.add(new Position().setFromGrid(pos));
				e.add(new Renderable(layer.getCell(pos.x, pos.y).getTile().getTextureRegion()));
				e.add(new NPC());
				e.add(new Interactible());

				final FileHandle fh = Gdx.files.internal(prop);

				if (fh.exists()) {
					String fileContents = fh.readString();
					String lines[] = Dialogue.parseDialogueLines(fileContents);

					Dialogue dia = new Dialogue(lines);

					if (layer.getName().equals("npc3")) {
						// TODO: Fix dirty hack.
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

	public static Entity generateBossEntity(TiledMap map, String mapJson, DialogueFinishListener dfl, Animation bossAnimation) {
		final Entity boss = new Entity();

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
		        TiledUtil.getMapTextPrefix(mapJson) + "boss_start.txt").readString());

		Dialogue diaComponent = new Dialogue(dia).addFinishListener(dfl);

		boss.add(diaComponent);
		
		final Renderable renderable = new Renderable(bossAnimation);
		boss.add(renderable);

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
		Dialogue diaComponent = new Dialogue(doorDia);
		e.add(diaComponent);
		e.add(new Renderable(closedDoorSprite).setPrioritity(75));

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
		Dialogue diaComponent = new Dialogue(doorDia).addFinishListener(dfl);

		e.add(diaComponent);
		e.add(new Renderable(openDoorSprite).setPrioritity(75));

		return e;
	}
}
