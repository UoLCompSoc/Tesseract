package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.components.RelativePosition;
import uk.org.ulcompsoc.tesseract.components.Text;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TextRenderSystem extends IteratingSystem {
	private ComponentMapper<RelativePosition>	relPosMapper	= ComponentMapper.getFor(RelativePosition.class);
	private ComponentMapper<Text>				textMapper		= ComponentMapper.getFor(Text.class);

	private Batch								batch			= null;
	private BitmapFont							font			= null;

	@SuppressWarnings("unchecked")
	public TextRenderSystem(Batch batch, BitmapFont font, int priority) {
		super(Family.getFor(RelativePosition.class, Text.class), priority);

		this.batch = batch;
		this.font = font;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Text text = textMapper.get(entity);
		Rectangle pos = relPosMapper.get(entity).pos;

		batch.begin();

		font.setColor(text.color);
		font.drawMultiLine(batch, text.text, pos.x, pos.y);

		batch.end();
	}
}
