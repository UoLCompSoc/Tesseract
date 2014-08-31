package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.FocusTaker;
import uk.org.ulcompsoc.tesseract.components.Position;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class FocusTakingSystem extends IteratingSystem {
	@SuppressWarnings("unchecked")
	public FocusTakingSystem(int priority) {
		super(Family.getFor(Position.class, FocusTaker.class), priority);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Vector2 camPos = Mappers.position.get(entity).position;
		FocusTaker ft = Mappers.focusTaker.get(entity);

		ft.camera.position.x = camPos.x;
		ft.camera.position.y = camPos.y;
		ft.camera.position.z = 0.0f;
		ft.camera.update();
	}

}
