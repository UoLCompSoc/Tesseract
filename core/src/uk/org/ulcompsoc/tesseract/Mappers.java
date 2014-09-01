package uk.org.ulcompsoc.tesseract;

import uk.org.ulcompsoc.tesseract.components.BattleDialog;
import uk.org.ulcompsoc.tesseract.components.Boss;
import uk.org.ulcompsoc.tesseract.components.Combatant;
import uk.org.ulcompsoc.tesseract.components.Dialogue;
import uk.org.ulcompsoc.tesseract.components.Enemy;
import uk.org.ulcompsoc.tesseract.components.FocusTaker;
import uk.org.ulcompsoc.tesseract.components.MouseClickListener;
import uk.org.ulcompsoc.tesseract.components.Moving;
import uk.org.ulcompsoc.tesseract.components.Named;
import uk.org.ulcompsoc.tesseract.components.Player;
import uk.org.ulcompsoc.tesseract.components.Position;
import uk.org.ulcompsoc.tesseract.components.Renderable;
import uk.org.ulcompsoc.tesseract.components.Solid;
import uk.org.ulcompsoc.tesseract.components.Stats;
import uk.org.ulcompsoc.tesseract.components.TargetMarker;
import uk.org.ulcompsoc.tesseract.components.Text;
import uk.org.ulcompsoc.tesseract.components.WorldPlayerInputListener;
import uk.org.ulcompsoc.tesseract.components.Dimension;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * <p>
 * Holds {@link ComponentMapper} instances for components used in Tesseract;
 * saves creating multiple instances and wasting memory/time.
 * </p>
 * 
 * @author Ashley Davis (SgtCoDFish)
 */
public class Mappers {
	public static ComponentMapper<Position>				position			= ComponentMapper.getFor(Position.class);

	public static ComponentMapper<Renderable>			renderable			= ComponentMapper.getFor(Renderable.class);

	public static ComponentMapper<BattleDialog>			battleDialog		=
			ComponentMapper.getFor(BattleDialog.class);

	public static ComponentMapper<Text>					text				= ComponentMapper.getFor(Text.class);

	public static ComponentMapper<Named>				named				= ComponentMapper.getFor(Named.class);

	public static ComponentMapper<MouseClickListener>	mouseClickListener	=
			ComponentMapper.getFor(MouseClickListener.class);

	public static ComponentMapper<Combatant>			combatant			= ComponentMapper.getFor(Combatant.class);

	public static ComponentMapper<Stats>				stats				= ComponentMapper.getFor(Stats.class);

	public static ComponentMapper<Dialogue>				dialogue			= ComponentMapper.getFor(Dialogue.class);

	public static ComponentMapper<FocusTaker>			focusTaker			= ComponentMapper.getFor(FocusTaker.class);

	public static ComponentMapper<TargetMarker>			targetMarker		=
			ComponentMapper.getFor(TargetMarker.class);

	public static ComponentMapper<Moving>				moving				= ComponentMapper.getFor(Moving.class);

	public static ComponentMapper<Player>				player				= ComponentMapper.getFor(Player.class);

	public static ComponentMapper<Boss>					boss				= ComponentMapper.getFor(Boss.class);

	public static ComponentMapper<Solid>                solid               = ComponentMapper.getFor(Solid.class);
	
	public static ComponentMapper<Enemy>				enemy				= ComponentMapper.getFor(Enemy.class);
	
	public static ComponentMapper<WorldPlayerInputListener> worldPlayerInputListener =
			ComponentMapper.getFor(WorldPlayerInputListener.class);
            
    public static ComponentMapper<Dimension>            dimension           = ComponentMapper.getFor(Dimension.class);
}
