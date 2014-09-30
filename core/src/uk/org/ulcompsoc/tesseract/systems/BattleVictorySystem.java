package uk.org.ulcompsoc.tesseract.systems;

import uk.org.ulcompsoc.tesseract.TesseractStrings;
import uk.org.ulcompsoc.tesseract.components.Enemy;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleVictorySystem extends EntitySystem {
	private Signal<Boolean>		battleEndSignal	= null;

	private Engine				engine			= null;

	private BattleMessageSystem	messageSystem	= null;

	private boolean				wasBoss			= false;
	private boolean				hasProc			= false;

	public BattleVictorySystem(BattleMessageSystem messageSystem, int priority) {
		super(priority);
		this.messageSystem = messageSystem;
		this.battleEndSignal = new Signal<Boolean>();
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkProcessing() {
		if (engine.getEntitiesFor(Family.getFor(Enemy.class)).size() > 0) {
			hasProc = false;
		}

		if (!hasProc && victory()) {
			hasProc = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		doVictory();
	}

	public void hint(boolean wasBoss) {
		this.wasBoss = wasBoss;
	}

	public BattleVictorySystem addVictoryListener(Listener<Boolean> listener) {
		battleEndSignal.add(listener);
		return this;
	}

	@SuppressWarnings("unchecked")
	protected boolean victory() {
		return engine.getEntitiesFor(Family.getFor(Enemy.class)).size() == 0;
	}

	protected void doVictory() {
		Gdx.app.debug("VICTORY_SYSTEM", "Victory detected.");

		messageSystem.addMessage(TesseractStrings.getVictoryMessage());

		battleEndSignal.dispatch(wasBoss);
	}
}
