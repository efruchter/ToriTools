package toritools.render;

import java.awt.Color;
import java.awt.Graphics;

import toritools.math.Vector2;

/**
 * A health bar that blends two colors as it goes.
 * @author toriscope
 *
 */
public class HealthBar{

	private final Color minColor, maxColor;
	
	private final float maxHealth;
	private float health = 0;

	public HealthBar(final float maxHealth, final Color minColor, final Color maxColor) {
		this.minColor = minColor;
		this.maxColor = maxColor;
		this.maxHealth = this.health = maxHealth;
	}

	public void draw(Graphics g, final Vector2 pos, final Vector2 dim) {
		float ratio = health / maxHealth;
		g.setColor(ColorUtils.blend(maxColor, minColor, ratio));
		g.fillRect((int) pos.x, (int) pos.y, (int) (dim.x * ratio), (int) dim.y);
		g.setColor(Color.BLACK);
		g.drawRect((int) pos.x, (int) pos.y, (int) dim.x, (int) dim.y);
	}
	
	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}
}
