package uk.org.ulcompsoc.tesseract;

import java.util.Random;

import uk.org.ulcompsoc.tesseract.animations.PingPongFrameResolver;
import uk.org.ulcompsoc.tesseract.animations.PlayerAnimationFrameResolver;
import uk.org.ulcompsoc.tesseract.animations.SlimeFrameResolver;
import uk.org.ulcompsoc.tesseract.battle.BattlePerformers;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Boss;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.FocusTaker;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Movable;
import uk.org.ulcompsoc.tesseract.components.Moving;
import uk.org.ulcompsoc.tesseract.components.NPC;
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
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;
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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TesseractMain extends ApplicationAdapter {
	public static final String				PLAYER_NAME						= "Valiant Hero™";
	public static final float				BATTLE_END_TRANSITION_TIME		= 4.0f;
	public static final float				BATTLE_START_TRANSITION_TIME	= 2.0f;
	public static final float				WORLD_SELECT_LOAD_TIME			= 0.25f;
	public static final float				WORLD_SELECT_CHANGE_TIME		= 1.0f;

	private Random							random							= null;

	private SpriteBatch						batch							= null;
	private Camera							camera							= null;

	public static MusicManager				musicManager					= null;

	private ShaderProgram					vortexProgram					= null;

	private FreeTypeFontGenerator			fontGenerator					= null;

	private BitmapFont						font10							= null;
	private BitmapFont						font12							= null;
	private BitmapFont						font16							= null;
	private BitmapFont						font24							= null;

	private Engine							currentEngine					= null;

	private static Engine					battleEngine					= null;
	private static Engine					worldSelectEngine				= null;
	private static Engine[]					worldEngines					= null;

	public static Entity					battlePlayerEntity				= null;
	public static Entity					worldPlayerEntity				= null;
	public static Stats						playerStats						= null;

	private BattleVictoryListener			battleVictoryListener			= null;
	private BattleDefeatListener			battleDefeatListener			= null;

	private static boolean					bossIncomingFlag				= false;
	private static boolean					battleChangeFlag				= false;
	private static boolean					worldChangeFlag					= false;

	private boolean							healOnTransition				= false;
	private static boolean					worldSelectChangeFlag			= false;
	private static int						diffWorldFlag					= -1;
	private float							transitionTime					= -1.0f;

	private int								playerPowerLevel				= 0;

	private PlayerAnimationFrameResolver	playerAnimationFrameResolver	= null;
	private Texture[]						playerTextures					= null;
	private Animation[]						playerIdleAnims					= null;
	private Animation[]						playerUpAnims					= null;
	private Animation[]						playerDownAnims					= null;
	private Animation[]						playerLeftAnims					= null;
	private Animation[]						playerRightAnims				= null;
	private Animation						playerIdle						= null;
	private Animation						playerUp						= null;
	private Animation						playerDown						= null;
	private Animation						playerLeft						= null;
	private Animation						playerRight						= null;

	private Texture[]						slimeTextures					= null;
	private Animation[]						slimeAnims						= null;

	private Texture[]						torchTextures					= null;
	private Animation[]						torchAnims						= null;

	private Texture[]						bossTextures					= null;
	private Animation[]						bossAnims						= null;
	private Stats[]							bossStats						= { new Stats(20, 15, 10, 3),
			new Stats(100, 15, 10, 1), new Stats(150, 20, 10, 5), new Stats(150, 25, 10, 5), new Stats(150, 30, 10, 2),
			new Stats(150, 35, 10, 5), new Stats(100, 40, 5, 2)			};

	private Texture[]						worldSelectTextures				= null;

	private Texture							openDoorTex						= null;
	private Texture							closedDoorTex					= null;

	private DialogueFinishListener			healNPCListener					= null;
	private DialogueFinishListener			bossBattleListener				= null;
	private DialogueFinishListener			doorOpenListener				= null;
	private WorldSelectChangeListener		worldSelectChangeListener		= null;

	private MonsterTileHandler				monsterTileHandler				= null;

	private Entity							statusDialog					= null;
	private Entity[]						menuDialogs						= null;
	private Entity[]						menuTexts						= null;
	private Entity							hpText							= null;
	private Entity							rageText						= null;

	public static final String[]			playerFiles						= { "player/player_0.png",
			"player/player_1.png", "player/player_2.png", "player/player_3.png", "player/player_4.png",
			"player/player_5.png",											};

	public static final String[]			mapNames						= { "world1/world1.tmx",
			"world2/world2.tmx", "world3/world3.tmx", "world4/world4.tmx", "world5/world5.tmx", "world6/world6.tmx",
			"world7/world7.tmx"											};
	public static final Color[]				mapColors						= {
			new Color(80.0f / 255.0f, 172.0f / 255.0f, 61.0f / 255.0f, 1.0f),
			new Color(61.0f / 255.0f, 111.f / 255.0f, 172.0f / 255.0f, 1.0f),
			new Color(169.0f / 255.0f, 117.0f / 255.0f, 65.0f / 255.0f, 1.0f),
			new Color(152.0f / 255.0f, 61.0f / 255.0f, 172.0f / 255.0f, 1.0f),
			new Color(188.0f / 255.0f, 44.0f / 255.0f, 52.0f / 255.0f, 1.0f),
			new Color(116.0f / 255.0f, 116.0f / 255.0f, 116.0f / 255.0f, 1.0f),
			new Color(246.0f / 255.0f, 241.0f / 255.0f, 83.0f / 255.0f, 1.0f) };

	public static final String[]			slimeFiles						= { "monsters/world1_slime.png",
			"monsters/world2_slime.png", "monsters/world3_slime.png", "monsters/world4_slime.png",
			"monsters/world5_slime.png", "monsters/world6_slime.png"		};

	public static final String[]			torchFiles						= { "torches/world1_torches.png",
			"torches/world2_torches.png", "torches/world3_torches.png", "torches/world4_torches.png",
			"torches/world5_torches.png", "torches/world6_torches.png"		};

	public static final String[]			bossFiles						= { "bosses/final_boss.png",
			"bosses/world2_boss.png", "bosses/world3_boss.png", "bosses/world4_boss.png", "bosses/world5_boss.png",
			"bosses/world6_boss.png", "bosses/final_boss.png"				};

	public static final GridPoint2[]		bossSizes						= { new GridPoint2(128, 128),
			new GridPoint2(64, 64), new GridPoint2(64, 64), new GridPoint2(64, 64), new GridPoint2(64, 64),
			new GridPoint2(64, 64), new GridPoint2(128, 128)				};

	public static final String[]			worldSelectTexFiles				= { "worldtextures/world1_64.png",
			"worldtextures/world2_64.png", "worldtextures/world3_64.png", "worldtextures/world4_64.png",
			"worldtextures/world5_64.png", "worldtextures/world6_64.png"	};

	public static final String[]			musicFiles						= { "music/world1.ogg", "music/world2.ogg",
			"music/world3.ogg", "music/world4.ogg", "music/world5.ogg", "music/world6.ogg", "music/world7.ogg" };

	private static TesseractMap[]			maps							= null;
	public static int						currentMapIndex					= 0;

	final float								yTile							= 12 * WorldConstants.TILE_HEIGHT;

	@SuppressWarnings("unused")
	private GameState						gameState						= null;

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

		musicManager = new MusicManager(musicFiles);

		loadShader();

		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoRegular.ttf"));
		FreeTypeFontParameter para = new FreeTypeFontParameter();

		para.size = 10;
		font10 = fontGenerator.generateFont(para);
		// font10 = new BitmapFont(Gdx.files.internal("fonts/robotobm10.fnt"),
		// Gdx.files.internal("fonts/robotobm10.png"),
		// false);

		para.size = 12;
		font12 = fontGenerator.generateFont(para);
		// font12 = new BitmapFont(Gdx.files.internal("fonts/robotobm12.fnt"),
		// Gdx.files.internal("fonts/robotobm12.png"),
		// false);

		para.size = 16;
		font16 = fontGenerator.generateFont(para);
		// font16 = new BitmapFont(Gdx.files.internal("fonts/robotobm16.fnt"),
		// Gdx.files.internal("fonts/robotobm16.png"),
		// false);

		para.size = 24;
		font24 = fontGenerator.generateFont(para);
		// font24 = new BitmapFont(Gdx.files.internal("fonts/robotobm24.fnt"),
		// Gdx.files.internal("fonts/robotobm24.png"),
		// false);

		loadPlayerFiles();

		loadSlimeFiles();

		loadTorchFiles();

		loadBossFiles();

		openDoorTex = new Texture(Gdx.files.internal("door_open.png"));
		closedDoorTex = new Texture(Gdx.files.internal("door_closed.png"));

		playerStats = new Stats(100, 30, 5, 70);

		monsterTileHandler = new MonsterTileHandler();
		battleVictoryListener = new BattleVictoryListener();
		battleDefeatListener = new BattleDefeatListener();

		healNPCListener = new DialogueFinishListener();
		bossBattleListener = new DialogueFinishListener();
		doorOpenListener = new DialogueFinishListener();
		worldSelectChangeListener = new WorldSelectChangeListener();

		battleEngine = new Engine();
		worldEngines = new Engine[mapNames.length];
		worldSelectEngine = new Engine();

		initBattleEngine(battleEngine);
		initWorldEngines(worldEngines);
		initWorldSelectEngine(worldSelectEngine);

		changeToWorld(0);
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClearColor(mapColors[currentMapIndex].r, mapColors[currentMapIndex].g, mapColors[currentMapIndex].b,
				mapColors[currentMapIndex].a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
			if (Gdx.input.isKeyJustPressed(Keys.F5)) {
				changeToWorld((currentMapIndex + 1));
			} else if (Gdx.input.isKeyJustPressed(Keys.F1)) {
				doPlayerPowerUp();
			} else if (Gdx.input.isKeyJustPressed(Keys.F2)) {
				for (int i = 0; i < 20; i++) {
					doPlayerPowerUp();
				}
			}
		}

		if (isTransitioning()) {
			transitionTime -= deltaTime;

			vortexProgram.begin();
			vortexProgram.setUniformf("transitionTime", BATTLE_START_TRANSITION_TIME - transitionTime);
			vortexProgram.end();

			if (transitionTime <= 0.0f) {
				transitionTime = -1.0f;
				if (healOnTransition) {
					healToFull();
					healOnTransition = false;
				}

				if (worldChangeFlag) {
					changeToWorld(diffWorldFlag);
				} else if (battleChangeFlag) {
					vortexOff();
					changeToBattle(bossIncomingFlag);
				} else if (worldSelectChangeFlag) {
					changeToWorldSelect();
				}

				battleChangeFlag = worldChangeFlag = worldSelectChangeFlag = false;
				diffWorldFlag = -1;
			}
		}

		musicManager.update(deltaTime);
		currentEngine.update(deltaTime);
	}

	public void flagBattleChange(boolean boss) {
		battleChangeFlag = true;
		bossIncomingFlag = boss;
		transitionTime = BATTLE_START_TRANSITION_TIME;
		vortexOn();
	}

	public void flagWorldChange(int newWorld) {
		worldChangeFlag = true;
		diffWorldFlag = newWorld;

		musicManager.fadeOut(WORLD_SELECT_CHANGE_TIME);
		transitionTime = WORLD_SELECT_CHANGE_TIME;
	}

	public void flagWorldReturn(boolean bossBattleJustHappened) {
		worldChangeFlag = true;
		diffWorldFlag = -1;

		if (bossBattleJustHappened) {
			getCurrentMap().setBossBeaten();
			int bossesRemaining = 0;

			for (TesseractMap map : maps) {
				if (!map.bossBeaten && map.bossEntity != null) {
					bossesRemaining++;
				}
			}

			Gdx.app.debug("BOSSES_REMAINING", "" + bossesRemaining + " bosses remaining.");

			if (bossesRemaining == 1) { // 1 because of the last boss
				Gdx.app.debug("ALL_BOSSES", "All bosses defeated, opening final world.");
				Engine world1 = worldEngines[0];
				world1.removeEntity(maps[0].doorEntity);
				world1.addEntity(maps[0].openDoorEntity);
			} else if (bossesRemaining == 0) {
				flagWorldChange(0);
				return;
			}

			doPlayerPowerUp();
		}

		transitionTime = BATTLE_END_TRANSITION_TIME;
	}

	public void flagWorldSelectChange() {
		worldSelectChangeFlag = true;

		transitionTime = WORLD_SELECT_LOAD_TIME;
	}

	public static boolean isTransitioning() {
		return battleChangeFlag || worldChangeFlag || worldSelectChangeFlag;
	}

	public void changeToBattle(boolean boss) {
		Gdx.app.debug("BATTLE_CHANGE", "Changing to battle view.");

		this.currentEngine = battleEngine;
		@SuppressWarnings("unchecked")
		ImmutableArray<Entity> ents = currentEngine.getEntitiesFor(Family.getFor(Enemy.class));
		if (ents.size() > 0) {
			while (ents.size() > 0) {
				currentEngine.removeEntity(ents.get(0));
			}

			makeBattlePlayerEntity(currentEngine);
		}

		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();

		if (boss) {
			addBoss(battleEngine);
		} else {
			addSlimes(battleEngine, random.nextInt(3) + 1);
		}
		battleEngine.getSystem(BattleMessageSystem.class).clearAllMessages();
	}

	public void changeToWorld(int diffWorld) {
		battleChangeFlag = worldChangeFlag = false;
		Gdx.app.debug("WORLD_CHANGE", "Changing to world " + (diffWorld == -1 ? currentMapIndex : diffWorld) + ".");

		if (diffWorld != -1) {
			if (diffWorld >= maps.length) {
				diffWorld = diffWorld % maps.length;
			}

			if (diffWorld != currentMapIndex) {
				this.currentEngine.removeEntity(worldPlayerEntity);
			}

			currentMapIndex = diffWorld;
		}

		this.currentEngine = worldEngines[currentMapIndex];

		if (getCurrentMap().bossBeaten) {
			currentEngine.removeEntity(getCurrentMap().bossEntity);
		}

		if (diffWorld != -1) {
			worldPlayerEntity.add(getCurrentMap().findPlayerPosition());
			currentEngine.addEntity(worldPlayerEntity);
			musicManager.play(currentMapIndex);
		}

		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		camera.update();

	}

	public void changeToWorldSelect() {
		this.currentEngine = worldSelectEngine;

		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
	}

	public void initWorldEngines(Engine[] engines) {
		maps = new TesseractMap[mapNames.length];
		TmxMapLoader mapLoader = new TmxMapLoader();

		currentMapIndex = -1;

		worldPlayerEntity = new Entity();
		worldPlayerEntity.add(new Renderable(Facing.IDLE, playerIdle, playerUp, playerDown, playerLeft, playerRight,
				new PlayerAnimationFrameResolver()).setPrioritity(50));
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
					(i == mapNames.length - 1 ? null : torchAnims[i]), new TextureRegion(openDoorTex),
					new TextureRegion(closedDoorTex), healNPCListener, bossBattleListener, doorOpenListener);

			engine.addEntity(maps[i].baseLayerEntity);

			if (maps[i].torches != null) {
				for (Entity e : maps[i].torches) {
					engine.addEntity(e);
				}
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

			if (maps[i].doorEntity != null) {
				engine.addEntity(maps[i].doorEntity);
			}

			engine.addSystem(new WorldPlayerInputSystem(worldSelectChangeListener, 100));
			engine.addSystem(new MovementSystem(maps[i], 500));
			engine.addSystem(new FocusTakingSystem(750));
			engine.addSystem(new RenderSystem(batch, camera, 1000));
			engine.addSystem(new DialogueSystem(camera, batch, font10, 2000));

			engines[i] = engine;
		}

		currentEngine = worldEngines[0]; // DIRTY HACK
	}

	public void initBattleEngine(Engine engine) {
		makeBattlePlayerEntity(engine);
		Combatant playerCom = ComponentMapper.getFor(Combatant.class).get(battlePlayerEntity);

		Rectangle screenRect = new Rectangle(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		statusDialog = new Entity();
		statusDialog.add(new RelativePosition(new Rectangle(0.0f, 0.0f, 0.3f, 0.2f), screenRect));
		statusDialog.add(new BattleDialog(Color.BLUE, Color.DARK_GRAY, playerCom));

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
			menuDialogs[i].add(new BattleDialog(Color.NAVY, Color.DARK_GRAY, playerCom));
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
		engine.addSystem(battleAttackSystem.addVictoryListener(battleVictoryListener).addDefeatListener(
				battleDefeatListener));
		engine.addSystem(battleMessageSystem);
		engine.addSystem(new RenderSystem(batch, camera, 1000));
		engine.addSystem(new BattleDialogRenderSystem(camera, 2000));
		engine.addSystem(new TextRenderSystem(batch, font16, 3000));
	}

	public void makeBattlePlayerEntity(Engine engine) {
		battlePlayerEntity = new Entity();

		battlePlayerEntity.add(new Position(17 * WorldConstants.TILE_WIDTH, yTile));
		battlePlayerEntity.add(getBattlePlayerPowerLevelRenderable());
		battlePlayerEntity.add(playerStats);
		Gdx.app.debug("PLAYER_THINK_TIME", "Player has " + playerStats.getThinkTime() + "s think time.");

		Player playerComp = new Player(PLAYER_NAME);
		battlePlayerEntity.add(playerComp);
		Combatant playerCombatant = new Combatant();
		battlePlayerEntity.add(playerCombatant);
		battlePlayerEntity.add(new Named(playerComp.name));

		engine.addEntity(battlePlayerEntity);
	}

	public void initWorldSelectEngine(Engine engine) {
		worldSelectTextures = new Texture[worldSelectTexFiles.length];

		for (int i = 0; i < worldSelectTexFiles.length; i++) {
			worldSelectTextures[i] = new Texture(Gdx.files.internal(worldSelectTexFiles[i]));

			Entity e = new Entity();
			Position p = new Position().setFromGrid(3 + 2 * 3 * (i % 3), (6 + (i > 2 ? 8 : 0)));

			e.add(p);
			e.add(new Renderable(new TextureRegion(worldSelectTextures[i])));
			e.add(new Named("" + i));
			e.add(new MouseClickListener(new Rectangle(p.position.x, p.position.y, 64, 64), new MouseClickPerformer() {
				@Override
				public void perform(Entity invoker, Engine engine) {
					Integer i = Integer.parseInt(ComponentMapper.getFor(Named.class).get(invoker).name);
					flagWorldChange(i.intValue());
				}
			}));

			engine.addEntity(e);
		}

		engine.addSystem(new BattleInputSystem(camera, 50));
		engine.addSystem(new RenderSystem(batch, camera, 1000));
	}

	private void addBoss(Engine engine) {
		TesseractMap map = getCurrentMap();

		if (map.bossBeaten) {
			Gdx.app.debug("ADD_BOSS", "Trying to fight a boss for the second time.");
		}

		Entity boss = new Entity();
		final float yMiddle = 12 * WorldConstants.TILE_HEIGHT;
		boss.add(new Position(3 * WorldConstants.TILE_WIDTH, yMiddle));
		boss.add(new Renderable(bossAnims[currentMapIndex]).setAnimationResolver(new PingPongFrameResolver(0.1f)));
		boss.add(bossStats[currentMapIndex]);
		boss.add(new Boss());
		boss.add(new Combatant());
		Enemy enemy = ComponentMapper.getFor(Enemy.class).get(map.bossEntity);
		boss.add(enemy);
		boss.add(new Named(enemy.speciesName));

		engine.addEntity(boss);
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
			Stats slimeStats = new Stats(50 + 10 * playerPowerLevel, 10, 10, 5 + (random.nextInt(3) + 1) * 5);
			slimeEntity.add(slimeStats);
			Gdx.app.debug("SLIME_THINK_TIME", "Slimes think for " + slimeStats.getThinkTime() + "s.");
			slimeEntity.add(new Combatant().setThinkingTime(0.0f + random.nextFloat()));

			Enemy slime1 = new Enemy("Slime");
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

	public void loadPlayerFiles() {
		playerAnimationFrameResolver = new PlayerAnimationFrameResolver();
		playerTextures = new Texture[playerFiles.length];
		playerIdleAnims = new Animation[playerFiles.length];
		playerUpAnims = new Animation[playerFiles.length];
		playerDownAnims = new Animation[playerFiles.length];
		playerLeftAnims = new Animation[playerFiles.length];
		playerRightAnims = new Animation[playerFiles.length];

		for (int i = 0; i < playerFiles.length; i++) {
			playerTextures[i] = new Texture(Gdx.files.internal(playerFiles[i]));

			TextureRegion[][] playerRegions = TextureRegion.split(playerTextures[i], WorldConstants.TILE_WIDTH,
					WorldConstants.TILE_HEIGHT);

			Animation idle = (!playerFiles[i].equals("player_4.png") ? new Animation(0.2f, playerRegions[0][0],
					playerRegions[0][1], playerRegions[0][2], playerRegions[0][3]) : new Animation(0.2f,
					playerRegions[0][0], playerRegions[0][1], playerRegions[0][2], playerRegions[0][3],
					playerRegions[0][4]));

			TextureRegion[] downArray = new TextureRegion[4];
			System.arraycopy(playerRegions[1], 0, downArray, 0, 4);

			TextureRegion[] upArray = new TextureRegion[4];
			System.arraycopy(playerRegions[2], 0, upArray, 0, 4);

			TextureRegion[] rightArray = new TextureRegion[4];
			System.arraycopy(playerRegions[3], 0, rightArray, 0, 4);

			TextureRegion[] leftArray = new TextureRegion[4];
			System.arraycopy(playerRegions[4], 0, leftArray, 0, 4);

			Animation down = new Animation(0.2f, new Array<TextureRegion>(downArray));
			Animation up = new Animation(0.2f, new Array<TextureRegion>(upArray));
			Animation right = new Animation(0.2f, new Array<TextureRegion>(rightArray));
			Animation left = new Animation(0.2f, new Array<TextureRegion>(leftArray));

			idle.setPlayMode(PlayMode.LOOP_PINGPONG);
			down.setPlayMode(PlayMode.LOOP_PINGPONG);
			up.setPlayMode(PlayMode.LOOP_PINGPONG);
			left.setPlayMode(PlayMode.LOOP_PINGPONG);
			right.setPlayMode(PlayMode.LOOP_PINGPONG);

			playerIdleAnims[i] = idle;
			playerUpAnims[i] = up;
			playerDownAnims[i] = down;
			playerLeftAnims[i] = left;
			playerRightAnims[i] = right;
		}

		playerPowerLevel = 0;
	}

	public Renderable getWorldPlayerPowerLevelRenderable(Facing facing) {
		playerIdle = playerIdleAnims[playerPowerLevel];
		playerUp = playerUpAnims[playerPowerLevel];
		playerDown = playerDownAnims[playerPowerLevel];
		playerLeft = playerLeftAnims[playerPowerLevel];
		playerRight = playerRightAnims[playerPowerLevel];

		return new Renderable(facing, playerIdle, playerUp, playerDown, playerLeft, playerRight,
				playerAnimationFrameResolver).setPrioritity(50);
	}

	public Renderable getBattlePlayerPowerLevelRenderable() {
		return getWorldPlayerPowerLevelRenderable(Facing.IDLE).setPrioritity(50).setAnimationResolver(
				new PingPongFrameResolver());
	}

	public void doPlayerPowerUp() {
		playerPowerLevel++;
		playerStats.addExperience(10);

		if (playerPowerLevel >= playerFiles.length) {
			playerPowerLevel = playerFiles.length - 1;
		} else {
			Facing f = ComponentMapper.getFor(Renderable.class).get(worldPlayerEntity).facing;

			worldPlayerEntity.add(getWorldPlayerPowerLevelRenderable(f));
			battlePlayerEntity.add(getBattlePlayerPowerLevelRenderable());
		}
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
		flagBattleChange(true);
	}

	public void healToFull() {
		playerStats.restoreHP(playerStats.maxHP);
	}

	public void moveToLastWorld() {
		Gdx.app.debug("LAST_WORLD", "Last world teleport activated.");
		flagWorldChange(6);
	}

	public static void doFinalFight() {
		worldEngines[6].getSystem(DialogueSystem.class).add(getCurrentMap().bossEntity);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (fontGenerator != null) {
			fontGenerator.dispose();
		}

		for (TesseractMap map : maps) {
			if (map != null) {
				map.dispose();
			}
		}

		if (openDoorTex != null) {
			openDoorTex.dispose();
		}

		if (closedDoorTex != null) {
			closedDoorTex.dispose();
		}

		for (int i = 0; i < worldSelectTexFiles.length; i++) {
			if (worldSelectTextures[i] != null) {
				worldSelectTextures[i].dispose();
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

		for (int i = 0; i < playerTextures.length; i++) {
			if (playerTextures[i] != null) {
				playerTextures[i].dispose();
			}
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

		if (musicManager != null) {
			musicManager.dispose();
		}
	}

	public class WorldSelectChangeListener implements Listener<Boolean> {
		@Override
		public void receive(Signal<Boolean> signal, Boolean object) {
			flagWorldSelectChange();
		}
	}

	public class BattleVictoryListener implements Listener<Boolean> {
		@Override
		public void receive(Signal<Boolean> signal, Boolean object) {
			Gdx.app.debug("BATTLE_END", "Battle end detected: boss = " + object);
			flagWorldReturn(object.booleanValue());
		}
	}

	public class BattleDefeatListener implements Listener<Boolean> {
		@Override
		public void receive(Signal<Boolean> signal, Boolean object) {
			Gdx.app.debug("BATTLE_DEFEAT", "Battle defeat detected :(");
			healOnTransition = true;
			flagWorldChange(0);
		}
	}

	public class DialogueFinishListener implements Listener<Entity> {
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			// Dialogue dia =
			// ComponentMapper.getFor(Dialogue.class).get(object);

			Gdx.app.debug("DIA_FINISH", "Detected finish in dialogue.");

			Enemy e = ComponentMapper.getFor(Enemy.class).get(object);
			NPC npc = ComponentMapper.getFor(NPC.class).get(object);

			if (e != null) {
				startBossBattle();
			} else if (npc != null) {
				healToFull();
			} else {
				moveToLastWorld();
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
							flagBattleChange(false);
						}
					}
				}
			}
		}
	}
}
