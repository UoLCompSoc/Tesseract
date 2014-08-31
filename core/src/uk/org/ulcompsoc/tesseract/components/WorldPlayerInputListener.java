package uk.org.ulcompsoc.tesseract.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Input.Keys;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class WorldPlayerInputListener extends Component {
	public int[]	upKeys		= { Keys.W, Keys.UP };
	public int[]	downKeys	= { Keys.S, Keys.DOWN };
	public int[]	leftKeys	= { Keys.A, Keys.LEFT };
	public int[]	rightKeys	= { Keys.D, Keys.RIGHT };

	public int		examineKey	= Keys.SPACE;
	public int		hopKey		= Keys.ENTER;
}
