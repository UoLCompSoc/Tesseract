package uk.org.ulcompsoc.tesseract;

import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Mappers {
	public static ComponentMapper<Position>		position	= ComponentMapper.getFor(Position.class);
	public static ComponentMapper<Renderable>	renderable	= ComponentMapper.getFor(Renderable.class);
}
