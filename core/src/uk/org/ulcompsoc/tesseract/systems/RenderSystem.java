package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.TargetMarker;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class RenderSystem extends IteratingSystem {
	// used to avoid allocating a new Vector2 every tick
	private Vector2							posTemp				= new Vector2(0.0f, 0.0f);

	private ComponentMapper<Position>		posMapper			= ComponentMapper.getFor(Position.class);
	private ComponentMapper<Renderable>		renderMapper		= ComponentMapper.getFor(Renderable.class);
	private ComponentMapper<TargetMarker>	targetMarkerMapper	= ComponentMapper.getFor(TargetMarker.class);

	private Camera							camera				= null;
	private Batch							batch				= null;

	private ShapeRenderer					renderer			= null;

	@SuppressWarnings("unchecked")
	public RenderSystem(Batch batch, Camera camera, int priority) {
		super(Family.getFor(Position.class, Renderable.class), priority);

		this.batch = batch;
		this.camera = camera;
		this.renderer = new ShapeRenderer();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		GridPoint2 pos = posMapper.get(entity).position;
		Renderable r = renderMapper.get(entity);

		TextureRegion current = r.getCurrent();

		posTemp.set(pos.x * WorldConstants.TILE_WIDTH, pos.y * WorldConstants.TILE_HEIGHT);
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(current, posTemp.x, posTemp.y);

		batch.end();

		if (targetMarkerMapper.has(entity)) {
			TargetMarker tm = targetMarkerMapper.get(entity);

			renderer.setColor(tm.color);
			renderer.setProjectionMatrix(camera.combined);

			renderer.begin(ShapeType.Line);
			renderer.rect(posTemp.x, posTemp.y, current.getRegionWidth(), current.getRegionHeight());
			renderer.end();
		}
	}

}
