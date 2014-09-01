package uk.org.ulcompsoc.tesseract.animations;

import java.util.List;

import uk.org.ulcompsoc.tesseract.WorldConstants;
import uk.org.ulcompsoc.tesseract.components.Renderable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class AnimationJSONParser {
	/**
	 * <p>
	 * Parses an animation's data from a JSON file. The loaded texture is
	 * appended to textures, but it's the program's responsibility to clean it
	 * up after this function.
	 * </p>
	 * 
	 * @param jsonString
	 *        a string of valid loaded JSON describing the animation.
	 * @param textures
	 *        a list of textures to which a new texture will be appended if this
	 *        function is successful.
	 * @return a {@link Renderable} if successful or null on failure.
	 */
	public static Renderable parseAnimation(String jsonString, List<Texture> textures) {
		JsonReader reader = new JsonReader();
		JsonValue val = reader.parse(jsonString);

		if (val.child.name.equalsIgnoreCase("animation")) {
			val = val.child;

			String fileLocation = null; // required

			Integer frameCount = null; // required
			Integer textureRow = 0; // optional

			Integer cellWidth = WorldConstants.TILE_WIDTH; // optional
			Integer cellHeight = WorldConstants.TILE_HEIGHT; // optional

			Integer priority = 25; // optional

			// optional, defaults to a sensible value.
			Float frameDuration = 0.15f;
			AnimationFrameResolver resolver = null; // optional

			// should probably be an enum...
			for (JsonValue animVal : val) {
				if (animVal.name.equalsIgnoreCase("location")) {
					fileLocation = animVal.asString();
				} else if (animVal.name.equalsIgnoreCase("frames") || animVal.name.equalsIgnoreCase("frameCount")) {
					frameCount = animVal.asInt();
				} else if (animVal.name.equalsIgnoreCase("frameDuration")) {
					frameDuration = animVal.asFloat();
				} else if (animVal.name.equalsIgnoreCase("row")) {
					textureRow = animVal.asInt();
				} else if (animVal.name.equalsIgnoreCase("resolver")) {
					resolver = parseResolver(animVal);
				} else if (animVal.name.equalsIgnoreCase("width")) {
					cellWidth = animVal.asInt();
				} else if (animVal.name.equalsIgnoreCase("height")) {
					cellHeight = animVal.asInt();
				} else if (animVal.name.equalsIgnoreCase("priority")) {
					priority = animVal.asInt();
				}
			}

			final boolean hasLocation = (fileLocation != null);
			final boolean hasFrameCount = (frameCount != null);

			if (!hasLocation || !hasFrameCount) {
				Gdx.app.debug("PARSE_ANIMATION", "Ill-formed animation:\nhasLocation =  " + hasLocation
						+ ".\nhasFrameCount = " + hasFrameCount + ".");
				return null;
			}

			FileHandle fh = Gdx.files.internal(fileLocation);

			if (!fh.exists()) {
				Gdx.app.debug("PARSE_ANIMATION", "Could not find animation file: " + fileLocation + ".");
				return null;
			}

			Texture texture = null;

			try {
				texture = new Texture(fh);
			} catch (Exception e) {
				// catch-all dirty hack because the Texture files in Gdx
				// are confusing to me atm! :D

				texture = null;
				Gdx.app.debug("PARSE_ANIMATION", "Error occurred while loading texture for animation.");
			}

			if (texture == null) {
				Gdx.app.debug("PARSE_ANIMATION", "Could not load texture.");
				return null;
			}

			textures.add(texture);

			TextureRegion[] tempRegions = TextureRegion.split(texture, cellWidth, cellHeight)[textureRow];
			TextureRegion[] actualRegions = null;
			if (tempRegions.length != frameCount.intValue()) {
				System.arraycopy(tempRegions, 0, actualRegions, 0, frameCount.intValue());
			} else {
				actualRegions = tempRegions;
			}

			Animation anim = new Animation(frameDuration.floatValue(), actualRegions);

			Renderable retVal = new Renderable(anim).setPrioritity(priority.intValue());
			if (resolver != null) {
				retVal.setAnimationResolver(resolver);
			}

			return retVal;
		} else {
			return null;
		}
	}

	public static AnimationFrameResolver parseResolver(JsonValue jsonValue) {
		final String name = jsonValue.name;

		AnimationFrameResolver retVal = null;

		if (name.equalsIgnoreCase("pingpong")) {
			retVal = parsePingPong(jsonValue);
		} else if (name.equalsIgnoreCase("player")) {
			retVal = parsePlayerResolver(jsonValue);
		} else if (name.equalsIgnoreCase("slime")) {
			retVal = parseSlimeResolver(jsonValue);
		}

		return retVal;
	}

	private static AnimationFrameResolver parsePingPong(JsonValue value) {
		if (value.child != null) {
			Float startVal = null;

			for (JsonValue childVal : value.child) {
				if (childVal.name.equalsIgnoreCase("startTime")) {
					startVal = childVal.asFloat();
					break;
				}
			}

			if (startVal == null) {
				return new PingPongFrameResolver();
			} else {
				return new PingPongFrameResolver(startVal.floatValue());
			}
		} else {
			return new PingPongFrameResolver();
		}
	}

	private static AnimationFrameResolver parsePlayerResolver(JsonValue value) {
		if (value.child != null) {
			Boolean always = null;
			Float frameDuration = null;

			for (JsonValue childVal : value.child) {
				if (childVal.name.equalsIgnoreCase("always")) {
					always = childVal.asBoolean();
				} else if (childVal.name.equalsIgnoreCase("frameDuration")) {
					frameDuration = childVal.asFloat();
				}
			}

			if (always == null && frameDuration == null) {
				return new PlayerAnimationFrameResolver();
			} else if (always == null && frameDuration != null) {
				return new PlayerAnimationFrameResolver(frameDuration.floatValue());
			} else if (always != null && frameDuration == null) {
				return new PlayerAnimationFrameResolver(always.booleanValue());
			} else {
				return new PlayerAnimationFrameResolver(always.booleanValue(), frameDuration.floatValue());
			}
		} else {
			return new PlayerAnimationFrameResolver();
		}
	}

	private static AnimationFrameResolver parseSlimeResolver(JsonValue value) {
		return new SlimeFrameResolver();
	}
}
