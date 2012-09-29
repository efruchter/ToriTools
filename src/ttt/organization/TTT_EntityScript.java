package ttt.organization;

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
	void onSpawn(TTT_Entity self, TTT_Scene scene);

	/**
	 * This is called upon entity update.
	 * 
	 * @param self
	 *            the entity running the script.
	 * @param time
	 *            the time in milliseconds between frame delays.
	 */
	void onUpdate(TTT_Entity self, TTT_Scene scene, long timeDelta);

	/**
	 * This is called upon entity deletion not including room exit.
	 * 
	 * @param self
	 *            the entity running the script
	 * @param isRoomExit
	 *            True if the room is exiting, false if the deletion was natural
	 *            (player killed it, etc.).
	 */
	void onDeath(TTT_Entity self, TTT_Scene scene, boolean isRoomExit);

	/**
	 * Get the unique string id of the script.
	 * 
	 * @return the unique name of the script.
	 */
	String getName();

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
	public static class TTT_EntityScriptAdapter implements TTT_EntityScript {
		@Override
		public void onSpawn(TTT_Entity self, TTT_Scene scene) {
		}

		@Override
		public void onUpdate(TTT_Entity self, TTT_Scene scene, long timeDelta) {
		}

		@Override
		public void onDeath(TTT_Entity self, TTT_Scene scene, boolean isRoomExit) {
		}

		@Override
		public String getName() {
			return "DEFAULT";
		}
	}
}
