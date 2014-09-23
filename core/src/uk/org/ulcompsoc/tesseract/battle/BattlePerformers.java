package uk.org.ulcompsoc.tesseract.battle;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.MouseClickPerformer;
import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Dimension;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.TargetMarker;
import uk.org.ulcompsoc.tesseract.systems.BattleAttackSystem;
import uk.org.ulcompsoc.tesseract.systems.BattleMessageSystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattlePerformers {
	public static class AttackPerformer implements MouseClickPerformer {
		@SuppressWarnings("unchecked")
		@Override
		public void perform(Entity invoker, Engine engine) {
			ImmutableArray<Entity> enemies = engine.getEntitiesFor(Family.getFor(Enemy.class));

			if (enemies != null) {
				battleMessageSystem.addMessage(TesseractStrings.getChooseTargetMessage());

				for (int i = 0; i < enemies.size(); i++) {
					Entity e = enemies.get(i);

					if (Mappers.finishedMarker.has(e)) {
						continue;
					}

					final Renderable r = Mappers.renderable.get(e);
					final float scaleAmt = (Mappers.scaled.has(e) ? Mappers.scaled.get(e).scaleAmt : 1.0f);

					final float imgW = r.width * scaleAmt;
					final float imgH = r.height * scaleAmt;

					e.add(new TargetMarker());
					e.add(new Dimension(imgW, imgH));
					e.add(new MouseClickListener(enemyTargetPerformer));
				}
			}
		}
	}

	public static class DefendPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			battleMessageSystem.addMessage(TesseractStrings.getDefendMessage());
			Mappers.combatant.get(TesseractMain.battlePlayerEntity).addBuff(new DefenceBuff(5.0f));

			addDefendAnimation(engine);
		}
	}

	public static class QuaffPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			battleMessageSystem.addMessage(TesseractStrings.getQuaffMessage());
			Mappers.combatant.get(TesseractMain.battlePlayerEntity).addBuff(new HealBuff(engine, 5));
		}
	}

	public static class FleePerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
			battleMessageSystem.addMessage(TesseractStrings.getFleeMessage());
		}
	}

	public static class EnemyTargetPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker, Engine engine) {
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
			e.remove(Dimension.class);

			targets = engine.getEntitiesFor(Family.getFor(TargetMarker.class));
		}
	}

	private static void addDefendAnimation(Engine engine) {
		Entity e = new Entity();

		Position playerPos = Mappers.position.get(TesseractMain.battlePlayerEntity);

		e.add(new Position(playerPos.position.x, playerPos.position.y + WorldConstants.TILE_HEIGHT * 2.0f));
		e.add(TesseractMain.getTempDefendRenderable(e));

		engine.addEntity(e);
	}
}
