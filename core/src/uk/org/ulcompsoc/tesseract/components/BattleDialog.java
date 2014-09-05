package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.ui.UIPopup;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialog extends Component {
	public final Color		fillColor;
	public final Color		lineColor;

	public final UIPopup	popup;

	public BattleDialog(UIPopup popup) {
		this(popup, Color.NAVY, Color.GRAY);
	}

	/**
	 * Note that line colour is not used atm
	 * 
	 * @param popup
	 * @param fillColor
	 * @param lineColor
	 */
	public BattleDialog(UIPopup popup, Color fillColor, Color lineColor) {
		this.fillColor = fillColor;
		this.lineColor = lineColor;
		this.popup = popup;
	}
}
