package uk.org.ulcompsoc.tesseract.input;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class AlwaysOnPredicate implements InputActivationPredicate {
	public AlwaysOnPredicate() {
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
