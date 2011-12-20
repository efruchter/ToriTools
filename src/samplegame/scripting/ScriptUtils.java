package samplegame.scripting;

import java.util.Arrays;

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

	public static boolean isColliding(final Entity a, final Entity b) {
		// left of
		if (a.pos.x + a.dim.x < b.pos.x) {
			return false;
		}
		// below
		else if (a.pos.y + a.dim.y < b.pos.y) {
			return false;
		}
		// right
		else if (b.pos.x + b.dim.x < a.pos.x) {
			return false;
		}
		// above
		else if (b.pos.y + b.dim.y < a.pos.y) {
			return false;
		}

		return true;
	}

	public static void safeMove(final Entity e, final Vector2 delta,
			final Entity... opposing) {
		e.pos = e.pos.add(delta);
		moveOut(e, opposing);
	}

	public static void moveOut(final Entity self, final Entity... entities) {
		for (Entity entity : entities) {
			if (self != entity && isColliding(entity, self)) {
				self.pos = self.pos.add(findBestVectorOut(self, entity).scale(
						1.1f));
			}
		}
	}

	public static Vector2 findBestVectorOut(final Entity toMove,
			final Entity noMove) {
		Vector2[] v = new Vector2[4];
		v[0] = new Vector2(0, noMove.pos.y - (toMove.pos.y + toMove.dim.y));
		v[1] = new Vector2(0, (toMove.pos.y - (noMove.pos.y + noMove.dim.y)) * -1);
		v[2] = new Vector2(noMove.pos.x - (toMove.pos.x + toMove.dim.x), 0);
		v[3] = new Vector2((toMove.pos.x - (noMove.pos.x + noMove.dim.x)) * -1, 0);
		Arrays.sort(v);
		return v[0];
	}
}
