package uk.org.ulcompsoc.tesseract;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattlePerformers {
	public static class AttackPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker) {
			Gdx.app.debug("PERFORM_ATTACK", "Performing an attack.");
		}
	}

	public static class JumpPerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker) {
			Gdx.app.debug("PERFORM_JUMP", "Performing a jump.");
		}
	}

	public static class FleePerformer implements MouseClickPerformer {
		@Override
		public void perform(Entity invoker) {
			Gdx.app.debug("PERFORM_FLEE", "Performing a flee.");
		}
	}

	public final static AttackPerformer			attackPerformer	= new AttackPerformer();
	public final static JumpPerformer			jumpPerformer	= new JumpPerformer();
	public final static FleePerformer			fleePerformer	= new FleePerformer();
	public final static MouseClickPerformer[]	performers		= { attackPerformer, jumpPerformer, fleePerformer };
}
