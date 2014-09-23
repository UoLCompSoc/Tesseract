package uk.org.ulcompsoc.tesseract.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

/**
 * <p>
 * Dynamically builds a UI box from a loaded tileset of UI elements.
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class UIBuilder implements Disposable {
	public static final int						DEFAULT_UI_TILE_WIDTH	= 32;
	public static final int						DEFAULT_UI_TILE_HEIGHT	= 32;

	private final Texture						texture;
	public final int							tileWidth;
	public final int							tileHeight;

	private final Map<GridPoint2, FrameBuffer>	owned;

	private final SpriteBatch					batch					= new SpriteBatch();

	private final GridPoint2					gpCache					= new GridPoint2();

	// u = upper, m = middle, b = bottom
	// corresponding to rows 0, 1, 2 of tile set
	// l = left, m = middle, r = right
	// corresponding to cols 0, 1, 2 of tile set
	private final TextureRegion					ul;
	private final TextureRegion					um;
	private final TextureRegion					ur;
	private final TextureRegion					ml;
	private final TextureRegion					mm;
	private final TextureRegion					mr;
	private final TextureRegion					bl;
	private final TextureRegion					bm;
	private final TextureRegion					br;

	public UIBuilder(String uiTextureLocation) {
		this(uiTextureLocation, DEFAULT_UI_TILE_WIDTH, DEFAULT_UI_TILE_HEIGHT);
	}

	public UIBuilder(String uiTextureLocation, int tileWidth, int tileHeight) {
		this.texture = new Texture(Gdx.files.internal(uiTextureLocation));
		this.owned = new HashMap<GridPoint2, FrameBuffer>();

		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		TextureRegion[][] regions = TextureRegion.split(texture, tileWidth, tileHeight);

		ul = regions[0][0];
		um = regions[0][1];
		ur = regions[0][2];

		ml = regions[1][0];
		mm = regions[1][1];
		mr = regions[1][2];

		bl = regions[2][0];
		bm = regions[2][1];
		br = regions[2][2];
	}

	public void build(int uiWidthInTiles, int uiHeightInTiles) {
		final int widthInTiles = Math.max(2, uiWidthInTiles);
		final int heightInTiles = Math.max(2, uiHeightInTiles);

		final GridPoint2 key = new GridPoint2(widthInTiles, heightInTiles);

		if (owned.containsKey(key)) {
			return;
		}

		final int expectedFBWidth = widthInTiles * tileWidth;
		final int expectedFBHeight = heightInTiles * tileHeight;

		FrameBuffer fb = new FrameBuffer(texture.getTextureData().getFormat(), expectedFBWidth, expectedFBHeight, false);

		fb.begin();
		Gdx.gl20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		OrthographicCamera camera = new OrthographicCamera();
		camera.setToOrtho(false, fb.getWidth(), fb.getHeight());

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		drawBottom(batch, widthInTiles);
		drawMiddle(batch, widthInTiles, heightInTiles - 2);
		drawTop(batch, widthInTiles, heightInTiles);

		batch.end();
		fb.end();

		// Gdx.app.debug("FB_SIZE", "Expected:\nwidth = " + expectedFBWidth +
		// ", height = " + expectedFBHeight
		// + "\ngot:\nwidth = " + fb.getWidth() + ", height = " + fb.getHeight()
		// + ".");

		owned.put(key, fb);
	}

	public UIPopup buildAndGet(int widthInTiles, int heightInTiles) {
		return getUIPopup(widthInTiles, heightInTiles);
	}

	public UIPopup buildFromActualSize(float width, float height) {
		final int widthInTiles = (int) ((width / tileWidth) + ((width % tileWidth) == 0 ? 0 : 1));
		final int heightInTiles = (int) ((height / tileHeight) + ((height % tileHeight) == 0 ? 0 : 1));

		return getUIPopup(widthInTiles, heightInTiles);
	}

	public Texture get(int widthInTiles, int heightInTiles) {
		return owned.get(gpCache.set(widthInTiles, heightInTiles)).getColorBufferTexture();
	}

	public Rectangle getRectangle(int widthInTiles, int heightInTiles) {
		FrameBuffer fb = owned.get(gpCache.set(widthInTiles, heightInTiles));
		return new Rectangle(0.0f, 0.0f, fb.getWidth(), fb.getHeight());
	}

	public UIPopup getUIPopup(int uiWidthInTiles, int uiHeightInTiles) {
		final int widthInTiles = Math.max(2, uiWidthInTiles);
		final int heightInTiles = Math.max(2, uiHeightInTiles);
		gpCache.set(widthInTiles, heightInTiles);

		if (!owned.containsKey(gpCache)) {
			build(widthInTiles, heightInTiles);
		}

		gpCache.set(widthInTiles, heightInTiles);

		Texture foundTex = owned.get(gpCache).getColorBufferTexture();
		Rectangle foundRect = getRectangle(widthInTiles, heightInTiles);
		return new UIPopup(foundTex, foundRect);
	}

	private void drawBottom(Batch target, int widthInTiles) {
		target.draw(bl, 0, 0);

		for (int i = 1; i < (widthInTiles - 1); i++) {
			target.draw(bm, i * tileWidth, 0);
		}

		target.draw(br, (widthInTiles - 1) * tileWidth, 0);
	}

	private void drawMiddle(Batch target, int widthInTiles, int heightInTiles) {
		for (int j = 1; j <= heightInTiles; j++) {
			target.draw(ml, 0, tileHeight * j);

			for (int i = 1; i < (widthInTiles - 1); i++) {
				target.draw(mm, i * tileWidth, tileHeight * j);
			}

			target.draw(mr, (widthInTiles - 1) * tileWidth, tileHeight * j);
		}
	}

	private void drawTop(Batch target, int widthInTiles, int heightInTiles) {
		final int actualY = (heightInTiles - 1) * tileHeight;
		target.draw(ul, 0, actualY);

		for (int i = 1; i < (widthInTiles - 1); i++) {
			target.draw(um, i * tileWidth, actualY);
		}

		target.draw(ur, (widthInTiles - 1) * tileWidth, actualY);
	}

	@Override
	public void dispose() {
		if (texture != null) {
			texture.dispose();
		}

		for (FrameBuffer fb : owned.values()) {
			if (fb != null) {
				fb.dispose();
			}
		}
	}
}