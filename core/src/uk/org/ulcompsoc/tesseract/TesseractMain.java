package uk.org.ulcompsoc.tesseract;

import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.systems.RenderSystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TesseractMain extends ApplicationAdapter {
	private SpriteBatch	batch			= null;
	private Camera		camera			= null;

	private Engine		engine			= null;

	private Texture		playerTexture	= null;
	private Entity		playerEntity	= null;

	private Texture		slimeTexture	= null;
	private Entity		slimeEntity		= null;

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

		engine = new Engine();

		playerTexture = new Texture(Gdx.files.local("basicPlayer.png"));
		TextureRegion[] playerRegions = TextureRegion.split(playerTexture, WorldConstants.TILE_WIDTH,
				WorldConstants.TILE_HEIGHT)[0];
		playerEntity = new Entity();

		playerEntity.add(new Position(17, 10));
		playerEntity.add(new Renderable(playerRegions[1], playerRegions[0], playerRegions[3], playerRegions[2]));

		slimeTexture = new Texture(Gdx.files.local("greenSlime.png"));
		TextureRegion slimeRegion = TextureRegion.split(slimeTexture, WorldConstants.TILE_WIDTH,
				WorldConstants.TILE_HEIGHT)[0][0];
		slimeEntity = new Entity();
		slimeEntity.add(new Position(3, 10)).add(new Renderable(slimeRegion));

		engine.addEntity(playerEntity);
		engine.addEntity(slimeEntity);

		engine.addSystem(new RenderSystem(batch, camera, 100));
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
	}
}
