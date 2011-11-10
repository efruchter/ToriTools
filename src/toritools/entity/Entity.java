package toritools.entity;

import java.awt.Graphics;
import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;

import samplegame.scripting.EntityScript;
import toritools.entity.sprite.Sprite;
import toritools.map.VariableCase;
import toritools.math.Vector2;

/**
 * A scripted entity that exists in the game world. Designed to start with some
 * basic settings. Be sure to set up the specific things you want!
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
	 * Static vars for optimization.
	 */
	private static Vector2 BASE_VECT = new Vector2();
	private static Sprite BASE_SPRITE = new Sprite(new ImageIcon(
			"resources/nope.png").getImage(), 1, 1);

	/*
	 * Editor variables!
	 */
	public File file;

	/**
	 * This variable case will be passed in containing the additional data from
	 * the xml level file, as well as entity data from the entity xml.
	 * 
	 * @param variables
	 */
	public Entity() {
		script = EntityScript.BLANK;
		pos = BASE_VECT;
		dim = pos;
		solid = false;
		title = "DEFAULT";
		layer = 0;
		sprite = editor = BASE_SPRITE;
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

	public void moveOut(final Entity... entities) {
		Vector2 aMid = getMid();
		for (Entity b : entities) {
			if (this == b || !this.isColliding(b))
				continue;
			Vector2 bMid = b.getMid();
			float xBridge = aMid.sub(bMid).x;
			float yBridge = aMid.sub(bMid).y;			
			
			// X
			float xSign = xBridge < 0 ? -1 : 1;
			xBridge = xSign * (b.dim.x / 2 + this.dim.x / 2 + 1);
			this.pos.x = bMid.x + xBridge - this.dim.x / 2;

			// Y
			float ySign = yBridge < 0 ? -1 : 1;
			yBridge = ySign * (b.dim.y / 2 + this.dim.y / 2 + 1);
			this.pos.y = bMid.y + xBridge - this.dim.y / 2;
		}
	}

	public Vector2 getMid() {
		return new Vector2(pos.x + dim.x / 2, pos.y + dim.y / 2);
	}

	public void draw(final Graphics g, final Vector2 offset) {
		sprite.draw(g, pos.add(offset), dim);
	}

	public void addVariables(final HashMap<String, String> variables) {
		this.variables.getVariables().putAll(variables);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}