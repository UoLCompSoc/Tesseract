package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Renderable.RenderType;
import uk.org.ulcompsoc.tesseract.components.TargetMarker;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class RenderSystem extends EntitySystem {
	public static Comparator<Entity> renderablePriorityComparator = new RenderablePriorityComparator();

	private Camera camera = null;
	private Batch batch = null;
	private ShapeRenderer renderer = null;

	@SuppressWarnings("unchecked")
	private Family renderSystemFamily = Family.getFor(Position.class, Renderable.class);

	boolean reorder = true;

	private ImmutableArray<Entity> entitiesImmu = null;
	private List<Entity> entities = null;

	private EntityListener renderSystemListener = new RenderSystemListener();

	public RenderSystem(Batch batch, ShapeRenderer renderer, Camera camera, int priority) {
		super(priority);

		this.batch = batch;
		this.renderer = renderer;
		this.camera = camera;
		this.renderer = new ShapeRenderer();
		this.entities = new ArrayList<Entity>();
	}

	@Override
	public void addedToEngine(Engine engine) {
		entitiesImmu = engine.getEntitiesFor(renderSystemFamily);
		engine.addEntityListener(renderSystemListener);
		doReorder();
	}

	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(renderSystemListener);
	}

	public void doReorder() {
		entities.clear();

		for (int i = 0; i < entitiesImmu.size(); i++) {
			entities.add(entitiesImmu.get(i));
		}

		Collections.sort(entities, renderablePriorityComparator);
	}

	@Override
	public void update(float deltaTime) {
		if (reorder) {
			doReorder();
			reorder = false;
		}

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		renderer.setProjectionMatrix(camera.combined);

		for (int i = 0; i < entities.size(); ++i) {
			processEntity(entities.get(i), deltaTime);
		}
	}

	public void processEntity(Entity entity, float deltaTime) {
		Vector2 pos = Mappers.position.get(entity).position;
		Renderable r = Mappers.renderable.get(entity);

		if (r.renderType == RenderType.TILED) {
			r.tiledRenderer.setView((OrthographicCamera) camera);
			for (TiledMapTileLayer layer : r.layers) {
				batch.begin();
				r.tiledRenderer.renderTileLayer(layer);
				batch.end();
			}
		} else {
			final float scaleAmt = (Mappers.scaled.has(entity) ? Mappers.scaled.get(entity).scaleAmt : 1.0f);
			TextureRegion current = r.getCurrent(deltaTime);

			if (r.color != null) {
				batch.setColor(r.color);
			}

			batch.begin();

			batch.draw(current, pos.x, pos.y, current.getRegionWidth() * scaleAmt, current.getRegionHeight() * scaleAmt);

			batch.end();

			if (r.color != null) {
				batch.setColor(Color.WHITE);
			}

			if (Mappers.targetMarker.has(entity)) {
				TargetMarker tm = Mappers.targetMarker.get(entity);

				renderer.setColor(tm.color);

				renderer.begin(ShapeType.Line);
				renderer.rect(pos.x, pos.y, current.getRegionWidth() * scaleAmt, current.getRegionHeight() * scaleAmt);
				renderer.end();
			}
		}
	}

	private class RenderSystemListener implements EntityListener {

		@Override
		public void entityAdded(Entity entity) {
			if (renderSystemFamily.matches(entity)) {
				reorder = true;
			}
		}

		@Override
		public void entityRemoved(Entity entity) {
			if (renderSystemFamily.matches(entity)) {
				entities.remove(entity);
			}
		}

	}

	private static class RenderablePriorityComparator implements Comparator<Entity> {

		@Override
		public int compare(Entity o1, Entity o2) {
			Renderable r1 = Mappers.renderable.get(o1);
			Renderable r2 = Mappers.renderable.get(o2);

			return (r1.renderPriority > r2.renderPriority ? 1 : (r1.renderPriority == r2.renderPriority ? 0 : -1));
		}

	}
}
