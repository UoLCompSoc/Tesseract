package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Text;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TextRenderSystem extends IteratingSystem {
	private Batch		batch	= null;
	private BitmapFont	font	= null;

	@SuppressWarnings("unchecked")
	public TextRenderSystem(Batch batch, BitmapFont font, int priority) {
		super(Family.getFor(Position.class, Text.class), priority);

		this.batch = batch;
		this.font = font;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Text text = Mappers.text.get(entity);
		Vector2 pos = Mappers.position.get(entity).position;

		batch.begin();

		font.setColor(text.color);
		font.drawMultiLine(batch, text.getText(), pos.x, pos.y);

		batch.end();
	}
}
