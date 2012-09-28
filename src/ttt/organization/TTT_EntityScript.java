package ttt.organization;

import toritools.entity.Entity;
import toritools.entity.Level;

/**
 * The scripts that entities load should implement this interface.
 * 
 * @author toriscope
 * 
 */
public interface TTT_EntityScript {

	/**
	 * This is called upon entity creation, after all entities have loaded.
	 * Happens once each room load, or upon dynamic spawn.
	 */
	public void onSpawn(final Entity self, final Level level);

	/**
	 * This is called upon entity update.
	 * 
	 * @param self
	 *            the entity running the script.
	 * @param time
	 *            the time in milliseconds between frame delays.
	 */
	public void onUpdate(final Entity self, final long time, final Level level);

	/**
	 * This is called upon entity deletion not including room exit.
	 * 
	 * @param self
	 *            the entity running the script
	 * @param isRoomExit
	 *            True if the room is exiting, false if the deletion was natural
	 *            (player killed it, etc.).
	 */
	public void onDeath(final Entity self, final Level level, boolean isRoomExit);

	/**
	 * Get the unique string id of the script.
	 * 
	 * @return the unique name of the script.
	 */
	public String getName();

	/**
	 * A Blank entity script.
	 */
	public static TTT_EntityScript BLANK = new TTT_EntityScriptAdapter();

	/**
	 * An Entity Script with the methods all concrete, in standard java adapter
	 * style.
	 * 
	 * @author toriscope
	 * 
	 */
	class TTT_EntityScriptAdapter implements TTT_EntityScript {
		@Override
		public void onSpawn(Entity self, final Level level) {
		}

		@Override
		public void onUpdate(Entity self, long time, final Level level) {
		}

		@Override
		public void onDeath(Entity self, final Level level, boolean isRoomExit) {
		}

		@Override
		public String getName() {
			return "DEFAULT";
		}
	}
}
