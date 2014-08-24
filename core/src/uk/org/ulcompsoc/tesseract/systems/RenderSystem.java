package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Renderable.RenderType;
import uk.org.ulcompsoc.tesseract.components.TargetMarker;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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
	private ComponentMapper<Position>		posMapper			= ComponentMapper.getFor(Position.class);
	private ComponentMapper<Renderable>		renderMapper		= ComponentMapper.getFor(Renderable.class);
	private ComponentMapper<TargetMarker>	targetMarkerMapper	= ComponentMapper.getFor(TargetMarker.class);

	private Camera							camera				= null;
	private Batch							batch				= null;

	private ShapeRenderer					renderer			= null;

	@SuppressWarnings("unchecked")
	private Family							renderSystemFamily	= Family.getFor(Position.class, Renderable.class);

	boolean									reorder				= true;

	private ImmutableArray<Entity>			entitiesImmu		= null;
	private List<Entity>					entities			= null;

	public RenderSystem(Batch batch, Camera camera, int priority) {
		super(priority);

		this.batch = batch;
		this.camera = camera;
		this.renderer = new ShapeRenderer();
		this.entities = new ArrayList<Entity>();
	}

	@Override
	public void addedToEngine(Engine engine) {
		entitiesImmu = engine.getEntitiesFor(renderSystemFamily);
		engine.addEntityListener(renderSystemListener);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(renderSystemListener);
	}

	public void doReorder() {
		Gdx.app.debug("RENDER_SYSTEM_REORDER", "Reordering entities in RenderSystem.");
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
		Vector2 pos = posMapper.get(entity).getWorldPosition();
		Renderable r = renderMapper.get(entity);

		if (r.renderType == RenderType.TILED) {
			r.tiledRenderer.setView((OrthographicCamera) camera);
			for (TiledMapTileLayer layer : r.layers) {
				batch.begin();
				r.tiledRenderer.renderTileLayer(layer);
				batch.end();
			}
		} else {
			TextureRegion current = r.getCurrent(deltaTime);

			batch.begin();

			batch.draw(current, pos.x, pos.y);

			batch.end();

			if (targetMarkerMapper.has(entity)) {
				TargetMarker tm = targetMarkerMapper.get(entity);

				renderer.setColor(tm.color);

				renderer.begin(ShapeType.Line);
				renderer.rect(pos.x, pos.y, current.getRegionWidth(), current.getRegionHeight());
				renderer.end();
			}
		}
	}

	private EntityListener				renderSystemListener			= new EntityListener() {
																			@Override
																			public void entityRemoved(Entity entity) {
																			}

																			@Override
																			public void entityAdded(Entity entity) {
																				if (renderSystemFamily.matches(entity)) {
																					reorder = true;
																				}
																			}
																		};

	public static Comparator<Entity>	renderablePriorityComparator	= new Comparator<Entity>() {
																			ComponentMapper<Renderable>	rendMapper	= ComponentMapper
																															.getFor(Renderable.class);

																			@Override
																			public int compare(Entity o1, Entity o2) {
																				Renderable r1 = rendMapper.get(o1);
																				Renderable r2 = rendMapper.get(o2);

																				return (r1.renderPriority > r2.renderPriority ? 1
																						: (r1.renderPriority == r2.renderPriority ? 0
																								: -1));
																			}
																		};
}
