package uk.org.ulcompsoc.tesseract;

import java.util.Random;

import uk.org.ulcompsoc.tesseract.animations.PingPongFrameResolver;
import uk.org.ulcompsoc.tesseract.animations.PlayerAnimationFrameResolver;
import uk.org.ulcompsoc.tesseract.animations.SlimeFrameResolver;
import uk.org.ulcompsoc.tesseract.audio.MusicManager;
import uk.org.ulcompsoc.tesseract.audio.SilentMusicManager;
import uk.org.ulcompsoc.tesseract.audio.StandardMusicManager;
import uk.org.ulcompsoc.tesseract.battle.BattlePerformers;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Boss;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.Dimension;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.FocusTaker;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Movable;
import uk.org.ulcompsoc.tesseract.components.Named;
import uk.org.ulcompsoc.tesseract.components.Player;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Renderable.Facing;
import uk.org.ulcompsoc.tesseract.components.Stats;
import uk.org.ulcompsoc.tesseract.components.Text;
import uk.org.ulcompsoc.tesseract.components.WorldPlayerInputListener;
import uk.org.ulcompsoc.tesseract.dialoguelisteners.BossBattleDialogueFinishListener;
import uk.org.ulcompsoc.tesseract.dialoguelisteners.FullHealDialogueFinishListener;
import uk.org.ulcompsoc.tesseract.dialoguelisteners.WorldWarpDialogueFinishListener;
import uk.org.ulcompsoc.tesseract.fonts.FontResolver;
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
import uk.org.ulcompsoc.tesseract.ui.UIBuilder;

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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;

public class TesseractMain extends ApplicationAdapter {
	public static final String			PLAYER_NAME						= "Valiant Hero™";
	public static final float			BATTLE_END_TRANSITION_TIME		= 4.0f;
	public static final float			BATTLE_START_TRANSITION_TIME	= 2.0f;
	public static final float			WORLD_SELECT_LOAD_TIME			= 0.25f;
	public static final float			WORLD_SELECT_CHANGE_TIME		= 1.0f;

	private Random						random							= null;

	private static SpriteBatch			batch							= null;
	private static ShapeRenderer		shapeRenderer					= null;
	private static Camera				camera							= null;

	public static MusicManager			musicManager					= null;

	private UIBuilder					uiBuilder						= null;

	private static boolean				useShader						= false;
	private static ShaderProgram		vortexProgram					= null;

	private FontResolver				fontResolver					= null;
	private BitmapFont					font10							= null;
	private BitmapFont					font12							= null;
	private BitmapFont					font16							= null;
	private BitmapFont					font24							= null;

	private Engine						currentEngine					= null;

	private static Engine				battleEngine					= null;
	private static Engine				worldSelectEngine				= null;
	private static Engine[]				worldEngines					= null;

	public static Entity				battlePlayerEntity				= null;
	public static Entity				worldPlayerEntity				= null;
	public static Stats					playerStats						= null;

	private BattleVictoryListener		battleVictoryListener			= null;
	private BattleDefeatListener		battleDefeatListener			= null;

	private static boolean				bossIncomingFlag				= false;
	private static boolean				battleChangeFlag				= false;
	private static boolean				worldChangeFlag					= false;

	private boolean						healOnTransition				= false;
	private static boolean				worldSelectChangeFlag			= false;
	private static int					diffWorldFlag					= -1;
	private static float				transitionTime					= -1.0f;

	private Texture[]					playerTextures					= null;
	private Animation[]					playerIdleAnims					= null;
	private Animation[]					playerUpAnims					= null;
	private Animation[]					playerDownAnims					= null;
	private Animation[]					playerLeftAnims					= null;
	private Animation[]					playerRightAnims				= null;
	private Animation					playerIdle						= null;
	private Animation					playerUp						= null;
	private Animation					playerDown						= null;
	private Animation					playerLeft						= null;
	private Animation					playerRight						= null;

	private Texture						slimeDesat						= null;
	private Animation					slimeDesatAnim					= null;

	private Texture[]					torchTextures					= null;
	private Animation[]					torchAnims						= null;

	private Texture[]					bossTextures					= null;
	private Animation[]					bossAnims						= null;
	private Stats[]						bossStats						= { new Stats(20, 15, 10, 3),
			new Stats(100, 15, 10, 1), new Stats(300, 15, 10, 5), new Stats(200, 20, 10, 5), new Stats(150, 20, 10, 2),
			new Stats(200, 20, 10, 5), new Stats(350, 25, 5, 2)		};

	private Texture[]					worldSelectTextures				= null;

	private Texture						openDoorTex						= null;
	private Texture						closedDoorTex					= null;

	private WorldSelectChangeListener	worldSelectChangeListener		= null;

	private MonsterTileHandler			monsterTileHandler				= null;

	private Entity						statusDialog					= null;
	private Entity[]					menuDialogs						= null;
	private Entity[]					menuTexts						= null;
	private Entity						hpText							= null;
	private Entity						rageText						= null;

	public static final String[]		playerFiles						= { "player/player_0.png",
			"player/player_1.png", "player/player_2.png", "player/player_3.png", "player/player_4.png",
			"player/player_5.png",										};

	public static final String[]		mapNames						= { "world1", "world2", "world3", "world4",
			"world5", "world6", "world7"								};

	public static final String[]		slimeFiles						= { "monsters/world1_slime.png",
			"monsters/world2_slime.png", "monsters/world3_slime.png", "monsters/world4_slime.png",
			"monsters/world5_slime.png", "monsters/world6_slime.png"	};

	public static final String[]		torchFiles						= { "torches/world1_torches.png",
			"torches/world2_torches.png", "torches/world3_torches.png", "torches/world4_torches.png",
			"torches/world5_torches.png", "torches/world6_torches.png"	};

	public static final String[]		bossFiles						= { "bosses/final_boss.png",
			"bosses/world2_boss.png", "bosses/world3_boss.png", "bosses/world4_boss.png", "bosses/world5_boss.png",
			"bosses/world6_boss.png", "bosses/final_boss.png"			};

	public static final GridPoint2[]	bossSizes						= { new GridPoint2(128, 128),
			new GridPoint2(64, 64), new GridPoint2(64, 64), new GridPoint2(64, 64), new GridPoint2(64, 64),
			new GridPoint2(64, 64), new GridPoint2(128, 128)			};

	public static final String[]		worldSelectTexFiles				= { "worldtextures/world1_64.png",
			"worldtextures/world2_64.png", "worldtextures/world3_64.png", "worldtextures/world4_64.png",
			"worldtextures/world5_64.png", "worldtextures/world6_64.png" };

	public static final String[]		musicFiles						= { "music/world1.ogg", "music/world2.ogg",
			"music/world3.ogg", "music/world4.ogg", "music/world5.ogg", "music/world6.ogg", "music/world7.ogg" };

	private static TesseractMap[]		maps							= null;
	public static int					currentMapIndex					= 0;

	@SuppressWarnings("unused")
	private GameState					gameState						= null;

	public TesseractMain(FontResolver fontResolver) {
		this(fontResolver, Difficulty.EASY, false, false);
	}

	public TesseractMain(FontResolver fontResolver, Difficulty diff, boolean debug, boolean silent) {
		this.fontResolver = fontResolver;
		WorldConstants.DEBUG = debug;
		WorldConstants.DIFFICULTY = diff;
		WorldConstants.SILENT = silent;
	}

	@Override
	public void create() {
		if (WorldConstants.DEBUG) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}

		ShaderProgram.pedantic = true;

		random = new Random();

		camera = new OrthographicCamera();
		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		musicManager = (!WorldConstants.SILENT ? new StandardMusicManager(musicFiles) : new SilentMusicManager());

		uiBuilder = new UIBuilder("ui/ui.png", 32, 32);
		uiBuilder.build(2, 2);

		loadShader();

		font10 = fontResolver.resolve(10);
		font12 = fontResolver.resolve(12);
		font16 = fontResolver.resolve(16);
		font24 = fontResolver.resolve(24);

		loadPlayerFiles();

		loadSlimeFiles();

		loadBossFiles();

		openDoorTex = new Texture(Gdx.files.internal("door_open.png"));
		closedDoorTex = new Texture(Gdx.files.internal("door_closed.png"));

		playerStats = new Stats(100, 30, 5, 70);

		monsterTileHandler = new MonsterTileHandler();
		battleVictoryListener = new BattleVictoryListener();
		battleDefeatListener = new BattleDefeatListener();

		worldSelectChangeListener = new WorldSelectChangeListener();

		battleEngine = new Engine();
		initBattleEngine(battleEngine);

		worldSelectEngine = new Engine();
		initWorldSelectEngine(worldSelectEngine);

		// resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		worldEngines = new Engine[mapNames.length];
		initWorldEngines(worldEngines);

		changeToWorld(0);
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		Color mapColor = getCurrentMap().color;

		Gdx.gl.glClearColor(mapColor.r, mapColor.g, mapColor.b, mapColor.a);
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
			} else if (Gdx.input.isKeyJustPressed(Keys.O)) {
				Gdx.app.debug("PLAYER_ANIM_JSON", new Json().prettyPrint(playerIdle));
			} else if (Gdx.input.isKeyJustPressed(Keys.F9)) {
				TesseractDebug.debugAllInEngine(currentEngine);
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

		if (Gdx.input.isKeyPressed(Keys.HOME)) {
			Vector2 pos = Mappers.position.get(worldPlayerEntity).position;

			float oldZoom = ((OrthographicCamera) camera).zoom;
			((OrthographicCamera) camera).zoom = 1.0f;
			Color temp = batch.getColor();
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			batch.setColor(Color.NAVY);
			batch.draw(uiBuilder.get(2, 2), pos.x, pos.y + 32);
			batch.end();
			batch.setColor(temp);
			((OrthographicCamera) camera).zoom = oldZoom;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void resize(int width, int height) {
		final float xScale = width / camera.viewportWidth;
		final float yScale = height / camera.viewportHeight;

		Gdx.app.debug("RESIZE", "Resize: xScale = " + xScale + ", yScale = " + yScale + ".");

		final Family posFamily = Family.getFor(Position.class);

		((OrthographicCamera) camera).setToOrtho(false, width, height);

		if (battleEngine != null) {
			// this handleResize is a dirty hack
			battleEngine.getSystem(BattleMessageSystem.class).handleResize(xScale, yScale);
			ImmutableArray<Entity> ents = battleEngine.getEntitiesFor(posFamily);

			for (int i = 0; i < ents.size(); i++) {
				Entity e = ents.get(i);

				Mappers.position.get(e).handleResize(xScale, yScale);

				if (Mappers.dimension.has(e)) {
					Dimension d = Mappers.dimension.get(e);

					d.width *= xScale;
					d.height *= yScale;
				}
			}
		}

		if (worldSelectEngine != null) {
			ImmutableArray<Entity> ents = worldSelectEngine.getEntitiesFor(posFamily);

			for (int i = 0; i < ents.size(); i++) {
				Entity e = ents.get(i);

				Mappers.position.get(e).handleResize(xScale, yScale);

				if (Mappers.dimension.has(e)) {
					Dimension d = Mappers.dimension.get(e);

					d.width *= xScale;
					d.height *= yScale;
				}
			}
		}

		worldSelectEngine = new Engine();
		initWorldSelectEngine(worldSelectEngine);
	}

	public static void flagBattleChange(boolean boss) {
		battleChangeFlag = true;
		bossIncomingFlag = boss;
		transitionTime = BATTLE_START_TRANSITION_TIME;
		vortexOn();
	}

	public static void flagWorldChange(int newWorld) {
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

	@SuppressWarnings("unchecked")
	public void changeToBattle(boolean boss) {
		Gdx.app.debug("BATTLE_CHANGE", "Changing to battle view.");

		this.currentEngine = battleEngine;

		ImmutableArray<Entity> ents = currentEngine.getEntitiesFor(Family.getFor(Enemy.class));
		if (ents.size() > 0) {
			while (ents.size() > 0) {
				currentEngine.removeEntity(ents.get(0));
			}

			makeBattlePlayerEntity(currentEngine);
		}

		camera.position.set(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, 0.0f);
		((OrthographicCamera) camera).zoom = 1.0f;
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

			if (currentMapIndex != -1 && diffWorld != currentMapIndex) {
				worldEngines[currentMapIndex].removeEntity(worldPlayerEntity);
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

		((OrthographicCamera) camera).zoom = 0.5f;
		camera.update();
	}

	public void changeToWorldSelect() {
		this.currentEngine = worldSelectEngine;

		camera.position.set(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, 0.0f);
		((OrthographicCamera) camera).zoom = 1.0f;
		camera.update();
	}

	private void initWorldEngines(Engine[] engines) {
		maps = new TesseractMap[mapNames.length];

		currentMapIndex = -1;

		worldPlayerEntity = new Entity();
		worldPlayerEntity.add(new Renderable(Facing.IDLE, playerIdle, playerUp, playerDown, playerLeft, playerRight,
				new PlayerAnimationFrameResolver(false)).setPrioritity(50));
		worldPlayerEntity.add(new FocusTaker(camera));
		worldPlayerEntity.add(new Player(PLAYER_NAME));
		worldPlayerEntity.add(new WorldPlayerInputListener());

		worldPlayerEntity.componentAdded.add(monsterTileHandler.movingAddListener);
		worldPlayerEntity.componentRemoved.add(monsterTileHandler.movingRemoveListener);
		worldPlayerEntity.add(new Movable());
		worldPlayerEntity.add(new Named("WorldPlayer"));

		worldPlayerEntity.add(playerStats);

		for (int i = 0; i < mapNames.length; i++) {
			Engine engine = new Engine();

			maps[i] = new TesseractMap("maps/", mapNames[i], batch, new TextureRegion(openDoorTex), new TextureRegion(
					closedDoorTex), new FullHealDialogueFinishListener(), new BossBattleDialogueFinishListener(),
					new WorldWarpDialogueFinishListener(6));

			engine.addEntity(maps[i].baseLayerEntity);

			if (maps[i].jsonEntites != null) {
				for (Entity e : maps[i].jsonEntites) {
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
			engine.addSystem(new RenderSystem(batch, shapeRenderer, camera, 1000));
			engine.addSystem(new DialogueSystem(camera, batch, font10, 2000));

			engines[i] = engine;
		}

		currentEngine = worldEngines[0]; // DIRTY HACK
	}

	private void initBattleEngine(Engine engine) {
		makeBattlePlayerEntity(engine);

		Rectangle screenRect = new Rectangle(0.0f, 0.0f, camera.viewportWidth, camera.viewportHeight);

		final float statusX = 0.0f;
		final float statusY = 0.0f;
		statusDialog = new Entity();
		statusDialog.add(new Position(statusX, statusY));
		statusDialog.add(new Dimension(13 * uiBuilder.tileWidth, 4 * uiBuilder.tileHeight));

		Dimension statusDim = Mappers.dimension.get(statusDialog);
		final Rectangle statusRect = new Rectangle(statusX, statusY, statusDim.width, statusDim.height);

		statusDialog.add(new BattleDialog(uiBuilder.buildAndGet(13, 4)));

		hpText = new Entity();
		Text hpTextComponent = new Text("HP: 100/100", Color.WHITE, playerStats.hpChangeSignal);
		hpText.add(new Position(0.0f, statusRect.height * 0.8f).smartCentreX(
				Text.getTextWidth(hpTextComponent, statusRect.width, font16), statusRect));
		hpTextComponent.baseText = "HP: ";
		hpText.add(hpTextComponent);
		healToFull();

		rageText = new Entity();
		Text rageTextComponent = new Text("Rage Level: Really mad.");
		rageText.add(new Position(0.0f, statusRect.height * 0.4f).smartCentreX(
				Text.getTextWidth(rageTextComponent, statusRect.width, font16), statusRect));
		rageText.add(rageTextComponent);

		final int menuDialogCount = 4;
		final String[] dialogStrings = { "Attack", "Defend", "Quaff", "Flee" };
		final Position[] menuPositions = new Position[dialogStrings.length];

		final float menuW = uiBuilder.tileWidth * 3;
		final float menuH = uiBuilder.tileHeight * 2;

		menuPositions[3] = new Position().bottomRightOf(screenRect, menuW);
		menuPositions[2] = new Position().left(menuPositions[3], menuW);

		menuPositions[0] = new Position().above(menuPositions[2], menuH);
		menuPositions[1] = new Position().above(menuPositions[3], menuH);

		menuDialogs = new Entity[menuDialogCount];
		menuTexts = new Entity[menuDialogCount];

		for (int i = 0; i < menuDialogCount; i++) {
			menuDialogs[i] = new Entity();

			final String thisString = dialogStrings[i];

			menuTexts[i] = new Entity();
			Text text = new Text(thisString);
			menuTexts[i].add(text);

			menuDialogs[i].add(menuPositions[i]);
			BattleDialog menuDialog = new BattleDialog(uiBuilder.buildFromActualSize(menuW, menuH));
			menuDialogs[i].add(menuDialog);
			menuDialogs[i].add(new Dimension(menuW, menuH));
			menuDialogs[i].add(new MouseClickListener(BattlePerformers.performers[i]));

			menuTexts[i].add(new Position().smartCentre(Text.getTextWidth(text, menuW, font24), Text.getTextHeight(
					text, menuW, font24), new Rectangle(menuPositions[i].position.x, menuPositions[i].position.y,
					menuW, menuH)));

		}

		engine.addEntity(statusDialog);
		for (int i = 0; i < menuDialogs.length; i++) {
			engine.addEntity(menuDialogs[i]);
			engine.addEntity(menuTexts[i]);
		}
		engine.addEntity(hpText);
		engine.addEntity(rageText);

		Entity battleMessageEntity = new Entity();
		Dimension bmeDim = Dimension.relativeDimension(screenRect, 0.7f, 0.1f);
		BattleDialog battleMessageEntityDialog = new BattleDialog(uiBuilder.buildFromActualSize((int) bmeDim.width,
				(int) bmeDim.height));

		bmeDim.width = battleMessageEntityDialog.popup.innerRectangle.width;
		bmeDim.height = battleMessageEntityDialog.popup.innerRectangle.height;

		battleMessageEntity.add(bmeDim);

		battleMessageEntity.add(battleMessageEntityDialog);
		battleMessageEntity.add(new Position(0.0f, screenRect.height * 0.85f).smartCentreX(
				Mappers.dimension.get(battleMessageEntity).width, screenRect));

		BattleMessageSystem battleMessageSystem = new BattleMessageSystem(battleMessageEntity, shapeRenderer, font24,
				camera, 300);
		BattleAttackSystem battleAttackSystem = new BattleAttackSystem(battleMessageSystem, 200);
		BattlePerformers.battleAttackSystem = battleAttackSystem;
		BattlePerformers.battleMessageSystem = battleMessageSystem;

		engine.addSystem(new BuffSystem(50));
		engine.addSystem(new BattleAISystem(75));
		engine.addSystem(new BattleInputSystem(camera, 100));
		engine.addSystem(battleAttackSystem.addVictoryListener(battleVictoryListener).addDefeatListener(
				battleDefeatListener));
		engine.addSystem(battleMessageSystem);
		engine.addSystem(new RenderSystem(batch, shapeRenderer, camera, 1000));
		engine.addSystem(new BattleDialogRenderSystem(batch, camera, 2000));
		engine.addSystem(new TextRenderSystem(batch, font16, 3000));
	}

	private void makeBattlePlayerEntity(Engine engine) {
		battlePlayerEntity = new Entity();

		Player playerComp = new Player(PLAYER_NAME);
		battlePlayerEntity.add(playerComp);

		// dirty hack with positioning here
		battlePlayerEntity.add(new Position(0.85f * camera.viewportWidth, 0.0f).smartCentreY(
				WorldConstants.TILE_HEIGHT, 0.2f * camera.viewportHeight, 0.8f * camera.viewportHeight));
		battlePlayerEntity.add(getBattlePlayerPowerLevelRenderable());
		battlePlayerEntity.add(playerStats);

		Combatant playerCombatant = new Combatant();
		battlePlayerEntity.add(playerCombatant);
		battlePlayerEntity.add(new Named(playerComp.name));

		engine.addEntity(battlePlayerEntity);
	}

	private void initWorldSelectEngine(Engine engine) {
		worldSelectTextures = new Texture[worldSelectTexFiles.length];

		for (int i = 0; i < worldSelectTexFiles.length; i++) {
			worldSelectTextures[i] = new Texture(Gdx.files.internal(worldSelectTexFiles[i]));
		}

		final int texWidthInTiles = worldSelectTextures[0].getWidth() / WorldConstants.TILE_WIDTH;
		final int widthPadding = 5;

		final int screenWidthInTiles = (int) (camera.viewportWidth / WorldConstants.TILE_WIDTH) - (widthPadding * 2)
				- texWidthInTiles;
		final int screenHeightInTiles = (int) (camera.viewportHeight / WorldConstants.TILE_HEIGHT);

		final int numCols = 3;
		final int squaresPerRow = worldSelectTexFiles.length / (numCols);

		int xIncr = (int) ((1.0f / squaresPerRow) * (screenWidthInTiles));
		int yIncr = (int) ((1.0f / numCols) * screenHeightInTiles);

		int gridX = 5;
		int gridY = 0;

		for (int i = 0; i < worldSelectTexFiles.length; i++) {
			if (i % numCols == 0) {
				gridY += yIncr;
				gridX = 5;
			}

			Entity e = new Entity();
			Position p = new Position().setFromGrid(gridX, gridY);
			Renderable r = new Renderable(new TextureRegion(worldSelectTextures[i]));

			gridX += xIncr;

			e.add(p);
			e.add(new Dimension(r.width, r.height));
			e.add(r);
			e.add(new Named("" + i));
			e.add(new MouseClickListener(new MouseClickPerformer() {
				@Override
				public void perform(Entity invoker, Engine engine) {
					Integer i = Integer.parseInt(Mappers.named.get(invoker).name);
					flagWorldChange(i.intValue());
				}
			}));

			engine.addEntity(e);
		}

		engine.addSystem(new BattleInputSystem(camera, 50));
		engine.addSystem(new RenderSystem(batch, shapeRenderer, camera, 1000));
	}

	private void addBoss(Engine engine) {
		TesseractMap map = getCurrentMap();

		if (map.bossBeaten) {
			Gdx.app.debug("ADD_BOSS", "Trying to fight a boss for the second time.");
			return;
		}

		Entity boss = new Entity();
		boss.add(new Position(0.15f * camera.viewportWidth, 0.0f).centreY(new Rectangle(0.0f,
				0.1f * camera.viewportHeight, camera.viewportWidth, camera.viewportHeight * 0.9f)));
		boss.add(new Renderable(bossAnims[currentMapIndex]).setAnimationResolver(new PingPongFrameResolver(0.1f)));
		boss.add(bossStats[currentMapIndex]);
		boss.add(new Boss());
		boss.add(new Combatant().setThinkingTime(bossStats[currentMapIndex].getThinkTime() / 2.0f));
		Enemy enemy = Mappers.enemy.get(map.bossEntity);
		boss.add(enemy);
		boss.add(new Named(enemy.speciesName));

		engine.addEntity(boss);
	}

	private void addSlimes(Engine engine, int count) {
		if (count < 1 || count > 3) {
			throw new GdxRuntimeException("Only between 1-3 slimes supported.");
		}

		final float fartherX = 0.15f * camera.viewportWidth;
		final float closerX = 0.10f * camera.viewportWidth;

		final float slimeH = slimeDesat.getHeight();

		// dirty hack with positioning here
		final float screenY = camera.viewportHeight * 0.2f;
		final float screenH = camera.viewportHeight * 0.8f;

		Position[] positions = null;

		if (count == 1) {
			positions = new Position[1];

			positions[0] = new Position(fartherX, 0.0f).smartCentreY(slimeH, screenY, screenH);
		} else if (count == 2) {
			positions = new Position[2];

			positions[0] = new Position(fartherX, 0.0f).smartCentreY(slimeH, screenY, screenH).adjustY(slimeH * 3.0f);

			positions[1] = new Position(fartherX, 0.0f).smartCentreY(slimeH, screenY, screenH).adjustY(slimeH * -3.0f);
		} else if (count == 3) {
			positions = new Position[3];

			positions[0] = new Position(fartherX, 0.0f).smartCentreY(slimeH, screenY, screenH);

			positions[1] = new Position(closerX, 0.0f).smartCentreY(slimeH, screenY, screenH).adjustY(slimeH * 3.0f);

			positions[2] = new Position(closerX, 0.0f).smartCentreY(slimeH, screenY, screenH).adjustY(slimeH * -3.0f);

		}
		Gdx.app.debug("PLAYER_BATTLE_POS", "x = " + Mappers.position.get(battlePlayerEntity).position.x + ", y = "
				+ Mappers.position.get(battlePlayerEntity).position.y + ".");
		Gdx.app.debug("SLIME_POS", "Slime positioned at x = " + positions[0].position.x + ", y = "
				+ positions[0].position.y + ".");

		for (int i = 0; i < count; i++) {
			Entity slimeEntity = new Entity();
			slimeEntity.add(positions[i]).add(
					new Renderable(slimeDesatAnim, getCurrentMap().color).setPrioritity(50).setAnimationResolver(
							new SlimeFrameResolver()));
			Stats slimeStats = new Stats(50 + 10 * Mappers.player.get(battlePlayerEntity).powerLevel, 10, 10,
					5 + (random.nextInt(3) + 1) * 5);
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

		String log = vortexProgram.getLog();
		if (log.length() > 0) {
			Gdx.app.debug("LOAD_SHADER", "Vortex shader compilation produced following log:\n" + log);
		}

		if (!vortexProgram.isCompiled()) {
			useShader = false;
			Gdx.app.log("LOAD_SHADER", "Vortex shader compilation failed");
		} else {
			useShader = true;
		}

		vortexOff();
	}

	public static void vortexOn() {
		if (useShader) {
			batch.setShader(vortexProgram);
			vortexProgram.begin();
			vortexProgram.setUniformf("vortexFlag", 1.0f);
			vortexProgram.setUniformf("transitionTime", transitionTime);
			vortexProgram.setUniformf("iResolution", new Vector2(camera.viewportWidth, camera.viewportHeight));
			vortexProgram.end();
		}
	}

	public static void vortexOff() {
		if (useShader) {
			vortexProgram.begin();
			vortexProgram.setUniformf("vortexFlag", 0.0f);
			vortexProgram.end();
			batch.setShader(null);
		}
	}

	public static TesseractMap getCurrentMap() {
		return maps[currentMapIndex];
	}

	public void loadPlayerFiles() {
		playerTextures = new Texture[playerFiles.length];
		playerIdleAnims = new Animation[playerFiles.length];
		playerUpAnims = new Animation[playerFiles.length];
		playerDownAnims = new Animation[playerFiles.length];
		playerLeftAnims = new Animation[playerFiles.length];
		playerRightAnims = new Animation[playerFiles.length];

		final float playerTimePerFrame = 0.3f;

		for (int i = 0; i < playerFiles.length; i++) {
			playerTextures[i] = new Texture(Gdx.files.internal(playerFiles[i]));

			TextureRegion[][] playerRegions = TextureRegion.split(playerTextures[i], WorldConstants.TILE_WIDTH,
					WorldConstants.TILE_HEIGHT);

			Animation idle = (!playerFiles[i].equals("player_4.png") ? new Animation(playerTimePerFrame,
					playerRegions[0][0], playerRegions[0][1], playerRegions[0][2], playerRegions[0][3])
					: new Animation(playerTimePerFrame, playerRegions[0][0], playerRegions[0][1], playerRegions[0][2],
							playerRegions[0][3], playerRegions[0][4]));

			TextureRegion[] downArray = new TextureRegion[4];
			System.arraycopy(playerRegions[1], 0, downArray, 0, 4);

			TextureRegion[] upArray = new TextureRegion[4];
			System.arraycopy(playerRegions[2], 0, upArray, 0, 4);

			TextureRegion[] rightArray = new TextureRegion[4];
			System.arraycopy(playerRegions[3], 0, rightArray, 0, 4);

			TextureRegion[] leftArray = new TextureRegion[4];
			System.arraycopy(playerRegions[4], 0, leftArray, 0, 4);

			Animation down = new Animation(playerTimePerFrame, new Array<TextureRegion>(downArray));
			Animation up = new Animation(playerTimePerFrame, new Array<TextureRegion>(upArray));
			Animation right = new Animation(playerTimePerFrame, new Array<TextureRegion>(rightArray));
			Animation left = new Animation(playerTimePerFrame, new Array<TextureRegion>(leftArray));

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
	}

	private Renderable getWorldPlayerPowerLevelRenderable(Facing facing) {
		final int playerPowerLevel = Mappers.player.get(battlePlayerEntity).powerLevel;

		playerIdle = playerIdleAnims[playerPowerLevel];
		playerUp = playerUpAnims[playerPowerLevel];
		playerDown = playerDownAnims[playerPowerLevel];
		playerLeft = playerLeftAnims[playerPowerLevel];
		playerRight = playerRightAnims[playerPowerLevel];

		return new Renderable(facing, playerIdle, playerUp, playerDown, playerLeft, playerRight,
				new PlayerAnimationFrameResolver(false)).setPrioritity(50);
	}

	private Renderable getBattlePlayerPowerLevelRenderable() {
		return getWorldPlayerPowerLevelRenderable(Facing.IDLE).setPrioritity(50).setAnimationResolver(
				new PlayerAnimationFrameResolver(true, 0.17f));
	}

	public void doPlayerPowerUp() {
		Player playerComp = Mappers.player.get(battlePlayerEntity);
		playerComp.powerLevel++;

		playerStats.addExperience(10);

		if (playerComp.powerLevel >= playerFiles.length) {
			playerComp.powerLevel = playerFiles.length - 1;
		} else {
			Facing f = Mappers.renderable.get(worldPlayerEntity).facing;

			worldPlayerEntity.add(getWorldPlayerPowerLevelRenderable(f));
			battlePlayerEntity.add(getBattlePlayerPowerLevelRenderable());
		}
	}

	public void loadSlimeFiles() {
		slimeDesat = new Texture(Gdx.files.internal("monsters/slime_desat.png"));
		TextureRegion[] desatRegions = TextureRegion.split(slimeDesat, WorldConstants.TILE_WIDTH,
				WorldConstants.TILE_HEIGHT)[0];
		slimeDesatAnim = new Animation(0.75f, desatRegions[0], desatRegions[1]);
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

		if (fontResolver != null) {
			fontResolver.dispose();
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

		if (torchTextures != null) {
			for (int i = 0; i < torchTextures.length; i++) {
				if (torchTextures[i] != null) {
					torchAnims[i] = null;
					torchTextures[i].dispose();
				}
			}
		}

		slimeDesatAnim = null;
		if (slimeDesat != null) {
			slimeDesat.dispose();
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
}
