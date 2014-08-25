package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.battle.BattleMessage;
import uk.org.ulcompsoc.tesseract.components.RelativePosition;
import uk.org.ulcompsoc.tesseract.components.Text;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleMessageSystem extends EntitySystem {
	private static final Rectangle	boxDimensions			= new Rectangle(0.2f, 0.8f, 0.6f, 0.1f);

	private List<BattleMessage>		messages				= new ArrayList<BattleMessage>();

	private float					displayTimeRemaining	= -1.0f;

	private Engine					engine					= null;

	private BitmapFont				font					= null;
	private Camera					camera					= null;

	private Rectangle				boxPos					= null;

	private ShapeRenderer			renderer				= new ShapeRenderer();

	private BattleMessage			currentBattleMessage	= null;
	private Entity					currentMessageText		= null;

	public BattleMessageSystem(BitmapFont font, Camera camera, int priority) {
		this(font, camera, new Rectangle(0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), priority);
	}

	public BattleMessageSystem(BitmapFont font, Camera camera, Rectangle screenRect, int priority) {
		boxPos = new RelativePosition(boxDimensions, screenRect).pos;
		this.font = font;
		this.camera = camera;
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

			renderer.setProjectionMatrix(camera.combined);
			renderer.setColor(currentBattleMessage.bgColor);
			renderer.begin(ShapeType.Filled);
			renderer.rect(boxPos.x, boxPos.y, boxPos.width, boxPos.height);
			renderer.end();

			renderer.begin(ShapeType.Line);
			renderer.setColor(Color.WHITE);
			renderer.rect(boxPos.x, boxPos.y, boxPos.width, boxPos.height);
			renderer.end();
		} else if (currentBattleMessage == null) {
			currentBattleMessage = messages.get(0);
			currentMessageText = new Entity();
			currentMessageText.add(RelativePosition.makeCentred(
					Text.getTextRectangle(currentBattleMessage.message, font), boxPos));
			currentMessageText.add(new Text(currentBattleMessage.message, currentBattleMessage.textColor));
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
		Gdx.app.debug("ADD_MESSAGE", "Got a message, queue now has " + messages.size() + " message(s).");
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
