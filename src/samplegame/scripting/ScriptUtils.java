package samplegame.scripting;

import toritools.entity.Entity;
import toritools.math.Vector2;

/**
 * Construction yard for various things that help users out in writing entity
 * scripts.
 * 
 * @author toriscope
 * 
 */
public class ScriptUtils {

	/**
	 * Represents the 8 directions. Use the getDirection method to grab an enum
	 * easily.
	 * 
	 * @author toriscope
	 * 
	 */
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT, UP_RIGHT, UP_LEFT, DOWN_LEFT, DOWN_RIGHT;

		/**
		 * Find the proper enum for the direction.
		 * 
		 * @param dir
		 *            direction in radians.
		 * @return the proper enum.
		 */
		public static Direction findEnum(float dir) {
			dir = (float) Math.toDegrees(dir) % 360;
			if (dir >= 337.5 || dir < 22.5)
				return Direction.RIGHT;
			if (dir >= 22.5 && dir < 67.5)
				return Direction.UP_RIGHT;
			if (dir >= 67.5 && dir < 112.5)
				return Direction.UP;
			if (dir >= 112.5 && dir < 157.5)
				return Direction.UP_LEFT;
			if (dir >= 157.5 && dir < 202.5)
				return Direction.LEFT;
			if (dir >= 202.5 && dir < 247.5)
				return Direction.DOWN_LEFT;
			if (dir >= 247.5 && dir < 292.5)
				return Direction.DOWN;
			return Direction.DOWN_RIGHT;
		}
	}
	
	public static void safeMove(final Entity e, final Vector2 delta, final Entity ... opposing){
		e.pos = e.pos.add(delta);
		e.moveOut(opposing);
	}
}
