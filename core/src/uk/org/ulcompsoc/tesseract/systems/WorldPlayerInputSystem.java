package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.Move;
import uk.org.ulcompsoc.tesseract.WorldConstants;
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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class WorldPlayerInputSystem extends IteratingSystem {
	private Engine	engine	= null;

	@SuppressWarnings("unchecked")
	public WorldPlayerInputSystem(int priority) {
		super(Family.getFor(Position.class, WorldPlayerInputListener.class), priority);
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
		Vector2 pos = ComponentMapper.getFor(Position.class).get(entity).position;
		WorldPlayerInputListener listener = ComponentMapper.getFor(WorldPlayerInputListener.class).get(entity);

		final float xMove = WorldConstants.TILE_WIDTH;
		final float yMove = WorldConstants.TILE_HEIGHT;

		if (!ComponentMapper.getFor(Moving.class).has(entity)) {
			if (Gdx.input.isKeyJustPressed(listener.upKey)) {
				Gdx.app.debug("MOVE_UP", "Attempting to move.");
				attemptMove(entity, Facing.UP, pos, pos.x, pos.y + yMove);

			} else if (Gdx.input.isKeyJustPressed(listener.downKey)) {
				Gdx.app.debug("MOVE_DOWN", "Attempting to move.");
				attemptMove(entity, Facing.DOWN, pos, pos.x, pos.y - yMove);

			} else if (Gdx.input.isKeyJustPressed(listener.leftKey)) {
				Gdx.app.debug("MOVE_LEFT", "Attempting to move.");
				attemptMove(entity, Facing.LEFT, pos, pos.x - xMove, pos.y);

			} else if (Gdx.input.isKeyJustPressed(listener.rightKey)) {
				Gdx.app.debug("MOVE_RIGHT", "Attempting to move.");
				attemptMove(entity, Facing.RIGHT, pos, pos.x + xMove, pos.y);
			}
		}
	}

	private boolean attemptMove(Entity entity, Renderable.Facing facing, Vector2 start, float x, float y) {
		MovementSystem ms = engine.getSystem(MovementSystem.class);

		Move move = ms.makeAndAddMoveIfPossible(entity, Move.Type.TILE, start, x, y, 0.1f,
				ComponentMapper.getFor(Solid.class).has(entity));

		if (move != null) {
			// move was/will be successful
			entity.add(new Moving(move));
			ComponentMapper.getFor(Renderable.class).get(entity).setFacing(facing);

			return true;
		} else {
			// move failed
			Gdx.app.debug("MOVE_FAIL", "Can't move into solid object.");
			return false;
		}
	}
}
