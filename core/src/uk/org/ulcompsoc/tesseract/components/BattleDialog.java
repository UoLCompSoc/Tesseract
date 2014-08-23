package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialog extends Component {
	public Color	fillColor	= Color.BLUE;
	public Color	lineColor	= Color.GRAY;

	public BattleDialog() {
		this(Color.BLUE, Color.GRAY);
	}

	public BattleDialog(Color fillColor) {
		this(fillColor, Color.GRAY);
	}

	public BattleDialog(Color fillColor, Color lineColor) {
		this.fillColor = fillColor;
		this.lineColor = lineColor;
	}
}
