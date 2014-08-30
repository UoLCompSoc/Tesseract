package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialog extends Component {
	public final Color		fillColor;
	public final Color		lineColor;

	public final Combatant	combatant;

	public final float		relativeWidth;
	public final float		relativeHeight;

	public final float		actualWidth;
	public final float		actualHeight;

	public final Rectangle	parentRect;

	public BattleDialog(Rectangle parentRect, float relativeWidth, float relativeHeight, Combatant combatant) {
		this(parentRect, relativeWidth, relativeHeight, Color.NAVY, Color.GRAY, combatant);
	}

	public BattleDialog(Rectangle parentRect, float relativeWidth, float relativeHeight, Color fillColor,
			Color lineColor, Combatant combatant) {
		this.fillColor = fillColor;
		this.lineColor = lineColor;

		this.relativeWidth = relativeWidth;
		this.relativeHeight = relativeHeight;

		this.parentRect = parentRect;

		this.combatant = combatant;

		this.actualWidth = parentRect.width * relativeWidth;
		this.actualHeight = parentRect.height * relativeHeight;
	}
}
