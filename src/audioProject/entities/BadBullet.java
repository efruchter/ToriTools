package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.render.ColorUtils;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class BadBullet extends Entity {

	/**
	 * Build a bad booooleeeeet
	 * @param position the starting position of bullet.
	 * @param speed the delta per update.
	 */
	public BadBullet(final Vector2 position, final Vector2 speed, final float radius) {
		type = "BadBullet";

		getVariableCase().setVar("damage", "5");
		
		layer = 1;

		pos = position;
		dim = Vector2.ONE.scale(radius);
		
		final Color color = ColorUtils.blend(new Color(255, 0, 128), Color.RED, Math.random());

		addScript(new EntityScriptAdapter() {
			
			boolean explodeDeath = true;

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				
				if (self.getPos().x < level.getDim().x && !ScriptUtils.isColliding(level, self)) {
					level.despawnEntity(self);
					explodeDeath = false;
				}

				self.setPos(self.getPos().add(speed.scale(time)));
			}

			@Override
			public void onDeath(Entity self, Level level, boolean isRoomExit) {
				if (explodeDeath)
					level.spawnEntity(new Explosion(self.getPos(), color, self.getDim().x, 20));
			}
		});

		setSprite(new AbstractSpriteAdapter() {
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(color);
				g.drawOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}
		});
	}
}
