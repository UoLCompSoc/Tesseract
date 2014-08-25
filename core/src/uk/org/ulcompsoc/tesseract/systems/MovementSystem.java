package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.Move;
import uk.org.ulcompsoc.tesseract.Move.Type;
import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Moving;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.tiled.TesseractMap;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class MovementSystem extends EntitySystem {
	private ComponentMapper<Position>	posMapper		= ComponentMapper.getFor(Position.class);

	private TesseractMap				map				= null;

	private List<Move>					moves			= new ArrayList<Move>();
	private List<Integer>				movesToRemove	= new ArrayList<Integer>();

	public MovementSystem(TesseractMap map, int priority) {
		super(priority);

		this.map = map;
	}

	public void setMap(TesseractMap map) {
		this.map = map;
	}

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			boolean done = false;

			done = move.update(deltaTime);

			Position pos = posMapper.get(move.mover);

			pos.position.set(move.getCurrentX(), move.getCurrentY());

			if (done) {
				// Gdx.app.debug("MOVE_COMPLETE", "Finished a move.");
				move.mover.remove(Moving.class);
				movesToRemove.add(i);
			}
		}

		if (movesToRemove.size() > 0) {
			for (Integer i : movesToRemove) {
				moves.remove(i.intValue());
			}

			movesToRemove.clear();
		}
	}

	/**
	 * 
	 * @param move
	 * @return true if the move will be successful.
	 */
	public boolean addMove(Move move) {
		if (!map.isTileSolid((int) move.nx / WorldConstants.TILE_WIDTH, (int) move.ny / WorldConstants.TILE_HEIGHT)) {
			moves.add(move);

			return true;
		} else {
			return false;
		}
	}

	public Move makeAndAddMoveIfPossible(Entity mover, Type type, Vector2 start, float nx, float ny, float duration,
			boolean isSolid) {
		if (!map.isTileSolid((int) nx / WorldConstants.TILE_WIDTH, (int) ny / WorldConstants.TILE_HEIGHT)) {
			Move move = new Move(mover, type, start, nx, ny, duration, isSolid);
			moves.add(move);
			return move;
		} else {
			return null;
		}
	}
}
