package uk.org.ulcompsoc.tesseract.battle;

import uk.org.ulcompsoc.tesseract.MouseClickPerformer;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.TargetMarker;
import uk.org.ulcompsoc.tesseract.systems.BattleAttackSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleMessageSystem;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattlePerformers {
	public static class AttackPerformer implements MouseClickPerformer {
		private ComponentMapper<Position>	posMapper	= ComponentMapper.getFor(Position.class);

		@SuppressWarnings("unchecked")
		@Override
		public void perform(Entity invoker, Engine engine) {
			// Gdx.app.debug("PERFORM_ATTACK", "Performing an attack.");

			ImmutableArray<Entity> enemies = engine.getEntitiesFor(Family.getFor(Enemy.class));

			if (enemies != null) {
				battleMessageSystem.addMessage(TesseractStrings.getChooseTargetMessage());

				for (int i = 0; i < enemies.size(); i++) {
					Entity e = enemies.get(i);
					Vector2 pos = posMapper.get(e).position;

					final Renderable r = ComponentMapper.getFor(Renderable.class).get(e);
					final float imgW = r.width;
					final float imgH = r.height;

					e.add(new TargetMarker());
					e.add(new MouseClickListener(new Rectangle(pos.x, pos.y, imgW, imgH), enemyTargetPerformer));
				}
			}
		}
	}

	public static class DefendPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			// Gdx.app.debug("PERFORM_JUMP", "Performing defend.");
			battleMessageSystem.addMessage(TesseractStrings.getDefendMessage());
			ComponentMapper.getFor(Combatant.class).get(TesseractMain.battlePlayerEntity).addBuff(new DefenceBuff());
		}
	}

	public static class QuaffPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			// Gdx.app.debug("PERFORM_QUAFF", "Performing a quaff.");
			battleMessageSystem.addMessage(TesseractStrings.getQuaffMessage());
			ComponentMapper.getFor(Combatant.class).get(TesseractMain.battlePlayerEntity).addBuff(new HealBuff(5));
		}
	}

	public static class FleePerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			// Gdx.app.debug("PERFORM_FLEE", "Performing a flee.");
			battleMessageSystem.addMessage(TesseractStrings.getFleeMessage());
		}
	}

	public static class EnemyTargetPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			// Gdx.app.debug("PERFORM_TARGET", "Attack was targetted.");

			battleAttackSystem.addAttack(new BattleAttack(TesseractMain.battlePlayerEntity, invoker, AttackType.MELEE));
			removeTargets(engine);
		}
	}

	public static BattleAttackSystem			battleAttackSystem		= null;
	public static BattleMessageSystem			battleMessageSystem		= null;

	public final static AttackPerformer			attackPerformer			= new AttackPerformer();
	public final static DefendPerformer			defendPerformer			= new DefendPerformer();
	public final static QuaffPerformer			quaffPerformer			= new QuaffPerformer();
	public final static FleePerformer			fleePerformer			= new FleePerformer();
	public final static EnemyTargetPerformer	enemyTargetPerformer	= new EnemyTargetPerformer();
	public final static MouseClickPerformer[]	performers				= { attackPerformer, defendPerformer,
			quaffPerformer, fleePerformer								};

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
