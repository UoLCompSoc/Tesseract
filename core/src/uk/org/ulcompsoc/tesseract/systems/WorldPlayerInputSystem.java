package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.Move;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Dialogue;
import uk.org.ulcompsoc.tesseract.components.Moving;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Renderable.Facing;
import uk.org.ulcompsoc.tesseract.components.WorldPlayerInputListener;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class WorldPlayerInputSystem extends IteratingSystem {
	private Engine			engine					= null;

	private DialogueSystem	dialogueSystem			= null;

	public Signal<Boolean>	worldSelectChangeSignal	= null;

	@SuppressWarnings("unchecked")
	public WorldPlayerInputSystem(Listener<Boolean> worldSelectChangeListener, int priority) {
		super(Family.getFor(Position.class, Renderable.class, WorldPlayerInputListener.class), priority);
		worldSelectChangeSignal = new Signal<Boolean>();
		worldSelectChangeSignal.add(worldSelectChangeListener);
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.engine = engine;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		this.engine = null;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		if (dialogueSystem == null) {
			dialogueSystem = engine.getSystem(DialogueSystem.class);
		}

		Vector2 pos = Mappers.position.get(entity).position;
		WorldPlayerInputListener listener = Mappers.worldPlayerInputListener.get(entity);

		final float xMove = WorldConstants.TILE_WIDTH;
		final float yMove = WorldConstants.TILE_HEIGHT;

		if (!engine.getSystem(DialogueSystem.class).checkProcessing() && !TesseractMain.isTransitioning()) {
			Moving moving = Mappers.moving.get(entity);

			if (moving == null || moving.move.isNearlyDone()) {
				if (!Mappers.moving.has(entity)) {
					if (isAnyKeyPressed(listener.upKeys)) {
						attemptMove(entity, Facing.UP, pos, pos.x, pos.y + yMove);
					} else if (isAnyKeyPressed(listener.downKeys)) {
						attemptMove(entity, Facing.DOWN, pos, pos.x, pos.y - yMove);
					} else if (isAnyKeyPressed(listener.leftKeys)) {
						attemptMove(entity, Facing.LEFT, pos, pos.x - xMove, pos.y);
					} else if (isAnyKeyPressed(listener.rightKeys)) {
						attemptMove(entity, Facing.RIGHT, pos, pos.x + xMove, pos.y);
					} else if (isAnyKeyJustPressed(listener.worldChangeKeys)) {
						worldSelectChangeSignal.dispatch(true);
					}
				}
			}

			if (isAnyKeyJustPressed(listener.examineKeys)) {
				// Gdx.app.debug("EXAMINE", "Attempting to examine.");
				attemptExamine(entity);
			}
		} else {
			if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY)) {
				dialogueSystem.notifyPress();
			}
		}
	}

	private boolean attemptMove(Entity entity, Renderable.Facing facing, Vector2 start, float x, float y) {
		MovementSystem ms = engine.getSystem(MovementSystem.class);

		Move move = ms.makeAndAddMoveIfPossible(entity, Move.Type.TILE, start, x, y, 0.1f, Mappers.solid.has(entity));
		Mappers.renderable.get(entity).setFacing(facing);

		if (move != null) {
			// move was/will be successful
			entity.add(new Moving(move));

			return true;
		} else {
			// move failed
			return false;
		}
	}

	private boolean attemptExamine(Entity entity) {
		GridPoint2 pos = Mappers.position.get(entity).getGridPosition();
		Facing f = Mappers.renderable.get(entity).facing;

		GridPoint2 pointInFront = Facing.pointInFront(pos.x, pos.y, f);
		int origX = pointInFront.x;
		int origY = pointInFront.y;

		@SuppressWarnings("unchecked")
		ImmutableArray<Entity> npcEntities = engine.getEntitiesFor(Family.getFor(Position.class, Dialogue.class));

		for (int i = 0; i < npcEntities.size(); i++) {
			Entity other = npcEntities.get(i);

			Dialogue dia = Mappers.dialogue.get(other);
			GridPoint2 otherPos = Mappers.position.get(other).getGridPosition();

			for (int x = 0; x <= dia.interactionWidth; x++) {
				if (pointInFront.equals(otherPos)) {
					dialogueSystem.add(other);
					return true;
				}

				pointInFront.x--;
			}

			pointInFront.x = origX;

			for (int y = 0; y < dia.interactionHeight; y++) {
				pointInFront.y--;

				if (pointInFront.equals(otherPos)) {
					dialogueSystem.add(other);
					return true;
				}
			}

			pointInFront.y = origY;
		}

		return false;
	}

	public boolean isAnyKeyPressed(int[] keys) {
		for (int k : keys) {
			if (Gdx.input.isKeyPressed(k)) {
				return true;
			}
		}

		return false;
	}

	public boolean isAnyKeyJustPressed(int[] keys) {
		for (int k : keys) {
			if (Gdx.input.isKeyJustPressed(k)) {
				return true;
			}
		}

		return false;
	}
}
