package samplegame.entity;

import org.lwjgl.util.vector.Vector2f;

import samplegame.scripting.EntityScript;
import toritools.map.VariableCase;

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
	public VariableCase variables = new VariableCase();

	/**
	 * The control script for this entity.
	 */
	public EntityScript script;

	/*
	 * INSTANCE ENTITY VARIABLES
	 */
	public Vector2f pos, dim;
	public boolean solid;
	public String title;

	/**
	 * This variable case will be passed in containing the additional data from
	 * the xml level file, as well as entity data from the entity xml.
	 * 
	 * @param variables
	 */
	public Entity() {
		this.script = EntityScript.BLANK;
		this.pos = new Vector2f();
		this.dim = new Vector2f();
		this.solid = false;
		this.title = "DEFAULT";
	}

	/*
	 * CONTROL METHODS
	 */

	public void onSpawn(final Level world) {
		script.onSpawn(world, this);
	}

	public void onUpdate(final Level world) {
		script.onUpdate(world, this);
	}

	public void onDeath(final Level world, final boolean isRoomExit) {
		script.onDeath(world, this, isRoomExit);
	}

	public boolean isColliding(final Entity e) {
		// left of
		if (this.pos.x + this.dim.x < e.pos.x)
			return false;
		// below
		if (this.pos.y + this.dim.y < e.pos.y)
			return false;
		// right
		if (e.pos.x + e.dim.x < this.pos.x)
			return false;
		// above
		if (e.pos.y + e.dim.y < this.pos.y)
			return false;

		return true;
	}
}