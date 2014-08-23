package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.RelativePosition;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialogRenderSystem extends IteratingSystem {
	private ComponentMapper<RelativePosition>	relPosMapper	= ComponentMapper.getFor(RelativePosition.class);
	private ComponentMapper<BattleDialog>		bdMapper		= ComponentMapper.getFor(BattleDialog.class);

	private Camera								camera			= null;
	private ShapeRenderer						renderer		= null;

	@SuppressWarnings("unchecked")
	public BattleDialogRenderSystem(Camera camera, int priority) {
		super(Family.getFor(RelativePosition.class, BattleDialog.class), priority);

		this.camera = camera;
		this.renderer = new ShapeRenderer();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		BattleDialog dialog = bdMapper.get(entity);
		Rectangle posRect = relPosMapper.get(entity).pos;

		renderer.setProjectionMatrix(camera.combined);

		renderer.setColor(dialog.fillColor);
		renderer.begin(ShapeType.Filled);

		renderer.rect(posRect.x, posRect.y, posRect.width, posRect.height);

		renderer.end();

		renderer.setColor(dialog.lineColor);
		renderer.begin(ShapeType.Line);

		renderer.rect(posRect.x, posRect.y, posRect.width, posRect.height);

		renderer.end();
	}
}
