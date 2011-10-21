package samplegame.scripting;

/**
 * The scripts that entities load should implement this interface.
 * 
 * @author toriscope
 * 
 */
public interface EntityScript {

	/**
	 * This is called upon entity creation, after all entities have loaded.
	 * 
	 * @param world
	 *            the world object
	 * @param self
	 *            the entity running the script.
	 */
	public void onSpawn();

	/**
	 * This is called upon entity update.
	 * 
	 * @param world
	 *            the world object
	 * @param self
	 *            the entity running the script.
	 */
	public void onUpdate();

	/**
	 * This is called upon entity deletion.
	 * 
	 * @param world
	 *            the world object
	 * @param self
	 *            the entity running the script.
	 */
	public void onDeath();
}
