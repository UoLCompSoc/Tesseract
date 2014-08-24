package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.animations.AnimationFrameResolver;
import uk.org.ulcompsoc.tesseract.animations.PingPongFrameResolver;

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

	public enum Facing {
		UP, DOWN, LEFT, RIGHT;
	}

	public RenderType				renderType		= RenderType.NONE;

	private TextureRegion			current			= null;

	// not the component's responsibility to dispose(), should be done in class
	// where it's loaded
	public Facing					facing			= Facing.DOWN;
	public TextureRegion			upTexture		= null;
	public TextureRegion			downTexture		= null;
	public TextureRegion			leftTexture		= null;
	public TextureRegion			rightTexture	= null;

	public Animation				animation		= null;
	public AnimationFrameResolver	resolver		= null;

	public TiledMapTileLayer[]		layers			= null;
	public TiledMapRenderer			tiledRenderer	= null;

	// lower = first
	public int						renderPriority	= 0;

	public Renderable(Facing facing, TextureRegion upTexture, TextureRegion downTexture, TextureRegion leftTexture,
			TextureRegion rightTexture) {
		this.facing = facing;
		this.upTexture = upTexture;
		this.downTexture = downTexture;
		this.leftTexture = leftTexture;
		this.rightTexture = rightTexture;

		this.current = downTexture;

		this.renderType = RenderType.UDLR_SPRITE;
	}

	public Renderable(TextureRegion sprite) {
		this.renderType = RenderType.STATIC_SPRITE;
		this.facing = Facing.DOWN;
		this.current = sprite;
		this.downTexture = sprite;
	}

	public Renderable(Animation animation) {
		this(animation, new PingPongFrameResolver());
	}

	public Renderable(Animation animation, AnimationFrameResolver resolver) {
		this.animation = animation;
		this.resolver = resolver;
		this.renderType = RenderType.ANIM_SPRITE_LOOP;
	}

	public Renderable(TiledMapRenderer tiledRenderer, TiledMapTileLayer[] layers) {
		this.layers = layers;
		this.tiledRenderer = tiledRenderer;
		this.renderType = RenderType.TILED;
	}

	public TextureRegion getCurrent(float deltaTime) {
		if (renderType == RenderType.ANIM_SPRITE_LOOP) {
			return resolver.resolveFrame(animation, deltaTime);
		} else {
			return current;
		}
	}

	public Renderable setPrioritity(int priority) {
		this.renderPriority = priority;
		return this;
	}

	public Renderable setAnimationResolver(AnimationFrameResolver resolver) {
		this.resolver = resolver;
		return this;
	}

	public Renderable setFacing(Facing facing) {
		if (renderType == RenderType.UDLR_SPRITE) {
			this.facing = facing;

			switch (facing) {
			case UP: {
				current = upTexture;
				break;
			}

			case DOWN: {
				current = downTexture;
				break;
			}

			case LEFT: {
				current = leftTexture;
				break;
			}

			case RIGHT: {
				current = rightTexture;
				break;
			}
			}
		}

		return this;
	}
}
