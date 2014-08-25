package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Move;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Dialogue;
import uk.org.ulcompsoc.tesseract.components.Moving;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Renderable.Facing;
import uk.org.ulcompsoc.tesseract.components.Solid;
import uk.org.ulcompsoc.tesseract.components.WorldPlayerInputListener;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
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
	private ComponentMapper<Position>	posMapper		= ComponentMapper.getFor(Position.class);
	private ComponentMapper<Renderable>	facingMapper	= ComponentMapper.getFor(Renderable.class);
	private ComponentMapper<Moving>		movingMapper	= ComponentMapper.getFor(Moving.class);

	private Engine						engine			= null;

	private DialogueSystem				dialogueSystem	= null;

	@SuppressWarnings("unchecked")
	public WorldPlayerInputSystem(int priority) {
		super(Family.getFor(Position.class, Renderable.class, WorldPlayerInputListener.class), priority);
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
		Vector2 pos = posMapper.get(entity).position;
		WorldPlayerInputListener listener = ComponentMapper.getFor(WorldPlayerInputListener.class).get(entity);

		final float xMove = WorldConstants.TILE_WIDTH;
		final float yMove = WorldConstants.TILE_HEIGHT;

		if (!engine.getSystem(DialogueSystem.class).checkProcessing() && !TesseractMain.isTransitioning()) {
			Moving moving = movingMapper.get(entity);

			if (moving == null || moving.move.isNearlyDone()) {
				if (!ComponentMapper.getFor(Moving.class).has(entity)) {
					if (Gdx.input.isKeyPressed(listener.upKey)) {
						Gdx.app.debug("MOVE_UP", "Attempting to move.");
						attemptMove(entity, Facing.UP, pos, pos.x, pos.y + yMove);

					} else if (Gdx.input.isKeyPressed(listener.downKey)) {
						Gdx.app.debug("MOVE_DOWN", "Attempting to move.");
						attemptMove(entity, Facing.DOWN, pos, pos.x, pos.y - yMove);

					} else if (Gdx.input.isKeyPressed(listener.leftKey)) {
						Gdx.app.debug("MOVE_LEFT", "Attempting to move.");
						attemptMove(entity, Facing.LEFT, pos, pos.x - xMove, pos.y);

					} else if (Gdx.input.isKeyPressed(listener.rightKey)) {
						Gdx.app.debug("MOVE_RIGHT", "Attempting to move.");
						attemptMove(entity, Facing.RIGHT, pos, pos.x + xMove, pos.y);
					}
				}
			}

			if (Gdx.input.isKeyJustPressed(listener.examineKey)) {
				Gdx.app.debug("EXAMINE", "Attempting to examine.");
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

		Move move = ms.makeAndAddMoveIfPossible(entity, Move.Type.TILE, start, x, y, 0.1f,
				ComponentMapper.getFor(Solid.class).has(entity));
		ComponentMapper.getFor(Renderable.class).get(entity).setFacing(facing);

		if (move != null) {
			// move was/will be successful
			entity.add(new Moving(move));

			return true;
		} else {
			// move failed
			Gdx.app.debug("MOVE_FAIL", "Can't move into solid object.");
			return false;
		}
	}

	private boolean attemptExamine(Entity entity) {
		GridPoint2 pos = posMapper.get(entity).getGridPosition();
		Facing f = facingMapper.get(entity).facing;

		GridPoint2 pointInFront = Facing.pointInFront(pos, f);

		@SuppressWarnings("unchecked")
		ImmutableArray<Entity> npcEntities = engine.getEntitiesFor(Family.getFor(Position.class, Dialogue.class));

		for (int i = 0; i < npcEntities.size(); i++) {
			Entity other = npcEntities.get(i);

			GridPoint2 otherPos = posMapper.get(other).getGridPosition();

			if (pointInFront.equals(otherPos)) {
				dialogueSystem.add(other);
			}
		}

		return true;
	}
}
