package samplegame.entity;

import java.awt.Graphics;

import samplegame.entity.sprite.Sprite;
import samplegame.math.Vector2;
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
	public Vector2 pos, dim;
	public boolean solid;
	public String title;
	public int layer;
	public boolean visible = true;

	public Sprite sprite, editor;

	/**
	 * This variable case will be passed in containing the additional data from
	 * the xml level file, as well as entity data from the entity xml.
	 * 
	 * @param variables
	 */
	public Entity() {
		this.script = EntityScript.BLANK;
		this.pos = new Vector2();
		this.dim = new Vector2();
		this.solid = false;
		this.title = "DEFAULT";
		this.layer = 0;
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

	public Entity isCollidingWithSolid(Entity... solids) {
		for (Entity e : solids) {
			if (e != this && this.isColliding(e))
				return e;
		}
		return null;
	}

	public boolean isColliding(final Entity e) {
		// left of
		if (this.pos.x + this.dim.x < e.pos.x) {
			return false;
		}
		// below
		else if (this.pos.y + this.dim.y < e.pos.y) {
			return false;
		}
		// right
		else if (e.pos.x + e.dim.x < this.pos.x) {
			return false;
		}
		// above
		else if (e.pos.y + e.dim.y < this.pos.y) {
			return false;
		}

		return true;
	}

	public void moveOutX(final Float oldX, final Entity... others) {
		moveOut(oldX, null, others);
	}

	public void moveOutY(final Float oldY, final Entity... others) {
		moveOut(null, oldY, others);
	}

	public void moveOut(final Float oldX, final Float oldY,
			final Entity... others) {
		final Entity e = this.isCollidingWithSolid(others);
		if (e != null && oldY != null) {
			float y = this.pos.y;
			float midY = y + this.dim.y / 2f;
			float oMidY = e.pos.y + e.dim.y / 2f;
			if (oMidY < midY) {
				// this on bottom
				y = e.pos.y + e.dim.y + 1;
			} else if (oMidY > midY) {
				// this on top
				y = e.pos.y - this.dim.y - 1;
			}
			this.pos.y = y;
		}
		if (e == null)
			return;
		final Entity newE = this.isCollidingWithSolid(e);
		if (newE != null && oldX != null) {
			float x = this.pos.x;
			float midX = x + this.dim.x / 2f;
			float oMidX = e.pos.x + e.dim.x / 2f;
			if (oMidX < midX) {
				// this on right
				x = newE.pos.x + newE.dim.x + 1;
			} else if (oMidX > midX) {
				// this on left
				x = newE.pos.x - this.dim.x - 1;
			}
			this.pos.x = x;
		}
	}

	public Vector2 getMid() {
		return new Vector2(pos.x + dim.x / 2, pos.y + dim.y / 2);
	}

	public void draw(final Graphics g) {
		sprite.draw(g, pos, dim);
	}

	public static enum Orientation {
		NORTH, SOUTH, WEST, EAST
	}
}