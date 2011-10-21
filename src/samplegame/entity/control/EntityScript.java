package samplegame.entity.control;

import samplegame.entity.Entity;
import samplegame.entity.World;

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
	void onSpawn(World world, Entity self);

	/**
	 * This is called upon entity update.
	 * 
	 * @param world
	 *            the world object
	 * @param self
	 *            the entity running the script.
	 */
	void onUpdate(World world, Entity self);

	/**
	 * This is called upon entity deletion.
	 * 
	 * @param world
	 *            the world object
	 * @param self
	 *            the entity running the script.
	 */
	void onDeath(World world, Entity self);
}
