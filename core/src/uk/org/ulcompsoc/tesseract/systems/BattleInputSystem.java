package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.components.MouseClickListener;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleInputSystem extends IteratingSystem {
	private ComponentMapper<MouseClickListener>	mclMapper		= ComponentMapper.getFor(MouseClickListener.class);

	private Camera								camera			= null;

	private Vector3								mouseCoordCache	= new Vector3(0.0f, 0.0f, 0.0f);

	private boolean								hasReleased		= true;

	private Engine								engine			= null;

	@SuppressWarnings("unchecked")
	public BattleInputSystem(Camera camera, int priority) {
		super(Family.getFor(MouseClickListener.class), priority);

		this.camera = camera;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.engine = engine;
	};

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		this.engine = null;
	};

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		MouseClickListener mcl = mclMapper.get(entity);
		Rectangle pos = mcl.rect;

		if (pos.contains(mouseCoordCache.x, mouseCoordCache.y)) {
			mcl.perform(entity, engine);
		}
	}

	@Override
	public boolean checkProcessing() {
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (hasReleased) {
				mouseCoordCache.set(Gdx.input.getX(Buttons.LEFT), Gdx.input.getY(Buttons.LEFT), 0.0f);
				mouseCoordCache = camera.unproject(mouseCoordCache);

				hasReleased = false;

				return true;
			}
		} else {
			hasReleased = true;
		}

		return false;
	}
}
