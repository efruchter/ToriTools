package samplegame.entity;

import org.lwjgl.util.vector.Vector2f;

import samplegame.level.Level;
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
	private VariableCase variables = new VariableCase();

	/**
	 * The control script for this entity.
	 */
	private EntityScript script;

	/*
	 * INSTANCE ENTITY VARIABLES
	 */
	public Vector2f pos, dim;
	public Boolean solid;

	/**
	 * This variable case will be passed in containing the additional data from
	 * the xml level file, as well as entity data from the entity xml.
	 * 
	 * @param variables
	 */
	public Entity(final VariableCase variables, final EntityScript script) {
		this.variables = variables;
		this.script = script;
		try {
			pos = new Vector2f(
					Float.parseFloat(variables.getVar("position.x")),
					Float.parseFloat(variables.getVar("position.y")));
		} catch (Exception e) {
			pos = new Vector2f();
		}
		try {
			dim = new Vector2f(Float.parseFloat(variables
					.getVar("dimensions.x")), Float.parseFloat(variables
					.getVar("dimensions.y")));
		} catch (Exception e) {
			dim = new Vector2f();
		}
		try {
			solid = Boolean.parseBoolean(variables.getVar("solid"));
		} catch (Exception e) {
			solid = false;
		}
	}

	public String getVar(final String s) {
		return variables.getVar(s);
	}

	public void setVar(final String variable, final String value) {
		variables.setVar(variable, value);
	}

	/*
	 * CONTROL METHODS
	 */

	public void onSpawn(final Level world) {
		script.onSpawn();
	}

	public void onUpdate(final Level world) {
		script.onUpdate();
	}

	public void onDeath(final Level world) {
		script.onDeath();
	}

	public void draw() {

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