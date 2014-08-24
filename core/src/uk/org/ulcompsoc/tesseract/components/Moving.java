package uk.org.ulcompsoc.tesseract.components;

import uk.org.ulcompsoc.tesseract.Move;

import com.badlogic.ashley.core.Component;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Moving extends Component {
	public Move	move	= null;

	public Moving(Move move) {
		this.move = move;
	}

}
