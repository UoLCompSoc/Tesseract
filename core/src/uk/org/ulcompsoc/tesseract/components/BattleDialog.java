package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.ui.UIPopup;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class BattleDialog extends Component {
	public final UIPopup	popup;

	public Color			fillColor;

	public BattleDialog(UIPopup popup) {
		this(popup, Color.NAVY);
	}

	/**
	 * @param popup
	 *        A popup to draw for this dialog.
	 * @param fillColor
	 *        The colour to use for this dialog.
	 */
	public BattleDialog(UIPopup popup, Color fillColor) {
		this.fillColor = fillColor;
		this.popup = popup;
	}
}
