package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.battle.BattleMessage;
import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Text;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleMessageSystem extends EntitySystem {
	private List<BattleMessage>	messages				= new ArrayList<BattleMessage>();

	private float				displayTimeRemaining	= -1.0f;

	private Engine				engine					= null;

	private BitmapFont			font					= null;
	private Camera				camera					= null;

	private ShapeRenderer		renderer				= null;

	private final Rectangle		boxPos;

	private BattleMessage		currentBattleMessage	= null;
	private Entity				currentMessageText		= null;

	public BattleMessageSystem(Entity baseDialogEntity, ShapeRenderer renderer, BitmapFont font, Camera camera,
			int priority) {
		this.renderer = renderer;
		this.font = font;
		this.camera = camera;

		Position pos = Mappers.position.get(baseDialogEntity);
		BattleDialog bd = Mappers.battleDialog.get(baseDialogEntity);

		if (pos == null || bd == null) {
			throw new GdxRuntimeException("Constructor of BattleMessageSystem called with invalid baseDialogEntity.");
		}

		this.boxPos = new Rectangle(pos.position.x, pos.position.y, bd.actualWidth, bd.actualHeight);
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

			TextBounds bounds = font.getBounds(messages.get(0).message);

			currentMessageText = new Entity();
			currentMessageText.add(new Position().smartCentre(bounds.width, bounds.height, boxPos));
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
