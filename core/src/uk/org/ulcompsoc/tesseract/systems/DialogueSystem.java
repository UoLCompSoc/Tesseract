package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.components.Dialogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class DialogueSystem extends EntitySystem {
	private Batch			batch		= null;
	private Camera			camera		= null;
	private BitmapFont		font		= null;

	private List<Entity>	dias		= new ArrayList<Entity>();

	private ShapeRenderer	renderer	= new ShapeRenderer();

	private int				currentMsg	= 0;

	private boolean			pressed		= false;

	public DialogueSystem(Camera camera, Batch batch, BitmapFont font, int priority) {
		super(priority);
		this.camera = camera;
		this.batch = batch;
		this.font = font;
	}

	@Override
	public void update(float deltaTime) {
		Entity currentDia = dias.get(0);
		Dialogue dia = Mappers.dialogue.get(currentDia);
		Vector2 pos = Mappers.position.get(currentDia).position;

		renderer.setProjectionMatrix(camera.combined);

		renderer.setColor(Color.NAVY);
		renderer.begin(ShapeType.Filled);

		renderer.rect(pos.x, pos.y, 100, 100);

		renderer.end();

		renderer.setColor(Color.WHITE);
		renderer.begin(ShapeType.Line);

		renderer.rect(pos.x, pos.y, 100, 100);

		renderer.end();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		font.drawWrapped(batch, dia.dialogueLines[currentMsg], pos.x, pos.y + 95, 100);

		batch.end();

		if (pressed) {
			currentMsg++;
			pressed = false;

			if (currentMsg == dia.dialogueLines.length) {
				dia.finishSignal.dispatch(currentDia);
				dias.remove(0);
				currentMsg = 0;
				Gdx.app.debug("DIALOGUE_SYSTEM", "Finished a conversation.");
				return;
			}
		}
	}

	@Override
	public boolean checkProcessing() {
		return dias.size() > 0;
	}

	public void add(Entity e) {

		dias.add(e);
	}

	public void notifyPress() {
		pressed = true;
	}
}
