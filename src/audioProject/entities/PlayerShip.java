package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import toritools.additionaltypes.HistoryQueue;
import toritools.controls.KeyHolder;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.render.ColorUtils;
import toritools.render.HealthBar;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;
import audioProject.AudioProject;

public class PlayerShip extends Entity {

	public PlayerShip() {
		
		layer = 5;
		
		variables.setVar("id", "player");
		
		pos = new Vector2(100, 10);
		dim = new Vector2(20, 20);
		
		final HistoryQueue<Vector2> pastPos = new HistoryQueue<Vector2>(5);
		
		final HealthBar healthBar = new HealthBar(100, Color.RED, Color.GREEN);

		addScript(new EntityScriptAdapter() {

			final char UP = KeyEvent.VK_W, DOWN = KeyEvent.VK_S,
					RIGHT = KeyEvent.VK_D, LEFT = KeyEvent.VK_A,
					SHOOT = KeyEvent.VK_SPACE;

			float speed = .002f;
			
			int canShoot = 0;

			KeyHolder keys;
			
			PhysicsModule physics;

			@Override
			public void onSpawn(Entity self, Level level) {
				keys = ScriptUtils.getKeyHolder();
				physics = new PhysicsModule(Vector2.ZERO, new Vector2(.9f), self);
				healthBar.setHealth(100);
			}

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				
				float speed = this.speed * time;

				if (keys.isPressed(UP)) {
					physics.addAcceleration(new Vector2(0, -speed));
				}

				if (keys.isPressed(DOWN)) {
					physics.addAcceleration(new Vector2(0, speed));
				}

				if (keys.isPressed(LEFT)) {
					physics.addAcceleration(new Vector2(-speed, 0));
				}

				if (keys.isPressed(RIGHT)) {
					physics.addAcceleration(new Vector2(speed, 0));
				}

				if (keys.isPressed(SHOOT)) {
					if(canShoot-- < 0) {
						canShoot = 5;
						float spread = .2f;
						level.spawnEntity(new GoodBullet(self.getPos(), spread));
						Entity boolet = new GoodBullet(Vector2.ZERO, spread);
						boolet.setPos(self.getPos().add(0, self.getDim().y - boolet.getDim().y));
						level.spawnEntity(boolet);
					}
				}
				
				Vector2 delta  = physics.onUpdate(time);
				
				self.setPos(self.getPos().add(delta));
				
				ScriptUtils.moveOut(self, false, level.getSolids());
				
				pastPos.push(self.getPos());
				
				for (Entity badBullet : level.getEntitiesWithType("BadBullet")) {
					if (ScriptUtils.isColliding(self, badBullet)) {
						level.despawnEntity(badBullet);
						healthBar.setHealth(healthBar.getHealth() - badBullet.getVariableCase().getFloat("damage"));
					}
				}
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void draw(Graphics g, Entity self, Vector2 position,	Vector2 dimension) {
				
				healthBar.draw(g, new Vector2(10, 50), new Vector2(200, 30));
				
				Color c = ColorUtils.blend(Color.BLACK, Color.BLUE, Math.abs(AudioProject.controller.getFeel()));
				
				int alpha = 255;
				for(Vector2 hPos: pastPos) {
					g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
					g.fillOval((int) hPos.x, (int) hPos.y, (int) dimension.x, (int) dimension.y);
					alpha = alpha / 2;
				}
			}
		});
	}
}
