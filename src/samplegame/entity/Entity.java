package samplegame.entity;

import samplegame.scripting.EntityScript;

/**
 * A scripted entity that exists in the game world.
 * 
 * @author toriscope
 * 
 */
public class Entity {

	/**
	 * Holds all variables for the entity.
	 */
	private VariableCase variables = new VariableCase();

	/**
	 * The control script for this entity.
	 */
	private EntityScript script;

	/**
	 * This variable case will be passed in containing the additional data from
	 * the xml level file, as well as entity data from the entity xml.
	 * 
	 * @param variables
	 */
	public Entity(final VariableCase variables, final EntityScript script) {
		this.variables = variables;
		this.script = script;
	}

	public VariableCase getVariables() {
		return variables;
	}

	/*
	 * CONTROL METHODS
	 */

	public void onSpawn(final World world) {
		script.onSpawn();
	}

	public void onUpdate(final World world) {
		script.onUpdate();
	}

	public void onDeath(final World world) {
		script.onDeath();
	}
}