package toritools.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import toritools.entity.Entity;
import toritools.map.ToriMapIO;
import toritools.map.VariableCase;
import toritools.math.Vector2;

/**
 * Construction yard for various things that help users out in writing entity
 * scripts.
 * 
 * @author toriscope
 * 
 */
public class ScriptUtils {

	private static VariableCase profileVariables = new VariableCase();
	private final static String PROFILE = "profile.save";

	public static String getVar(final String key) {
		return profileVariables.getVar(key);
	}

	public static void setVar(final String key, final String value) {
		profileVariables.setVar(key, value);
	}

	public static void saveProfileVariables(final String prefix)
			throws IOException {
		ToriMapIO.writeMap(new File(prefix + "_" + PROFILE),
				profileVariables.getVariables());
	}

	public static void loadProfileVariables(final String prefix)
			throws FileNotFoundException {
		profileVariables = new VariableCase(ToriMapIO.readMap(new File(prefix
				+ "_" + PROFILE)));
	}

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
		if (a.pos.x + a.dim.x <= b.pos.x) {
			return false;
		}
		// below
		else if (a.pos.y + a.dim.y <= b.pos.y) {
			return false;
		}
		// right
		else if (b.pos.x + b.dim.x <= a.pos.x) {
			return false;
		}
		// above
		else if (b.pos.y + b.dim.y <= a.pos.y) {
			return false;
		}

		return true;
	}

	public static Vector2 safeMove(final Entity e, final Vector2 delta,
			final Entity... opposing) {
		e.pos = e.pos.add(delta);
		return moveOut(e, opposing);
	}

	public static Vector2 moveOut(final Entity self, final Entity... entities) {
		Vector2 delta = new Vector2();
		for (Entity entity : entities) {
			if (self != entity && isColliding(entity, self)) {
				self.pos = self.pos.add(delta = findBestVectorOut(self, entity)
						.scale(1.1f));
			}
		}
		return delta;
	}

	public static Vector2 findBestVectorOut(final Entity toMove,
			final Entity noMove) {
		Vector2 test;
		Vector2 best = new Vector2(0, noMove.pos.y
				- (toMove.pos.y + toMove.dim.y));

		test = new Vector2(0, (toMove.pos.y - (noMove.pos.y + noMove.dim.y))
				* -1);
		if (test.mag() < best.mag())
			best = test;

		test = new Vector2(noMove.pos.x - (toMove.pos.x + toMove.dim.x), 0);
		if (test.mag() < best.mag())
			best = test;

		test = new Vector2((toMove.pos.x - (noMove.pos.x + noMove.dim.x)) * -1,
				0);
		if (test.mag() < best.mag())
			best = test;

		return best;
	}
}
