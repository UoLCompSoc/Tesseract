package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleInputSystem extends EntitySystem {
	private ComponentMapper<BattleDialog>	bdMapper		= ComponentMapper.getFor(BattleDialog.class);

	private Camera							camera			= null;

	private Vector3							mouseCoordCache	= new Vector3(0.0f, 0.0f, 0.0f);

	private boolean							hasReleased		= true;

	private Engine							engine			= null;

	private ImmutableArray<Entity>			entities		= null;

	public BattleInputSystem(Camera camera, int priority) {
		super(priority);

		this.camera = camera;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		Family family = Family.getFor(MouseClickListener.class);
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
		Rectangle pos = mcl.rect;
		BattleDialog bd = bdMapper.get(entity);
		Combatant com = (bd != null ? bd.combatant : null);

		if (pos.contains(mouseCoordCache.x, mouseCoordCache.y)) {
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
