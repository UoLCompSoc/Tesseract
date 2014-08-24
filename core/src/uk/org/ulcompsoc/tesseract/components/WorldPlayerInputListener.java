package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Input.Keys;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class WorldPlayerInputListener extends Component {
	public int	upKey		= Keys.W;
	public int	downKey		= Keys.S;
	public int	leftKey		= Keys.A;
	public int	rightKey	= Keys.D;

	public int	examineKey	= Keys.SPACE;
	public int	hopKey		= Keys.ENTER;
}
