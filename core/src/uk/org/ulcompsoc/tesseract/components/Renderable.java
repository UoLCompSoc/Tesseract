package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.animations.AnimationFrameResolver;
import uk.org.ulcompsoc.tesseract.animations.PingPongFrameResolver;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Renderable extends Component {
	public enum RenderType {
		UDLR_SPRITE, UDLR_ANIM, STATIC_SPRITE, ANIM_SPRITE_LOOP, TILED, NONE;
	}

	public enum Facing {
		UP, DOWN, LEFT, RIGHT, IDLE;

		public static GridPoint2 pointInFront(GridPoint2 pos, Facing facing) {
			switch (facing) {
			case DOWN:
				return new GridPoint2(pos.x, pos.y - 1);
			case IDLE:
				return new GridPoint2(pos.x, pos.y - 1);
			case LEFT:
				return new GridPoint2(pos.x - 1, pos.y);
			case RIGHT:
				return new GridPoint2(pos.x + 1, pos.y);
			case UP:
				return new GridPoint2(pos.x, pos.y + 1);
			default:
				return null;
			}
		}

		public static GridPoint2 pointInFront(int x, int y, Facing f) {
			return pointInFront(new GridPoint2(x, y), f);
		}
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

	public Animation				idleAnim		= null;
	public Animation				upAnim			= null;
	public Animation				downAnim		= null;
	public Animation				leftAnim		= null;
	public Animation				rightAnim		= null;

	public Animation				animation		= null;
	public AnimationFrameResolver	resolver		= null;

	public TiledMapTileLayer[]		layers			= null;
	public TiledMapRenderer			tiledRenderer	= null;

	public float					width			= WorldConstants.TILE_WIDTH;
	public float					height			= WorldConstants.TILE_HEIGHT;

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

		this.width = upTexture.getRegionWidth();
		this.height = upTexture.getRegionHeight();
	}

	public Renderable(TextureRegion sprite) {
		this.renderType = RenderType.STATIC_SPRITE;
		this.facing = Facing.DOWN;
		this.current = sprite;
		this.downTexture = sprite;
		this.width = sprite.getRegionWidth();
		this.height = sprite.getRegionHeight();
	}

	public Renderable(Animation animation) {
		this(animation, new PingPongFrameResolver());
	}

	public Renderable(Animation animation, AnimationFrameResolver resolver) {
		this.animation = animation;
		this.resolver = resolver;
		this.renderType = RenderType.ANIM_SPRITE_LOOP;
		this.width = animation.getKeyFrames()[0].getRegionWidth();
		this.height = animation.getKeyFrames()[0].getRegionHeight();
	}

	public Renderable(TiledMapRenderer tiledRenderer, TiledMapTileLayer[] layers) {
		this.layers = layers;
		this.tiledRenderer = tiledRenderer;
		this.renderType = RenderType.TILED;
		this.width = WorldConstants.TILE_WIDTH;
		this.width = WorldConstants.TILE_HEIGHT;
	}

	public Renderable(Facing facing, Animation idleAnim, Animation upAnim, Animation downAnim, Animation leftAnim,
			Animation rightAnim) {
		this(facing, idleAnim, upAnim, downAnim, leftAnim, rightAnim, new PingPongFrameResolver());
	}

	public Renderable(Facing facing, Animation idleAnim, Animation upAnim, Animation downAnim, Animation leftAnim,
			Animation rightAnim, AnimationFrameResolver resolver) {
		this.facing = facing;

		this.idleAnim = idleAnim;
		this.upAnim = upAnim;
		this.downAnim = downAnim;
		this.leftAnim = leftAnim;
		this.rightAnim = rightAnim;

		this.renderType = RenderType.UDLR_ANIM;

		setFacing(facing);

		this.resolver = resolver;

	}

	public TextureRegion getCurrent(float deltaTime) {
		if (renderType == RenderType.ANIM_SPRITE_LOOP || renderType == RenderType.UDLR_ANIM) {
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

			case IDLE: {
				break;
			}
			}
		} else if (renderType == RenderType.UDLR_ANIM) {
			this.facing = facing;

			switch (facing) {
			case UP: {
				animation = upAnim;
				break;
			}

			case DOWN: {
				animation = downAnim;
				break;
			}

			case LEFT: {
				animation = leftAnim;
				break;
			}

			case RIGHT: {
				animation = rightAnim;
				break;
			}

			case IDLE: {
				animation = idleAnim;
				break;
			}
			}
		}

		return this;
	}
}
