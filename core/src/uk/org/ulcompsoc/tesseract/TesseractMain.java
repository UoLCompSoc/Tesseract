package uk.org.ulcompsoc.tesseract;

import java.util.Random;

import uk.org.ulcompsoc.tesseract.animations.PingPongFrameResolver;
import uk.org.ulcompsoc.tesseract.animations.SlimeFrameResolver;
import uk.org.ulcompsoc.tesseract.battle.BattlePerformers;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Combatant;
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
import uk.org.ulcompsoc.tesseract.systems.BattleAISystem;
import uk.org.ulcompsoc.tesseract.systems.BattleAttackSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleDialogRenderSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleInputSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleMessageSystem;
import uk.org.ulcompsoc.tesseract.systems.BuffSystem;
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
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TesseractMain extends ApplicationAdapter {
	public static final String			PLAYER_NAME						= "Valiant Hero™";
	public static final float			BATTLE_END_TRANSITION_TIME		= 4.0f;
	public static final float			BATTLE_START_TRANSITION_TIME	= 2.0f;

	private Random						random							= null;

	private SpriteBatch					batch							= null;
	private Camera						camera							= null;

	private ShaderProgram				vortexProgram					= null;

	private BitmapFont					font10							= null;
	private BitmapFont					font12							= null;
	private BitmapFont					font16							= null;
	private BitmapFont					font24							= null;

	private Engine						currentEngine					= null;
	private Engine						battleEngine					= null;
	private Engine[]					worldEngines					= null;

	public static Entity				battlePlayerEntity				= null;
	public static Entity				worldPlayerEntity				= null;
	public static Stats					playerStats						= null;

	BattleVictoryListener				battleVictoryListener			= null;

	private static boolean				battleChangeFlag				= false;
	private static boolean				worldChangeFlag					= false;
	private static boolean				diffWorldFlag					= false;
	private float						transitionTime					= -1.0f;

	private Texture						playerTexture					= null;
	private TextureRegion[]				playerRegions					= null;

	private Texture[]					slimeTextures					= null;
	private Animation[]					slimeAnims						= null;

	private Texture[]					torchTextures					= null;
	private Animation[]					torchAnims						= null;

	private Texture[]					bossTextures					= null;
	private Animation[]					bossAnims						= null;

	private DialogueFinishListener		healNPCListener					= null;
	private DialogueFinishListener		bossBattleListener				= null;

	private MonsterTileHandler			monsterTileHandler				= null;

	private Entity						statusDialog					= null;
	private Entity[]					menuDialogs						= null;
	private Entity[]					menuTexts						= null;
	private Entity						hpText							= null;
	private Entity						rageText						= null;

	public static final String[]		mapNames						= { "world1/world1.tmx", "world2/world2.tmx",
			"world3/world3.tmx", "world4/world4.tmx", "world5/world5.tmx", "world6/world6.tmx" };
	public static final Color[]			mapColors						= {
			new Color(80.0f / 255.0f, 172.0f / 255.0f, 61.0f / 255.0f, 1.0f),
			new Color(61.0f / 255.0f, 111.f / 255.0f, 172.0f / 255.0f, 1.0f),
			new Color(169.0f / 255.0f, 117.0f / 255.0f, 65.0f / 255.0f, 1.0f),
			new Color(152.0f / 255.0f, 61.0f / 255.0f, 172.0f / 255.0f, 1.0f),
			new Color(188.0f / 255.0f, 44.0f / 255.0f, 52.0f / 255.0f, 1.0f),
			new Color(116.0f / 255.0f, 116.0f / 255.0f, 116.0f / 255.0f, 1.0f) };

	public static final String[]		slimeFiles						= { "monsters/world1_slime.png",
			"monsters/world2_slime.png", "monsters/world3_slime.png", "monsters/world4_slime.png",
			"monsters/world5_slime.png", "monsters/world6_slime.png"	};

	public static final String[]		torchFiles						= { "torches/world1_torches.png",
			"torches/world2_torches.png", "torches/world3_torches.png", "torches/world4_torches.png",
			"torches/world5_torches.png", "torches/world6_torches.png"	};

	public static final String[]		bossFiles						= { "bosses/final_boss.png",
			"bosses/world2_boss.png", "bosses/world3_boss.png", "bosses/world4_boss.png", "bosses/world5_boss.png",
			"bosses/world6_boss.png"									};

	public static final GridPoint2[]	bossSizes						= { new GridPoint2(128, 128),
			new GridPoint2(64, 64), new GridPoint2(64, 64), new GridPoint2(64, 64), new GridPoint2(64, 64),
			new GridPoint2(64, 64)										};

	private static TesseractMap[]		maps							= null;
	public static int					currentMapIndex					= 0;

	@SuppressWarnings("unused")
	private GameState					gameState						= null;

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

		loadSlimeFiles();

		loadTorchFiles();

		loadBossFiles();

		playerStats = new Stats(100, 30, 5, 70);

		monsterTileHandler = new MonsterTileHandler();
		battleVictoryListener = new BattleVictoryListener();

		healNPCListener = new DialogueFinishListener();
		bossBattleListener = new DialogueFinishListener();

		battleEngine = new Engine();
		worldEngines = new Engine[mapNames.length];

		initBattleEngine(battleEngine);
		initWorldEngines(worldEngines);

		changeToWorld(true);
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClearColor(mapColors[currentMapIndex].r, mapColors[currentMapIndex].g, mapColors[currentMapIndex].b,
				mapColors[currentMapIndex].a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
			if (Gdx.input.isKeyJustPressed(Keys.F5)) {
				currentMapIndex++;

				if (currentMapIndex == mapNames.length) {
					currentMapIndex = 0;
				}

				changeToWorld(true);
			}
		}

		if (battleChangeFlag || worldChangeFlag) {
			transitionTime -= deltaTime;

			vortexProgram.begin();
			vortexProgram.setUniformf("transitionTime", BATTLE_START_TRANSITION_TIME - transitionTime);
			vortexProgram.end();

			if (transitionTime <= 0.0f) {
				transitionTime = -1.0f;

				if (worldChangeFlag) {
					worldChangeFlag = false;
					diffWorldFlag = false;
					changeToWorld(diffWorldFlag);
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

	public void flagWorldChange(boolean boss) {
		worldChangeFlag = true;
		diffWorldFlag = false;
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

	public void changeToWorld(boolean diffWorld) {
		Gdx.app.debug("WORLD_CHANGE", "Changing to world " + (currentMapIndex + 1) + ".");

		if (diffWorld) {
			this.currentEngine.removeEntity(worldPlayerEntity);
		}

		this.currentEngine = worldEngines[currentMapIndex];

		if (diffWorld) {
			worldPlayerEntity.add(getCurrentMap().findPlayerPosition());
			currentEngine.addEntity(worldPlayerEntity);
		}

		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		camera.update();
	}

	public void initWorldEngines(Engine[] engines) {
		maps = new TesseractMap[mapNames.length];
		TmxMapLoader mapLoader = new TmxMapLoader();

		currentMapIndex = 0;

		worldPlayerEntity = new Entity();
		worldPlayerEntity.add(new Renderable(Facing.DOWN, playerRegions[1], playerRegions[0], playerRegions[3],
				playerRegions[2]).setPrioritity(50));
		worldPlayerEntity.add(new FocusTaker(camera));
		worldPlayerEntity.add(new Player(PLAYER_NAME));
		worldPlayerEntity.add(new WorldPlayerInputListener());

		worldPlayerEntity.componentAdded.add(monsterTileHandler.movingAddListener);
		worldPlayerEntity.componentRemoved.add(monsterTileHandler.movingRemoveListener);
		worldPlayerEntity.add(new Movable());

		worldPlayerEntity.add(playerStats);

		for (int i = 0; i < mapNames.length; i++) {
			Engine engine = new Engine();

			maps[i] = new TesseractMap(mapLoader.load(Gdx.files.internal("maps/" + mapNames[i]).path()), batch,
					torchAnims[i], healNPCListener, bossBattleListener);

			engine.addEntity(maps[i].baseLayerEntity);

			for (Entity e : maps[i].torches) {
				engine.addEntity(e);
			}

			for (Entity e : maps[i].NPCs) {
				engine.addEntity(e);
			}

			engine.addEntity(maps[i].zLayerEntity);

			if (maps[i].bossEntity != null) {
				maps[i].bossEntity.add(new Renderable(bossAnims[i]).setPrioritity(100).setAnimationResolver(
						new PingPongFrameResolver()));
				engine.addEntity(maps[i].bossEntity);
			}

			engine.addSystem(new WorldPlayerInputSystem(100));
			engine.addSystem(new MovementSystem(getCurrentMap(), 500));
			engine.addSystem(new FocusTakingSystem(750));
			engine.addSystem(new RenderSystem(batch, camera, 1000));
			engine.addSystem(new DialogueSystem(camera, batch, font10, 2000));

			engines[i] = engine;
		}

		currentEngine = worldEngines[0]; // DIRTY HACK
	}

	public void initBattleEngine(Engine engine) {
		final float yTile = 12 * WorldConstants.TILE_HEIGHT;

		battlePlayerEntity = new Entity();

		battlePlayerEntity.add(new Position(17 * WorldConstants.TILE_WIDTH, yTile));
		battlePlayerEntity.add(new Renderable(Facing.DOWN, playerRegions[1], playerRegions[0], playerRegions[3],
				playerRegions[2]).setPrioritity(50));
		battlePlayerEntity.add(playerStats);
		Gdx.app.debug("PLAYER_THINK_TIME", "Player has " + playerStats.getThinkTime() + "s think time.");

		Player playerComp = new Player(PLAYER_NAME);
		battlePlayerEntity.add(playerComp);
		Combatant playerCombatant = new Combatant();
		battlePlayerEntity.add(playerCombatant);
		battlePlayerEntity.add(new Named(playerComp.name));

		Rectangle screenRect = new Rectangle(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		statusDialog = new Entity();
		statusDialog.add(new RelativePosition(new Rectangle(0.0f, 0.0f, 0.3f, 0.2f), screenRect));
		statusDialog.add(new BattleDialog(Color.BLUE, Color.DARK_GRAY, playerCombatant));

		final int menuDialogCount = 4;
		final String[] dialogStrings = { "Attack", "Defend", "Quaff", "Flee" };

		menuDialogs = new Entity[menuDialogCount];
		menuTexts = new Entity[menuDialogCount];

		for (int i = 0; i < menuDialogCount; i++) {
			menuDialogs[i] = new Entity();

			final float menuW = 0.7f / (float) menuDialogCount;
			final float menuX = (0.3f) + (menuW * (float) i);

			final String thisString = dialogStrings[i];

			RelativePosition menuPos = new RelativePosition(new Rectangle(menuX, 0.0f, menuW, 0.2f), screenRect);

			menuDialogs[i].add(menuPos);
			menuDialogs[i].add(new BattleDialog(Color.NAVY, Color.DARK_GRAY, playerCombatant));
			menuDialogs[i].add(new MouseClickListener(menuPos.pos, BattlePerformers.performers[i]));

			menuTexts[i] = new Entity();
			Text text = new Text(thisString);
			menuTexts[i].add(text);
			menuTexts[i].add(RelativePosition.makeCentred(Text.getTextRectangle(0.0f, 0.0f, text, font24),
					menuDialogs[i]));

		}

		hpText = new Entity();
		Text hpTextComponent = new Text("HP: ", Color.WHITE, playerStats.hpChangeSignal);
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

		engine.addSystem(new BuffSystem(50));
		engine.addSystem(new BattleAISystem(75));
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
			slimeEntity.add(positions[i]).add(
					new Renderable(slimeAnims[currentMapIndex]).setPrioritity(50).setAnimationResolver(
							new SlimeFrameResolver()));
			Stats slimeStats = new Stats(50, 10, 1, 2 + (random.nextInt(3) + 1) * 5);
			slimeEntity.add(slimeStats);
			Gdx.app.debug("SLIME_THINK_TIME", "Slimes think for " + slimeStats.getThinkTime() + "s.");
			slimeEntity.add(new Combatant());

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

	public void loadTorchFiles() {
		torchTextures = new Texture[torchFiles.length];
		torchAnims = new Animation[torchFiles.length];

		for (int i = 0; i < torchFiles.length; i++) {
			torchTextures[i] = new Texture(Gdx.files.internal(torchFiles[i]));
			TextureRegion[] torchRegions = TextureRegion.split(torchTextures[i], WorldConstants.TILE_WIDTH,
					WorldConstants.TILE_HEIGHT)[0];
			torchAnims[i] = new Animation(0.15f, torchRegions[0], torchRegions[1], torchRegions[2]);
			torchAnims[i].setPlayMode(PlayMode.LOOP_PINGPONG);
		}
	}

	public void loadSlimeFiles() {
		slimeTextures = new Texture[slimeFiles.length];
		slimeAnims = new Animation[slimeFiles.length];

		for (int i = 0; i < slimeFiles.length; i++) {
			slimeTextures[i] = new Texture(Gdx.files.internal(slimeFiles[i]));
			TextureRegion[] slimeRegions = TextureRegion.split(slimeTextures[i], WorldConstants.TILE_WIDTH,
					WorldConstants.TILE_HEIGHT)[0];
			slimeAnims[i] = new Animation(0.75f, slimeRegions[0], slimeRegions[1]);
			slimeAnims[i].setPlayMode(PlayMode.NORMAL);
		}
	}

	public void loadBossFiles() {
		bossTextures = new Texture[bossFiles.length];
		bossAnims = new Animation[bossFiles.length];

		for (int i = 0; i < bossFiles.length; i++) {
			bossTextures[i] = new Texture(Gdx.files.internal(bossFiles[i]));
			TextureRegion[] bossRegions = TextureRegion.split(bossTextures[i], bossSizes[i].x, bossSizes[i].y)[0];
			bossAnims[i] = new Animation(0.1f, new Array<TextureRegion>(bossRegions));
		}
	}

	public void startBossBattle() {
		Gdx.app.debug("BOSS_BATTLE", "Let's get ready to rumble.");
	}

	public void healToFull() {
		Gdx.app.debug("HEAL_FULL", "What a nice queen.");
		playerStats.restoreHP(playerStats.maxHP);
	}

	@Override
	public void dispose() {
		super.dispose();

		for (TesseractMap map : maps) {
			if (map != null) {
				map.dispose();
			}
		}

		for (int i = 0; i < torchTextures.length; i++) {
			if (torchTextures[i] != null) {
				torchAnims[i] = null;
				torchTextures[i].dispose();
			}
		}

		for (int i = 0; i < slimeTextures.length; i++) {
			if (slimeTextures[i] != null) {
				slimeAnims[i] = null;
				slimeTextures[i].dispose();
			}
		}

		for (int i = 0; i < bossTextures.length; i++) {
			if (bossTextures[i] != null) {
				bossAnims[i] = null;
				bossTextures[i].dispose();
			}
		}

		if (playerTexture != null) {
			playerTexture.dispose();
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

	public class BattleVictoryListener implements Listener<Boolean> {
		@Override
		public void receive(Signal<Boolean> signal, Boolean object) {
			Gdx.app.debug("BATTLE_END", "Battle end detected: boss = " + object);
			flagWorldChange(object.booleanValue());
		}
	}

	public class DialogueFinishListener implements Listener<Entity> {
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			// Dialogue dia =
			// ComponentMapper.getFor(Dialogue.class).get(object);

			Gdx.app.debug("DIA_FINISH", "Detected finish in dialogue.");

			Enemy e = ComponentMapper.getFor(Enemy.class).get(object);

			if (e != null) {
				startBossBattle();
			} else {
				healToFull();
			}
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
					// Gdx.app.debug("MOVING_ADD",
					// "Moving component added to player.");
					moving = true;
				}
			}
		}

		public class MonsterTileRemoveListener implements Listener<Entity> {
			@Override
			public void receive(Signal<Entity> signal, Entity object) {
				if (moving && !ComponentMapper.getFor(Moving.class).has(object)) {
					// Gdx.app.debug("MOVING_REMOVE", "Moving removed.");
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
