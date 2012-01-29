package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.additionaltypes.HistoryQueue;
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
		
		final HistoryQueue<Vector2> pastPos = new HistoryQueue<Vector2>(3);

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
				
				pastPos.push(self.getPos());
			}

			@Override
			public void onDeath(Entity self, boolean isRoomExit) {
				ScriptUtils.getCurrentLevel().spawnEntity(new Explosion(self.getPos(), new Color(255, 128, 0), self.getDim().x, 20));
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {

				int alpha = 255;
				for (Vector2 hPos : pastPos) {
					g.setColor(new Color(255, 128, 0, alpha));
					g.fillOval((int) hPos.x, (int) hPos.y, (int) dimension.x, (int) dimension.y);
					alpha = alpha / 2;
				}
			}
		});
	}
}
