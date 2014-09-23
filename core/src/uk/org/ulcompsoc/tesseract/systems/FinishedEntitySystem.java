package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.components.FinishedMarker;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * <p>
 * Removes all Entities with the {@link FinishedMarker} component from the
 * {@link Engine} to which this system is attached.
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class FinishedEntitySystem extends IteratingSystem {
	private Engine	engine	= null;

	@SuppressWarnings("unchecked")
	public FinishedEntitySystem(int priority) {
		this(Family.getFor(FinishedMarker.class), priority);
	}

	protected FinishedEntitySystem(Family family, int priority) {
		super(family, priority);
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		super.addedToEngine(engine);
	}

	@Override
	public void removedFromEngine(Engine engine) {
		this.engine = null;
		super.removedFromEngine(engine);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		engine.removeEntity(entity);
	}

}
