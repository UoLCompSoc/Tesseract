package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialog extends Component {
	public Color		fillColor	= Color.BLUE;
	public Color		lineColor	= Color.GRAY;

	public Combatant	combatant	= null;

	public BattleDialog(Combatant combatant) {
		this(Color.BLUE, Color.GRAY, combatant);
	}

	public BattleDialog(Color fillColor, Combatant combatant) {
		this(fillColor, Color.GRAY, combatant);
	}

	public BattleDialog(Color fillColor, Color lineColor, Combatant combatant) {
		this.fillColor = fillColor;
		this.lineColor = lineColor;
		this.combatant = combatant;
	}
}
