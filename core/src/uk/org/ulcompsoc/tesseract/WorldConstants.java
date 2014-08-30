package uk.org.ulcompsoc.tesseract;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class WorldConstants {
	public static boolean		DEBUG				= false;
	public static boolean		SILENT				= false;
	public static Difficulty	DIFFICULTY			= Difficulty.EASY;

	public static final int		TILE_WIDTH			= 32;
	public static final int		TILE_HEIGHT			= 32;

	public static final float	TILE_DIAG			= (float) Math.sqrt(TILE_WIDTH * TILE_WIDTH + TILE_HEIGHT
															* TILE_HEIGHT);
	public static final float	STEPS_PER_TILE_DIAG	= 8.0f;
	public static final float	MOVE_STEP_LENGTH	= TILE_DIAG / STEPS_PER_TILE_DIAG;
}
