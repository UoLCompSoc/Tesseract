package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Position;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialogRenderSystem extends IteratingSystem {
	private Batch	batch	= null;
	private Camera	camera	= null;

	@SuppressWarnings("unchecked")
	public BattleDialogRenderSystem(Batch batch, Camera camera, int priority) {
		super(Family.getFor(Position.class, BattleDialog.class), priority);

		this.batch = batch;
		this.camera = camera;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		BattleDialog dialog = Mappers.battleDialog.get(entity);
		Vector2 pos = Mappers.position.get(entity).position;

		Color oldColor = batch.getColor();

		if (Gdx.input.isKeyPressed(Keys.R)) {
			Gdx.app.debug("POS", "X,Y= " + pos.x + ", " + pos.y + ".");
		}

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.setColor(dialog.fillColor);
		batch.draw(dialog.popup.texture, pos.x, pos.y);

		batch.end();

		batch.setColor(oldColor);

		// renderer.setProjectionMatrix(camera.combined);
		//
		// renderer.setColor(dialog.fillColor);
		// renderer.begin(ShapeType.Filled);
		//
		// renderer.rect(pos.x, pos.y, dim.width, dim.height);
		//
		// renderer.end();
		//
		// renderer.setColor(dialog.lineColor);
		// renderer.begin(ShapeType.Line);
		//
		// renderer.rect(pos.x, pos.y, dim.width, dim.height);
		//
		// renderer.end();
	}
}
