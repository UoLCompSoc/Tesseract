package uk.org.ulcompsoc.tesseract;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.GridPoint2;
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
	private final int							tileWidth;
	private final int							tileHeight;

	private final Map<GridPoint2, FrameBuffer>	owned;

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

	public void draw(Batch batch, Color color, float x, float y, int uiWidthInTiles, int uiHeightInTiles) {
		final int widthInTiles = Math.max(3, uiWidthInTiles);
		final int heightInTiles = Math.max(3, uiHeightInTiles);

		Color cTemp = batch.getColor();
		batch.setColor(color);

		batch.begin();

		drawBottom(batch, x, y, widthInTiles);
		drawMiddle(batch, x, y, widthInTiles, heightInTiles - 2);
		drawTop(batch, x, y, widthInTiles, heightInTiles);

		batch.end();

		batch.setColor(cTemp);
	}

	private void drawBottom(Batch target, float x, float y, int widthInTiles) {
		target.draw(bl, x, y);

		for (int i = 1; i < (widthInTiles - 1); i++) {
			target.draw(bm, x + i * tileWidth, y);
		}

		target.draw(br, (widthInTiles - 1) * tileWidth + x, y);
	}

	private void drawMiddle(Batch target, float x, float y, int widthInTiles, int heightInTiles) {
		for (int j = 1; j <= heightInTiles; j++) {
			target.draw(ml, x, tileHeight * j + y);

			for (int i = 1; i < (widthInTiles - 1); i++) {
				target.draw(mm, x + i * tileWidth, y + tileHeight * j);
			}

			target.draw(mr, x + (widthInTiles - 1) * tileWidth, y + tileHeight * j);
		}
	}

	private void drawTop(Batch target, float x, float y, int widthInTiles, int heightInTiles) {
		final int actualY = (heightInTiles - 1) * tileHeight;
		target.draw(ul, x, y + actualY);

		for (int i = 1; i < (widthInTiles - 1); i++) {
			target.draw(um, x + i * tileWidth, y + actualY);
		}

		target.draw(ur, (widthInTiles - 1) * tileWidth + x, y + actualY);
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
