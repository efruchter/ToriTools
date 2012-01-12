package toritools.entity;

import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.ImageIcon;

import toritools.entity.sprite.Sprite;
import toritools.map.VariableCase;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

/**
 * A scripted entity that exists in the game world. Designed to start with some
 * basic settings. Be sure to set up the specific things you want!
 * 
 * @author toriscope
 * 
 */
public class Entity {
	
	/**
	 * Static vars for optimization.
	 */
	private static Vector2 BASE_VECT = new Vector2();
	private static Sprite BASE_SPRITE = new Sprite(new ImageIcon(
			"resources/nope.png").getImage(), 1, 1);

	/**
	 * Holds all variables for the entity.
	 */
	private VariableCase variables = new VariableCase();

	/**
	 * The control script for this entity.
	 */
	private EntityScript script = EntityScript.BLANK;

	/*
	 * INSTANCE ENTITY VARIABLES
	 */
	private Vector2 pos = BASE_VECT, dim = BASE_VECT;
	private boolean solid = false;
	private String type = "";
	private int layer = 0;
	private boolean visible = true;
	private boolean active = true;

	private Sprite sprite = BASE_SPRITE;

	private boolean inView = true;

	/*
	 * Editor variables!
	 */
	private String file = "";

	private int direction = 0;

	/**
	 * This variable case will be passed in containing the additional data from
	 * the xml level file, as well as entity data from the entity xml.
	 * 
	 * @param variables
	 */
	public Entity() {
		
	}

	public Entity(final EntityScript script) {
		this();
		this.script = script;
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

	public void draw(final Graphics g, final Vector2 offset) {
		sprite.draw(g, this, pos.add(offset), dim);
	}

	public void addVariables(final HashMap<String, String> variables) {
		this.variables.getVariables().putAll(variables);
	}

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

	public Vector2 getDim() {
		return dim;
	}

	public void setDim(Vector2 dim) {
		this.dim = dim;
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public boolean isInView() {
		return inView;
	}

	public void setInView(boolean inView) {
		this.inView = inView;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public VariableCase getVariableCase() {
		return variables;
	}

	public int getLayer() {
		return layer;
	}

	public String getFile() {
		return file;
	}

	public void setFile(final String file) {
		this.file = file;
	}

	public void setScript(EntityScript script) {
		this.script = script;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
}