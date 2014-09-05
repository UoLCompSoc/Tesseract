package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Text;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class TextRenderSystem extends IteratingSystem {
	private Batch		batch	= null;
	private Camera		camera	= null;
	private BitmapFont	font	= null;

	// private ShapeRenderer shapeRenderer = null;

	@SuppressWarnings("unchecked")
	public TextRenderSystem(Batch batch, Camera camera, BitmapFont font, int priority) {
		super(Family.getFor(Position.class, Text.class), priority);

		this.batch = batch;
		this.camera = camera;
		this.font = font;
		// this.shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Text text = Mappers.text.get(entity);
		Vector2 pos = Mappers.position.get(entity).position;
		Rectangle textRect = Text.getTextRectangle(text, font);

		// if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
		// shapeRenderer.setProjectionMatrix(camera.combined);
		// shapeRenderer.begin(ShapeType.Line);
		// shapeRenderer.setColor(Color.MAGENTA);
		//
		// shapeRenderer.rect(pos.x, pos.y, textRect.width, textRect.height);
		//
		// shapeRenderer.end();
		// }

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		font.setColor(text.color);
		font.drawWrapped(batch, text.getText(), pos.x + textRect.width * 0.25f, pos.y + textRect.height, text.wrapWidth);

		batch.end();
	}
}
