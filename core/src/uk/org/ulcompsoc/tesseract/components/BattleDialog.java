package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialog extends Component {
	public final Color		fillColor;
	public final Color		lineColor;

	public final Combatant	combatant;

	public BattleDialog(Combatant combatant) {
		this(Color.NAVY, Color.GRAY, combatant);
	}

	public BattleDialog(Color fillColor, Color lineColor, Combatant combatant) {
		this.fillColor = fillColor;
		this.lineColor = lineColor;

		this.combatant = combatant;
	}
}
