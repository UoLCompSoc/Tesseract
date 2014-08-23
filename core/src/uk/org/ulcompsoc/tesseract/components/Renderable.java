package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Renderable extends Component {
	public enum RenderType {
		UDLR_SPRITE, STATIC_SPRITE, TILED, NONE;
	}

	public RenderType		renderType		= RenderType.NONE;

	public TextureRegion	current			= null;

	// not the component's responsibility to dispose(), should be done in class
	// where it's loaded
	public TextureRegion	upTexture		= null;
	public TextureRegion	downTexture		= null;
	public TextureRegion	leftTexture		= null;
	public TextureRegion	rightTexture	= null;

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

	public TextureRegion getCurrent() {
		return current;
	}
}
