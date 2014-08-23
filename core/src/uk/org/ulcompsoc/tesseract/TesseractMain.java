package uk.org.ulcompsoc.tesseract;

import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.RelativePosition;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Stats;
import uk.org.ulcompsoc.tesseract.components.Text;
import uk.org.ulcompsoc.tesseract.systems.BattleDialogRenderSystem;
import uk.org.ulcompsoc.tesseract.systems.RenderSystem;
import uk.org.ulcompsoc.tesseract.systems.TextRenderSystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;

public class TesseractMain extends ApplicationAdapter {
	private SpriteBatch				batch			= null;
	private Camera					camera			= null;

	private Engine					engine			= null;

	private Texture					playerTexture	= null;
	private Entity					playerEntity	= null;

	private Texture					slimeTexture	= null;
	private Entity					slimeEntity		= null;

	private Entity					statusDialog	= null;
	private Entity					menuDialog		= null;
	private Entity					hpText			= null;

	private FreeTypeFontGenerator	fontGenerator	= null;
	private BitmapFont				font			= null;

	private GameState				gameState		= null;

	public TesseractMain() {
		this(false);
	}

	public TesseractMain(boolean debug) {
		WorldConstants.DEBUG = debug;
	}

	@Override
	public void create() {
		if (WorldConstants.DEBUG) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}

		Gdx.app.debug("TILE_INFO", "Window size in tiles is (x, y) = ("
				+ (Gdx.graphics.getWidth() / WorldConstants.TILE_WIDTH) + ", "
				+ (Gdx.graphics.getHeight() / WorldConstants.TILE_HEIGHT) + ").");

		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		((OrthographicCamera) camera).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoRegular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 12;
		font = fontGenerator.generateFont(parameter);

		engine = new Engine();

		final int yTile = 12;

		playerTexture = new Texture(Gdx.files.local("player/basicPlayer.png"));
		TextureRegion[] playerRegions = TextureRegion.split(playerTexture, WorldConstants.TILE_WIDTH,
				WorldConstants.TILE_HEIGHT)[0];
		playerEntity = new Entity();

		playerEntity.add(new Position(17, yTile));
		playerEntity.add(new Renderable(playerRegions[1], playerRegions[0], playerRegions[3], playerRegions[2]));
		playerEntity.add(new Stats(100, 4, 4));

		slimeTexture = new Texture(Gdx.files.local("monsters/greenSlime.png"));
		TextureRegion slimeRegion = TextureRegion.split(slimeTexture, WorldConstants.TILE_WIDTH,
				WorldConstants.TILE_HEIGHT)[0][0];
		slimeEntity = new Entity();
		slimeEntity.add(new Position(3, yTile)).add(new Renderable(slimeRegion));
		slimeEntity.add(new Stats(50, 2, 2));

		Rectangle screenRect = new Rectangle(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		statusDialog = new Entity();
		statusDialog.add(new RelativePosition(new Rectangle(0.0f, 0.0f, 0.3f, 0.2f), screenRect));
		statusDialog.add(new BattleDialog(Color.BLUE, Color.DARK_GRAY));

		menuDialog = new Entity();
		menuDialog.add(new RelativePosition(new Rectangle(0.3f, 0.0f, 0.7f, 0.2f), screenRect));
		menuDialog.add(new BattleDialog(Color.NAVY, Color.DARK_GRAY));

		hpText = new Entity();
		Text hpTextString = new Text("HP: 100/100");
		// hpText.add(new RelativePosition(new Rectangle(0.25f, 0.8f, 0.0f,
		// 0.0f), statusDialog));
		hpText.add(RelativePosition.makeCentred(Text.getTextRectangle(0.0f, 0.75f, hpTextString, font), statusDialog));
		hpText.add(hpTextString);
		Gdx.app.debug("TEXT_WIDTH", "HP Text width = " + Text.getTextWidth(hpTextString, font) + ".");

		engine.addEntity(playerEntity);
		engine.addEntity(slimeEntity);
		engine.addEntity(statusDialog);
		engine.addEntity(menuDialog);
		engine.addEntity(hpText);

		engine.addSystem(new RenderSystem(batch, camera, 1000));
		engine.addSystem(new BattleDialogRenderSystem(camera, 2000));
		engine.addSystem(new TextRenderSystem(batch, font, 3000));

		gameState = GameState.BATTLE;
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClearColor(0.0f, 0.8f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		engine.update(deltaTime);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (playerTexture != null) {
			playerTexture.dispose();
		}

		if (slimeTexture != null) {
			slimeTexture.dispose();
		}

		if (fontGenerator != null) {
			fontGenerator.dispose();
		}
	}
}
