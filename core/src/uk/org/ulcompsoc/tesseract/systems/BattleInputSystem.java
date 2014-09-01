package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.Dimension;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Position;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleInputSystem extends EntitySystem {
	private Camera					camera			= null;

	private Vector3					mouseCoordCache	= new Vector3(0.0f, 0.0f, 0.0f);

	private boolean					hasReleased		= true;

	private Engine					engine			= null;

	private ImmutableArray<Entity>	entities		= null;

	public BattleInputSystem(Camera camera, int priority) {
		super(priority);

		this.camera = camera;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		Family family = Family.getFor(MouseClickListener.class, Position.class, Dimension.class);
		this.entities = engine.getEntitiesFor(family);
		this.engine = engine;
	};

	@Override
	public void removedFromEngine(Engine engine) {
		this.entities = null;
		this.engine = null;
	};

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < entities.size(); ++i) {
			if (processEntity(entities.get(i), deltaTime)) {
				break;
			}
		}
	}

	public boolean processEntity(Entity entity, float deltaTime) {
		MouseClickListener mcl = Mappers.mouseClickListener.get(entity);
		Vector2 pos = Mappers.position.get(entity).position;
		Dimension dim = Mappers.dimension.get(entity);
		BattleDialog bd = Mappers.battleDialog.get(entity);
		Combatant com = (bd != null ? bd.combatant : null);

		final float x = mouseCoordCache.x;
		final float y = mouseCoordCache.y;

		if (x >= pos.x && y >= pos.y && x <= (dim.width + pos.x) && y <= (dim.height + pos.y)) {
			if (com == null || com.canAct()) {
				if (com != null) {
					com.thinkingTime = 0.0f;
				}

				mcl.perform(entity, engine);
				return true;
			} else {
				engine.getSystem(BattleMessageSystem.class).addMessage(TesseractStrings.getAttackNotReadyMessage());
			}
		}

		return false;
	}

	@Override
	public boolean checkProcessing() {
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (!TesseractMain.isTransitioning()) {
				if (hasReleased) {
					mouseCoordCache.set(Gdx.input.getX(Buttons.LEFT), Gdx.input.getY(Buttons.LEFT), 0.0f);
					mouseCoordCache = camera.unproject(mouseCoordCache);

					hasReleased = false;

					return true;
				}
			}
		} else {
			hasReleased = true;
		}

		return false;
	}
}
