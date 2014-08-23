package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Renderable extends Component {
	public enum RenderType {
		UDLR_SPRITE, STATIC_SPRITE, ANIM_SPRITE_LOOP, TILED, NONE;
	}

	public RenderType			renderType		= RenderType.NONE;

	private TextureRegion		current			= null;

	// not the component's responsibility to dispose(), should be done in class
	// where it's loaded
	public TextureRegion		upTexture		= null;
	public TextureRegion		downTexture		= null;
	public TextureRegion		leftTexture		= null;
	public TextureRegion		rightTexture	= null;

	public Animation			animation		= null;
	public float				animTime		= 0.0f;

	public TiledMapTileLayer[]	layers			= null;
	public TiledMapRenderer		tiledRenderer	= null;

	public Renderable(TextureRegion upTexture, TextureRegion downTexture, TextureRegion leftTexture,
			TextureRegion rightTexture) {
		this.upTexture = upTexture;
		this.downTexture = downTexture;
		this.leftTexture = leftTexture;
		this.rightTexture = rightTexture;

		this.current = downTexture;

		this.renderType = RenderType.UDLR_SPRITE;
	}

	public Renderable(TextureRegion sprite) {
		this.renderType = RenderType.STATIC_SPRITE;
		this.current = sprite;
		this.downTexture = sprite;
	}

	public Renderable(Animation animation) {
		this.animation = animation;
		this.renderType = RenderType.ANIM_SPRITE_LOOP;
	}

	public Renderable(TiledMapRenderer tiledRenderer, TiledMapTileLayer[] layers) {
		this.layers = layers;
		this.tiledRenderer = tiledRenderer;
		this.renderType = RenderType.TILED;
	}

	public TextureRegion getCurrent(float deltaTime) {
		if (renderType == RenderType.ANIM_SPRITE_LOOP) {
			animTime += deltaTime;
			return animation.getKeyFrame(animTime);
		} else {
			return current;
		}
	}
}
