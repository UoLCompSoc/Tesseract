package uk.org.ulcompsoc.tesseract.systems;

import java.util.ArrayList;
import java.util.List;

import uk.org.ulcompsoc.tesseract.Mappers;
import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Dialogue;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Text;
import uk.org.ulcompsoc.tesseract.ui.UIBuilder;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class DialogueSystem extends EntitySystem {
	private final Batch batch;
	private final Camera camera;
	private final UIBuilder builder;
	private final Color uiColor;

	private BitmapFont font = null;

	private List<Entity> dias = new ArrayList<Entity>();

	private int currentMsg = 0;

	private boolean pressed = false;

	private Position cachePos = new Position();
	private Rectangle cacheRect = new Rectangle();

	public DialogueSystem(Camera camera, Batch batch, UIBuilder builder, Color uiColor, BitmapFont font, int priority) {
		super(priority);

		this.batch = batch;
		this.camera = camera;
		this.builder = builder;
		this.uiColor = uiColor;

		this.font = font;
	}

	@Override
	public void update(float deltaTime) {
		Entity currentDia = dias.get(0);
		Dialogue dia = Mappers.dialogue.get(currentDia);
		Vector2 pos = Mappers.position.get(currentDia).position;

		final String thisMsg = dia.dialogueLines[currentMsg];

		final int boxW = 5;
		final int boxPaddingW = 5;
		final int boxPaddingH = 5;
		final int boxDrawable = boxW * builder.tileWidth - boxPaddingW * 2;

		cacheRect = Text.getTextRectangle(thisMsg, boxDrawable, font);
		cacheRect.x = pos.x + boxPaddingW;
		cacheRect.y = pos.y + builder.tileHeight - boxPaddingH;

		final int boxH = Math.max((int) (cacheRect.height / builder.tileHeight)
		        + (cacheRect.height % builder.tileHeight == 0 ? 1 : 0), 2);

		batch.setProjectionMatrix(camera.combined);

		Color oldCol = batch.getColor();

		batch.begin();
		batch.setColor(uiColor);
		batch.draw(builder.buildAndGet(boxW, boxH).texture, pos.x, pos.y + WorldConstants.TILE_HEIGHT);

		batch.setColor(oldCol);

		cachePos.smartCentreX(cacheRect.width, pos.x, boxW * builder.tileWidth)
		        .smartCentreY(cacheRect.height, pos.y, boxH * builder.tileHeight)
		        .adjustY(cacheRect.height + WorldConstants.TILE_HEIGHT);

		// Gdx.app.debug("DIALOGUE_UPDATE", "BoxH = " + (boxH *
		// builder.tileHeight) + ", textH = " + cacheRect.height
		// + "\ntextY = " + cachePos.position.y);

		font.drawWrapped(batch, thisMsg, cachePos.position.x, cachePos.position.y, boxDrawable);

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
