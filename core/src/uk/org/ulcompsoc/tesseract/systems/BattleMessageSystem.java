package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.TesseractMain;
import uk.org.ulcompsoc.tesseract.battle.BattleMessage;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Text;
import uk.org.ulcompsoc.tesseract.ui.UIBuilder;
import uk.org.ulcompsoc.tesseract.ui.UIPopup;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleMessageSystem extends EntitySystem {
	private List<BattleMessage>	messages				= new ArrayList<BattleMessage>();

	private float				displayTimeRemaining	= -1.0f;

	private Engine				engine					= null;

	private BitmapFont			font					= null;
	private Camera				camera					= null;

	private Batch				batch					= null;

	private Rectangle			boxPos					= null;
	private UIPopup				popup					= null;

	private BattleMessage		currentBattleMessage	= null;
	private Entity				currentMessageText		= null;

	public BattleMessageSystem(float x, float y, UIBuilder builder, Batch batch, Camera camera, BitmapFont font,
			int priority) {
		this.font = font;
		this.camera = camera;
		this.batch = batch;

		this.popup = builder.buildAndGet(15, 2);

		this.boxPos = new Rectangle(x, y, popup.innerRectangle.width, popup.innerRectangle.height);
		this.boxPos.x *= 1.25f;
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void removedFromEngine(Engine engine) {
		this.engine = null;
	}

	@Override
	public void update(float deltaTime) {
		if (displayTimeRemaining > 0.0f) {
			displayTimeRemaining -= deltaTime;

			batch.setProjectionMatrix(camera.combined);
			Color temp = batch.getColor();
			batch.setColor(TesseractMain.getCurrentMap().uiColor);

			batch.begin();
			batch.draw(popup.texture, boxPos.x, boxPos.y);
			batch.end();

			batch.setColor(temp);

		} else if (currentBattleMessage == null) {
			currentBattleMessage = messages.get(0);

			Rectangle bounds = Text.getTextRectangle(currentBattleMessage.message, boxPos.width, font);

			currentMessageText = new Entity();
			currentMessageText.add(new Position().smartCentre(bounds.width, bounds.height, boxPos));
			currentMessageText.add(new Text(currentBattleMessage.message, Color.WHITE, boxPos.width));
			engine.addEntity(currentMessageText);

			displayTimeRemaining = currentBattleMessage.time;
		} else {
			messages.remove(0);
			engine.removeEntity(currentMessageText);
			currentBattleMessage = null;
			displayTimeRemaining = -1.0f;
		}
	}

	@Override
	public boolean checkProcessing() {
		return messages.size() > 0;
	}

	public void addMessage(BattleMessage message) {
		messages.add(message);
	}

	public void handleResize(float xScale, float yScale) {
		boxPos.x *= xScale;
		boxPos.y *= yScale;
		boxPos.width *= xScale;
		boxPos.height *= yScale;
	}

	public void clearAllMessages() {
		currentBattleMessage = null;

		if (currentMessageText != null) {
			engine.removeEntity(currentMessageText);
			currentMessageText = null;
		}

		displayTimeRemaining = -1.0f;
		messages.clear();
	}
}
