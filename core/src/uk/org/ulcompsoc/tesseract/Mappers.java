package uk.org.ulcompsoc.tesseract;

import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Text;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * @author Ashley Davis (SgtCoDFish)
 */
public class Mappers {
	public static ComponentMapper<Position>		position		= ComponentMapper.getFor(Position.class);
	public static ComponentMapper<Renderable>	renderable		= ComponentMapper.getFor(Renderable.class);
	public static ComponentMapper<BattleDialog>	battleDialog	= ComponentMapper.getFor(BattleDialog.class);
	public static ComponentMapper<Text>			text			= ComponentMapper.getFor(Text.class);
}
