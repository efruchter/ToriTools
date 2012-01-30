package toritools.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import toritools.controls.KeyHolder;
import toritools.entity.Entity;
import toritools.entity.Level;
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
	private static KeyHolder keyHolder = new KeyHolder();
	private static boolean debugMode = false;
	
	private static Level level;
	private static Level newLevel;

	public static KeyHolder getKeyHolder() {
		return keyHolder;
	}

	public static void setKeyHolder(final KeyHolder keyHolder) {
		ScriptUtils.keyHolder = keyHolder;
	}

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
		if (a.getPos().x + a.getDim().x <= b.getPos().x) {
			return false;
		}
		// below
		else if (a.getPos().y + a.getDim().y <= b.getPos().y) {
			return false;
		}
		// right
		else if (b.getPos().x + b.getDim().x <= a.getPos().x) {
			return false;
		}
		// above
		else if (b.getPos().y + b.getDim().y <= a.getPos().y) {
			return false;
		}

		return true;
	}

	public static boolean isColliding(final Entity a, final List<Entity> b) {
		for (Entity e : b) {
			if (e != a && isColliding(e, a))
				return true;
		}
		return false;
	}

	public static Vector2 moveOut(final Entity self,
			final boolean disregardOutOfView, final Entity entity) {
		Vector2 delta = new Vector2();
		if (self != entity) {
			if (!(disregardOutOfView && !entity.isInView())
					&& isColliding(entity, self)) {
				self.setPos(self.getPos().add(
						delta = findBestVectorOut(self, entity).scale(1.1f)));
			}

		}
		return delta;
	}

	public static Vector2 moveOut(final Entity self,
			final boolean disregardOutOfView, final List<Entity> entities) {
		Vector2 delta = new Vector2();
		for (Entity entity : entities)
			if (self != entity) {
				if (!(disregardOutOfView && !entity.isInView())
						&& isColliding(entity, self)) {
					self.setPos(self.getPos()
							.add(delta = findBestVectorOut(self, entity).scale(
									1.1f)));
				}

			}
		return delta;
	}

	public static Vector2 findBestVectorOut(final Entity toMove,
			final Entity noMove) {
		Vector2 test;
		Vector2 best = new Vector2(0, noMove.getPos().y
				- (toMove.getPos().y + toMove.getDim().y));

		test = new Vector2(0,
				(toMove.getPos().y - (noMove.getPos().y + noMove.getDim().y))
						* -1);
		if (test.mag() < best.mag())
			best = test;

		test = new Vector2(noMove.getPos().x
				- (toMove.getPos().x + toMove.getDim().x), 0);
		if (test.mag() < best.mag())
			best = test;

		test = new Vector2(
				(toMove.getPos().x - (noMove.getPos().x + noMove.getDim().x))
						* -1, 0);
		if (test.mag() < best.mag())
			best = test;

		return best;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	public static void setDebugMode(boolean debugMode) {
		ScriptUtils.debugMode = debugMode;
	}

	public static Level getCurrentLevel() {
		return level;
	}
	
	/**
	 * Queue a level switch. If there is no current level, then it is switched to automatically.
	 * 
	 * @param newLevel
	 *            the level to switch to.
	 */
	public static void queueLevelSwitch(final Level newLevel) {
		ScriptUtils.newLevel = newLevel;
		System.out.println("New level queued.");
	}
	
	public static boolean isLevelQueued() {
		return newLevel != null;
	}
	
	/**
	 * Move to the queued level.
	 */
	public static void moveToQueuedLevel() {
		System.out.println("Moving to the queued level.");
		keyHolder.clearKeys();
		level = newLevel;
		newLevel = null;
	}
}
