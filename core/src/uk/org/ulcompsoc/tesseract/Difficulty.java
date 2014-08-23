package uk.org.ulcompsoc.tesseract;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public enum Difficulty {
	EASY, MEDIUM, HARD, EXTREME;

	public static Difficulty parseCommandLineArgs(String[] args) {
		Difficulty ret = EASY;
		String sub = null;

		for (int i = 0; i < args.length; i++) {
			String s = args[i];

			if (s.startsWith("--difficulty=")) {
				sub = s.substring(s.indexOf('='));
				System.out.println("Parse found --difficulty=" + sub);

			} else if (s.equals("-d")) {
				if (i != args.length - 1) {
					sub = args[i + 1];
					System.out.println("Parse found -d " + sub);
				}
			}
		}

		if (sub != null) {
			if ("easy".equalsIgnoreCase(sub)) {
				ret = EASY;
			} else if ("medium".equalsIgnoreCase(sub)) {
				ret = MEDIUM;
			} else if ("hard".equalsIgnoreCase(sub)) {
				ret = HARD;
			} else if ("extreme".equalsIgnoreCase(sub)) {
				ret = EXTREME;
			}
		}

		return ret;
	}
}
