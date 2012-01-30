package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import audioProject.AudioProject;

import toritools.additionaltypes.HistoryQueue;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class BadShip extends Entity {

	public BadShip(final Vector2 startingPosition) {

		variables.setVar("health", "100");
		type = "enemy";

		pos = startingPosition;
		dim = new Vector2(30, 30);
		
		final HistoryQueue<Vector2> pastPos = new HistoryQueue<Vector2>(1);

		addScript(new EntityScriptAdapter() {

			float speed = .2f * (float) Math.random();
			
			boolean explodeDeath = false;

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				
				if(AudioProject.controller.isBeat()) {
					level.spawnEntity(
							new BadBullet(
									self.getPos(), 
									Vector2.toward(self.getPos(), level.getEntityWithId("player").getPos()).scale(.2f))
							);
				}

				self.setPos(self.getPos().add(-speed * time, 0));

				if (!ScriptUtils.isColliding(level, self)) {
					level.despawnEntity(self);
				} else if (variables.getFloat("health") <= 0) {
					level.despawnEntity(self);
					explodeDeath = true;
				}
				
				pastPos.push(self.getPos());
			}
			
			@Override
			public void onDeath(Entity self, Level level, boolean isRoomExit) {
				if (explodeDeath)
					level.spawnEntity(new Explosion(self.getPos(), Color.RED, self.getDim().x, 20));
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {

				int alpha = 255;
				for (Vector2 hPos : pastPos) {
					g.setColor(new Color(255, 0, 0, alpha));
					g.fillOval((int) hPos.x, (int) hPos.y, (int) dimension.x, (int) dimension.y);
					alpha = alpha / 2;
				}
			}
		});
	}
}
