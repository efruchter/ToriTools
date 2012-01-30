package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class BadBullet extends Entity {

	/**
	 * Build a bad booooleeeeet
	 * @param position the starting position of bullet.
	 * @param speed the delta per update.
	 */
	public BadBullet(final Vector2 position, final Vector2 speed) {
		type = "BadBullet";

		layer = 1;

		pos = position;
		dim = new Vector2(10, 10);
		
		final Color color = new Color(255, 128, 0);

		addScript(new EntityScriptAdapter() {

			@Override
			public void onUpdate(Entity self, float time) {
				
				if (!ScriptUtils.isColliding(ScriptUtils.getCurrentLevel(), self)) {
					ScriptUtils.getCurrentLevel().despawnEntity(self);
				}

				self.setPos(self.getPos().add(speed.scale(time)));

				if (ScriptUtils.isColliding(self, ScriptUtils.getCurrentLevel().getEntityWithId("player"))) {
					ScriptUtils.getCurrentLevel().despawnEntity(self);
				}
			}

			@Override
			public void onDeath(Entity self, boolean isRoomExit) {
				ScriptUtils.getCurrentLevel().spawnEntity(new Explosion(self.getPos(), color, self.getDim().x, 20));
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
