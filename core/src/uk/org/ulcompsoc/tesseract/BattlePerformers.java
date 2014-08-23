package uk.org.ulcompsoc.tesseract;

import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.TargetMarker;
import uk.org.ulcompsoc.tesseract.systems.BattleAttackSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleMessageSystem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattlePerformers {
	public static class AttackPerformer implements MouseClickPerformer {
		private ComponentMapper<Position>	posMapper	= ComponentMapper.getFor(Position.class);

		@SuppressWarnings("unchecked")
		@Override
		public void perform(Entity invoker, Engine engine) {
			Gdx.app.debug("PERFORM_ATTACK", "Performing an attack.");

			ImmutableArray<Entity> enemies = engine.getEntitiesFor(Family.getFor(Enemy.class));

			if (enemies != null) {
				for (int i = 0; i < enemies.size(); i++) {
					Entity e = enemies.get(i);
					GridPoint2 pos = posMapper.get(e).position;

					e.add(new TargetMarker());
					e.add(new MouseClickListener(new Rectangle(pos.x * WorldConstants.TILE_WIDTH, pos.y
							* WorldConstants.TILE_HEIGHT, WorldConstants.TILE_WIDTH, WorldConstants.TILE_HEIGHT),
							enemyTargetPerformer));
					battleMessageSystem.addMessage(TesseractStrings.getChooseTargetMessage());
				}
			}
		}
	}

	public static class JumpPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			Gdx.app.debug("PERFORM_JUMP", "Performing a jump.");
			battleMessageSystem.addMessage(TesseractStrings.getJumpMessage());
		}
	}

	public static class FleePerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			Gdx.app.debug("PERFORM_FLEE", "Performing a flee.");
			battleMessageSystem.addMessage(TesseractStrings.getFleeMessage());
		}
	}

	public static class EnemyTargetPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			Gdx.app.debug("PERFORM_TARGET", "Attack was targetted.");

			removeTargets(engine);
			battleAttackSystem.addAttack(new BattleAttack(TesseractMain.playerEntity, invoker, AttackType.MELEE));
		}
	}

	public static BattleAttackSystem			battleAttackSystem		= null;
	public static BattleMessageSystem			battleMessageSystem		= null;

	public final static AttackPerformer			attackPerformer			= new AttackPerformer();
	public final static JumpPerformer			jumpPerformer			= new JumpPerformer();
	public final static FleePerformer			fleePerformer			= new FleePerformer();
	public final static EnemyTargetPerformer	enemyTargetPerformer	= new EnemyTargetPerformer();
	public final static MouseClickPerformer[]	performers				= { attackPerformer, jumpPerformer,
			fleePerformer												};

	@SuppressWarnings("unchecked")
	public static void removeTargets(Engine engine) {
		ImmutableArray<Entity> targets = engine.getEntitiesFor(Family.getFor(TargetMarker.class));
		while (targets.size() > 0) {
			Entity e = targets.get(0);
			e.remove(TargetMarker.class);
			e.remove(MouseClickListener.class);

			targets = engine.getEntitiesFor(Family.getFor(TargetMarker.class));
		}
	}
}
