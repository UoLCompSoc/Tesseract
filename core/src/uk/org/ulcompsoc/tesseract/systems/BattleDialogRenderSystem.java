package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Position;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialogRenderSystem extends IteratingSystem {
	private Camera			camera		= null;
	private ShapeRenderer	renderer	= null;

	@SuppressWarnings("unchecked")
	public BattleDialogRenderSystem(ShapeRenderer renderer, Camera camera, int priority) {
		super(Family.getFor(Position.class, BattleDialog.class), priority);

		this.camera = camera;
		this.renderer = renderer;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		BattleDialog dialog = Mappers.battleDialog.get(entity);
		Vector2 pos = Mappers.position.get(entity).position;

		renderer.setProjectionMatrix(camera.combined);

		renderer.setColor(dialog.fillColor);
		renderer.begin(ShapeType.Filled);

		renderer.rect(pos.x, pos.y, dialog.actualWidth, dialog.actualHeight);

		renderer.end();

		renderer.setColor(dialog.lineColor);
		renderer.begin(ShapeType.Line);

		renderer.rect(pos.x, pos.y, dialog.actualWidth, dialog.actualHeight);

		renderer.end();
	}
}
