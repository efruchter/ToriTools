package snakemeleon;

import toritools.math.Vector2;

/**
 * A vat of config options, to make tweaking lots of things a simpler ordeal.
 * 
 * @author toriscope
 * 
 */
public class SnakemeleonConstants {

    public static int tongueLength = 20;

    private SnakemeleonConstants() {
    }

    /**
     * How far behind the camera should smooth/lag.
     */
    final public static int cameraLag = 12;

    /**
     * What is commonly used as gravity. I cannot see why this would have to
     * change, ever/
     */
    final public static Vector2 gravity = new Vector2(0, 9.81f / 4);

    /**
     * The entity type for harmless environmental clutter.
     */
    final public static String dynamicPropType = "dynamicProp";

    /**
     * The entity type for a hinge joint to be placed.
     */
    final public static String hingeType = "hinge";
    
    /**
     * The entity type/id for a the player.
     */
    final public static String playerTypeId = "player";
}
