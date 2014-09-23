package uk.org.ulcompsoc.tesseract;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class MonsterTileHandler {
	boolean						moving					= false;

	int							monsterTilesVisited		= 0;

	Random						random					= new Random();

	MonsterTileAddListener		movingAddListener		= new MonsterTileAddListener();
	MonsterTileRemoveListener	movingRemoveListener	= new MonsterTileRemoveListener();

	public class MonsterTileAddListener implements Listener<Entity> {
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			if (Mappers.moving.has(object)) {
				moving = true;
			}
		}
	}

	public class MonsterTileRemoveListener implements Listener<Entity> {
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			if (moving && !Mappers.moving.has(object)) {
				moving = false;

				GridPoint2 pos = Mappers.position.get(object).getGridPosition();

				if (TesseractMain.getCurrentMap().isMonsterTile(pos)) {
					monsterTilesVisited++;
					if (monsterTilesVisited % 5 == 0)
						Gdx.app.debug("MONSTER_STEPS", "" + monsterTilesVisited + " steps taken.");
					final double prob = 0.02 * monsterTilesVisited;
					final double rand = random.nextDouble();

					if (rand <= prob) {
						monsterTilesVisited = 0;
						TesseractMain.flagBattleChange(false);
					}
				}
			}
		}
	}
}