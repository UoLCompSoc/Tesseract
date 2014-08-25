package uk.org.ulcompsoc.tesseract;

import java.util.Random;

import uk.org.ulcompsoc.tesseract.battle.BattlePerformers;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.FocusTaker;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Movable;
import uk.org.ulcompsoc.tesseract.components.Moving;
import uk.org.ulcompsoc.tesseract.components.Named;
import uk.org.ulcompsoc.tesseract.components.Player;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.RelativePosition;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Renderable.Facing;
import uk.org.ulcompsoc.tesseract.components.Stats;
import uk.org.ulcompsoc.tesseract.components.Text;
import uk.org.ulcompsoc.tesseract.components.WorldPlayerInputListener;
import uk.org.ulcompsoc.tesseract.systems.BattleAttackSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleDialogRenderSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleInputSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleMessageSystem;
import uk.org.ulcompsoc.tesseract.systems.DialogueSystem;
import uk.org.ulcompsoc.tesseract.systems.FocusTakingSystem;
import uk.org.ulcompsoc.tesseract.systems.MovementSystem;
import uk.org.ulcompsoc.tesseract.systems.RenderSystem;
import uk.org.ulcompsoc.tesseract.systems.TextRenderSystem;
import uk.org.ulcompsoc.tesseract.systems.WorldPlayerInputSystem;
import uk.org.ulcompsoc.tesseract.tiled.TesseractMap;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TesseractMain extends ApplicationAdapter {
	public static final String		PLAYER_NAME						= "Valiant Hero™";
	public static final float		BATTLE_END_TRANSITION_TIME		= 4.0f;
	public static final float		BATTLE_START_TRANSITION_TIME	= 2.0f;

	private Random					random							= null;

	private SpriteBatch				batch							= null;
	private Camera					camera							= null;

	private ShaderProgram			vortexProgram					= null;

	private BitmapFont				font10							= null;
	private BitmapFont				font12							= null;
	private BitmapFont				font16							= null;
	private BitmapFont				font24							= null;

	private Engine					currentEngine					= null;
	private Engine					battleEngine					= null;
	private Engine					worldEngine						= null;

	public static Entity			battlePlayerEntity				= null;
	public static Entity			worldPlayerEntity				= null;

	BattleVictoryListener			battleVictoryListener			= null;

	private static boolean			battleChangeFlag				= false;
	private static boolean			worldChangeFlag					= false;
	private float					transitionTime					= -1.0f;

	private Texture					playerTexture					= null;
	private TextureRegion[]			playerRegions					= null;

	private Texture					slimeTexture					= null;

	private Texture					torchTexture					= null;
	private Animation				torchAnim						= null;

	private MonsterTileHandler		monsterTileHandler				= null;

	private Entity					statusDialog					= null;
	private Entity[]				menuDialogs						= null;
	private Entity[]				menuTexts						= null;
	private Entity					hpText							= null;
	private Entity					rageText						= null;

	public static final String[]	mapNames						= { "world1/world1.tmx" };
	public static final Color[]		mapColors						= { new Color(80.0f / 255.0f, 172.0f / 255.0f,
																			61.0f / 255.0f, 1.0f) };
	private static TesseractMap[]	maps							= null;
	public static int				currentMapIndex					= 0;

	@SuppressWarnings("unused")
	private GameState				gameState						= null;

	public TesseractMain() {
		this(Difficulty.EASY, false);
	}

	public TesseractMain(Difficulty diff, boolean debug) {
		WorldConstants.DEBUG = debug;
		WorldConstants.DIFFICULTY = diff;
	}

	@Override
	public void create() {
		if (WorldConstants.DEBUG) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}

		ShaderProgram.pedantic = false;

		random = new Random();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		batch = new SpriteBatch();

		loadShader();

		font10 = new BitmapFont(Gdx.files.internal("fonts/robotobm10.fnt"), Gdx.files.internal("fonts/robotobm10.png"),
				false);
		font12 = new BitmapFont(Gdx.files.internal("fonts/robotobm12.fnt"), Gdx.files.internal("fonts/robotobm12.png"),
				false);
		font16 = new BitmapFont(Gdx.files.internal("fonts/robotobm16.fnt"), Gdx.files.internal("fonts/robotobm16.png"),
				false);
		font24 = new BitmapFont(Gdx.files.internal("fonts/robotobm24.fnt"), Gdx.files.internal("fonts/robotobm24.png"),
				false);

		playerTexture = new Texture(Gdx.files.internal("player/basicPlayer.png"));
		playerRegions = TextureRegion.split(playerTexture, WorldConstants.TILE_WIDTH, WorldConstants.TILE_HEIGHT)[0];

		slimeTexture = new Texture(Gdx.files.internal("monsters/greenSlime.png"));
		torchTexture = new Texture(Gdx.files.internal("torches/world1torches.png"));
		TextureRegion[] torchRegions = TextureRegion.split(torchTexture, WorldConstants.TILE_WIDTH,
				WorldConstants.TILE_HEIGHT)[0];
		torchAnim = new Animation(0.15f, torchRegions[0], torchRegions[1], torchRegions[2]);
		torchAnim.setPlayMode(PlayMode.LOOP_PINGPONG);

		monsterTileHandler = new MonsterTileHandler();
		battleVictoryListener = new BattleVictoryListener();

		battleEngine = new Engine();
		worldEngine = new Engine();

		initBattleEngine(battleEngine);
		initWorldEngine(worldEngine);

		changeToWorld();
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClearColor(mapColors[currentMapIndex].r, mapColors[currentMapIndex].g, mapColors[currentMapIndex].b,
				mapColors[currentMapIndex].a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (battleChangeFlag || worldChangeFlag) {
			transitionTime -= deltaTime;

			vortexProgram.begin();
			vortexProgram.setUniformf("transitionTime", BATTLE_START_TRANSITION_TIME - transitionTime);
			vortexProgram.end();

			if (transitionTime <= 0.0f) {
				transitionTime = -1.0f;

				if (worldChangeFlag) {
					worldChangeFlag = false;
					changeToWorld();
				} else if (battleChangeFlag) {
					battleChangeFlag = false;
					vortexOff();
					changeToBattle();
				}
			}
		}

		currentEngine.update(deltaTime);
	}

	public void flagBattleChange() {
		battleChangeFlag = true;
		transitionTime = BATTLE_START_TRANSITION_TIME;
		vortexOn();
	}

	public void flagWorldChange() {
		worldChangeFlag = true;
		transitionTime = BATTLE_END_TRANSITION_TIME;
	}

	public static boolean isTransitioning() {
		return battleChangeFlag || worldChangeFlag;
	}

	public void changeToBattle() {
		Gdx.app.debug("BATTLE_CHANGE", "Changing to battle view.");

		this.currentEngine = battleEngine;

		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();

		addSlimes(battleEngine, random.nextInt(3) + 1);
		battleEngine.getSystem(BattleMessageSystem.class).clearAllMessages();
	}

	public void changeToWorld() {
		Gdx.app.debug("WORLD_CHANGE", "Changing to world view.");

		this.currentEngine = worldEngine;

		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		camera.update();
	}

	public void initWorldEngine(Engine engine) {
		maps = new TesseractMap[mapNames.length];
		TmxMapLoader mapLoader = new TmxMapLoader();

		for (int i = 0; i < mapNames.length; i++) {
			maps[i] = new TesseractMap(mapLoader.load(Gdx.files.internal("maps/" + mapNames[i]).path()), batch,
					torchAnim);

			engine.addEntity(maps[i].baseLayerEntity);

			for (Entity e : maps[i].torches) {
				engine.addEntity(e);
			}

			for (Entity e : maps[i].NPCs) {
				engine.addEntity(e);
			}

			engine.addEntity(maps[i].zLayerEntity);
		}

		currentMapIndex = 0;

		worldPlayerEntity = new Entity();
		worldPlayerEntity.add(new Renderable(Facing.DOWN, playerRegions[1], playerRegions[0], playerRegions[3],
				playerRegions[2]).setPrioritity(50));
		worldPlayerEntity.add(getCurrentMap().findPlayerPosition());
		worldPlayerEntity.add(new FocusTaker(camera));
		worldPlayerEntity.add(new Player(PLAYER_NAME));
		worldPlayerEntity.add(new WorldPlayerInputListener());

		worldPlayerEntity.componentAdded.add(monsterTileHandler.movingAddListener);
		worldPlayerEntity.componentRemoved.add(monsterTileHandler.movingRemoveListener);
		worldPlayerEntity.add(new Movable());

		engine.addEntity(worldPlayerEntity);

		engine.addSystem(new WorldPlayerInputSystem(100));
		engine.addSystem(new MovementSystem(getCurrentMap(), 500));
		engine.addSystem(new FocusTakingSystem(750));
		engine.addSystem(new RenderSystem(batch, camera, 1000));
		engine.addSystem(new DialogueSystem(camera, batch, font10, 2000));
	}

	public void initBattleEngine(Engine engine) {
		final float yTile = 12 * WorldConstants.TILE_HEIGHT;

		battlePlayerEntity = new Entity();

		battlePlayerEntity.add(new Position(17 * WorldConstants.TILE_WIDTH, yTile));
		battlePlayerEntity.add(new Renderable(Facing.DOWN, playerRegions[1], playerRegions[0], playerRegions[3],
				playerRegions[2]).setPrioritity(50));
		battlePlayerEntity.add(new Stats(100, 25, 4));

		Player playerComp = new Player(PLAYER_NAME);
		battlePlayerEntity.add(playerComp);
		battlePlayerEntity.add(new Named(playerComp.name));

		Rectangle screenRect = new Rectangle(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		statusDialog = new Entity();
		statusDialog.add(new RelativePosition(new Rectangle(0.0f, 0.0f, 0.3f, 0.2f), screenRect));
		statusDialog.add(new BattleDialog(Color.BLUE, Color.DARK_GRAY));

		final int menuDialogCount = 3;
		final String[] dialogStrings = { "Attack", "Jump", "Flee" };

		menuDialogs = new Entity[menuDialogCount];
		menuTexts = new Entity[menuDialogCount];

		for (int i = 0; i < menuDialogCount; i++) {
			menuDialogs[i] = new Entity();

			final float menuW = 0.7f / (float) menuDialogCount;
			final float menuX = (0.3f) + (menuW * (float) i);

			final String thisString = dialogStrings[i];

			RelativePosition menuPos = new RelativePosition(new Rectangle(menuX, 0.0f, menuW, 0.2f), screenRect);

			menuDialogs[i].add(menuPos);
			menuDialogs[i].add(new BattleDialog(Color.NAVY, Color.DARK_GRAY));
			menuDialogs[i].add(new MouseClickListener(menuPos.pos, BattlePerformers.performers[i]));

			menuTexts[i] = new Entity();
			Text text = new Text(thisString);
			menuTexts[i].add(text);
			menuTexts[i].add(RelativePosition.makeCentred(Text.getTextRectangle(0.0f, 0.0f, text, font24),
					menuDialogs[i]));

		}

		hpText = new Entity();
		Text hpTextComponent = new Text("HP: 100/100");
		hpText.add(RelativePosition.makeCentredX(Text.getTextRectangle(0.0f, 0.75f, hpTextComponent, font16),
				statusDialog));
		hpText.add(hpTextComponent);

		rageText = new Entity();
		Text rageTextComponent = new Text("Rage Level:\nReally mad.");
		rageText.add(RelativePosition.makeCentredX(Text.getTextRectangle(0.0f, 0.5f, rageTextComponent, font16),
				statusDialog));
		rageText.add(rageTextComponent);

		engine.addEntity(battlePlayerEntity);
		engine.addEntity(statusDialog);
		for (int i = 0; i < menuDialogs.length; i++) {
			engine.addEntity(menuDialogs[i]);
			engine.addEntity(menuTexts[i]);
		}
		engine.addEntity(hpText);
		engine.addEntity(rageText);

		BattleMessageSystem battleMessageSystem = new BattleMessageSystem(font24, camera, screenRect, 300);
		BattleAttackSystem battleAttackSystem = new BattleAttackSystem(battleMessageSystem, 200);
		BattlePerformers.battleAttackSystem = battleAttackSystem;
		BattlePerformers.battleMessageSystem = battleMessageSystem;

		engine.addSystem(new BattleInputSystem(camera, 100));
		engine.addSystem(battleAttackSystem.addVictoryListener(battleVictoryListener));
		engine.addSystem(battleMessageSystem);
		engine.addSystem(new RenderSystem(batch, camera, 1000));
		engine.addSystem(new BattleDialogRenderSystem(camera, 2000));
		engine.addSystem(new TextRenderSystem(batch, font16, 3000));
	}

	private void addSlimes(Engine engine, int count) {
		if (count < 1 || count > 3) {
			throw new GdxRuntimeException("Only between 1-3 slimes supported.");
		}

		TextureRegion slimeRegion = TextureRegion.split(slimeTexture, WorldConstants.TILE_WIDTH,
				WorldConstants.TILE_HEIGHT)[0][0];

		final float yMiddle = 12 * WorldConstants.TILE_HEIGHT;

		Position[] positions = null;

		if (count == 1) {
			positions = new Position[1];
			positions[0] = new Position(3 * WorldConstants.TILE_WIDTH, yMiddle);
		} else if (count == 2) {
			positions = new Position[2];
			positions[0] = new Position(3 * WorldConstants.TILE_WIDTH, yMiddle + 2 * WorldConstants.TILE_HEIGHT);
			positions[1] = new Position(3 * WorldConstants.TILE_WIDTH, yMiddle - 2 * WorldConstants.TILE_HEIGHT);
		} else if (count == 3) {
			positions = new Position[3];
			positions[0] = new Position(3 * WorldConstants.TILE_WIDTH, yMiddle);
			positions[1] = new Position(2 * WorldConstants.TILE_WIDTH, yMiddle + 2 * WorldConstants.TILE_HEIGHT);
			positions[2] = new Position(2 * WorldConstants.TILE_WIDTH, yMiddle - 2 * WorldConstants.TILE_HEIGHT);
		}

		for (int i = 0; i < count; i++) {
			Entity slimeEntity = new Entity();
			slimeEntity.add(positions[i]).add(new Renderable(slimeRegion).setPrioritity(50));
			slimeEntity.add(new Stats(50, 2, 2));

			Enemy slime1 = new Enemy("Green Ooze");
			slimeEntity.add(slime1);
			slimeEntity.add(new Named(slime1.speciesName + " " + (i + 1)));
			engine.addEntity(slimeEntity);
		}
	}

	public void loadShader() {
		vortexProgram = new ShaderProgram(Gdx.files.internal("shaders/passVertex.glslv"),
				Gdx.files.internal("shaders/vortexFragment.glslf"));
		if (!vortexProgram.isCompiled()) {
			Gdx.app.debug("LOAD_SHADER", "Shader compilation produced following log:\n" + vortexProgram.getLog());
			throw new GdxRuntimeException("Shader compilation failed");
		}

		vortexOff();
	}

	public void vortexOn() {
		batch.setShader(vortexProgram);
		vortexProgram.begin();
		vortexProgram.setUniformf("vortexFlag", 1.0f);
		vortexProgram.setUniformf("transitionTime", 0.0f);
		vortexProgram
				.setUniformf("iResolution", new Vector2(camera.viewportWidth * 2.0f, camera.viewportHeight * 2.0f));
		vortexProgram.setUniformf("expectedTransitionTime", TesseractMain.BATTLE_START_TRANSITION_TIME);
		vortexProgram.end();
	}

	public void vortexOff() {
		vortexProgram.begin();
		vortexProgram.setUniformf("vortexFlag", 0.0f);
		vortexProgram.end();
		batch.setShader(null);
	}

	public static TesseractMap getCurrentMap() {
		return maps[currentMapIndex];
	}

	@Override
	public void dispose() {
		super.dispose();

		for (TesseractMap map : maps) {
			if (map != null) {
				map.dispose();
			}
		}

		if (torchTexture != null) {
			torchAnim = null;
			torchTexture.dispose();
		}

		if (playerTexture != null) {
			playerTexture.dispose();
		}

		if (slimeTexture != null) {
			slimeTexture.dispose();
		}

		if (font10 != null) {
			font10.dispose();
		}

		if (font12 != null) {
			font12.dispose();
		}

		if (font16 != null) {
			font16.dispose();
		}

		if (font24 != null) {
			font24.dispose();
		}
	}

	public class BattleVictoryListener implements Listener<Float> {
		@Override
		public void receive(Signal<Float> signal, Float object) {
			Gdx.app.debug("BATTLE_END", "Battle end detected.");
			flagWorldChange();
		}
	}

	public class MonsterTileHandler {
		boolean						moving					= false;

		int							monsterTilesVisited		= 0;

		MonsterTileAddListener		movingAddListener		= new MonsterTileAddListener();
		MonsterTileRemoveListener	movingRemoveListener	= new MonsterTileRemoveListener();

		public class MonsterTileAddListener implements Listener<Entity> {
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				if (ComponentMapper.getFor(Moving.class).has(object)) {
					Gdx.app.debug("MOVING_ADD", "Moving component added to player.");
					moving = true;
				}
			}
		}

		public class MonsterTileRemoveListener implements Listener<Entity> {
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				if (moving && !ComponentMapper.getFor(Moving.class).has(object)) {
					Gdx.app.debug("MOVING_REMOVE", "Moving removed.");
					moving = false;

					GridPoint2 pos = ComponentMapper.getFor(Position.class).get(object).getGridPosition();

					if (getCurrentMap().isMonsterTile(pos)) {
						monsterTilesVisited++;
						Gdx.app.debug("MONSTER_STEPS", "" + monsterTilesVisited + " steps taken.");
						final double prob = 0.02 * monsterTilesVisited;
						final double rand = Math.random();

						if (rand <= prob) {
							monsterTilesVisited = 0;
							flagBattleChange();
						}
					}
				}
			}
		}
	}
}
