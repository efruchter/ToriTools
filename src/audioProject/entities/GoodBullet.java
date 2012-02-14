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
import audioProject.AudioProject;

public class GoodBullet extends Entity {

	/**
	 * Build a good booooleeeeet
	 * @param position the starting position of bullet.
	 * @param spreadFactor the spread factor. 0 is no spread.
	 */
	public GoodBullet(final Vector2 position, final float spreadFactor) {
		type = "GoodBullet";

		layer = 1;

		pos = position;
		dim = new Vector2(10, 10);
		
		final Color color = ColorUtils.blend(Color.BLACK, Color.BLUE, Math.abs(AudioProject.controller.getFeel()));

		addScript(new EntityScriptAdapter() {
			
			float damage = 5;
			
			boolean explodeDeath = false;

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				
				Vector2 speed = new Vector2(.5f, spreadFactor * (float) (-.5f + Math.random()));
				
				if (!ScriptUtils.isColliding(level, self)) {
					level.despawnEntity(self);
				}

				self.setPos(self.getPos().add(speed.scale(time)));

				for (Entity enemy : level.getEntitiesWithType("enemy")) {
					if (ScriptUtils.isColliding(self, enemy)) {
						level.despawnEntity(self);
						enemy.getVariableCase().setVar("health", enemy.getVariableCase().getFloat("health") - damage + "");
						explodeDeath = true;
						break;
					}
				}
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
				g.fillOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}
		});
	}
}
