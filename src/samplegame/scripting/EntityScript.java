package samplegame.scripting;

import toritools.entity.Entity;
import toritools.entity.Level;

/**
 * The scripts that entities load should implement this interface.
 * 
 * @author toriscope
 * 
 */
public interface EntityScript {

	/**
	 * This is called upon entity creation, after all entities have loaded.
	 * Happens once each room load, or upon dynamic spawn.
	 */
	public void onSpawn(final Level level, final Entity self);

	/**
	 * This is called upon entity update.
	 * 
	 * @param world
	 *            the world object
	 * @param self
	 *            the entity running the script.
	 */
	public void onUpdate(final Level level, final Entity self);

	/**
	 * This is called upon entity deletion not including room exit.
	 * 
	 * @param world
	 *            the world object
	 * @param self
	 *            the entity running the script
	 * @param isRoomExit
	 *            True if the room is exiting, false if the deletion was natural
	 *            (player killed it, etc.).
	 */
	public void onDeath(final Level level, final Entity self, boolean isRoomExit);

	/**
	 * A Blank entity script.
	 */
	public static EntityScript BLANK = new EntityScript() {
		@Override
		public void onSpawn(Level level, Entity self) {}
		@Override
		public void onUpdate(Level level, Entity self) {}
		@Override
		public void onDeath(Level level, Entity self, boolean isRoomExit) {}
	};
}
