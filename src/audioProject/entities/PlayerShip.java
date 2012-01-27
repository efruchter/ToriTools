package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import toritools.controls.KeyHolder;
import toritools.entity.Entity;
import toritools.entity.physics.PhysicsModule;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class PlayerShip extends Entity {

	public PlayerShip() {
		
		variables.setVar("id", "player");
		
		pos = new Vector2(100, 10);
		dim = new Vector2(30, 30);

		addScript(new EntityScriptAdapter() {

			final char UP = KeyEvent.VK_W, DOWN = KeyEvent.VK_S,
					RIGHT = KeyEvent.VK_D, LEFT = KeyEvent.VK_A,
					SHOOT = KeyEvent.VK_SPACE;

			float speed = .001f;
			
			boolean canShoot = true;

			KeyHolder keys;
			
			PhysicsModule physics;

			@Override
			public void onSpawn(Entity self) {
				keys = ScriptUtils.getKeyHolder();
				physics = new PhysicsModule(Vector2.ZERO, new Vector2(.9f), self);
			}

			@Override
			public void onUpdate(Entity self, float time) {
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
					if(canShoot = !canShoot) {
						ScriptUtils.getCurrentLevel().spawnEntity(new GoodBullet(self.getPos()));
						Entity boolet = new GoodBullet(Vector2.ZERO);
						boolet.setPos(self.getPos().add(0, self.getDim().y - boolet.getDim().y));
						ScriptUtils.getCurrentLevel().spawnEntity(boolet);
					}
				}
				
				Vector2 delta  = physics.onUpdate(time);
				
				self.setPos(self.getPos().add(delta));
				
				ScriptUtils.moveOut(self, false, ScriptUtils.getCurrentLevel().getSolids());
			}
		});

		setSprite(new AbstractSpriteAdapter() {
			
			List<Vector2> pastPos = new LinkedList<Vector2>();
			final int MAX_HISTORY = 5;

			@Override
			public void draw(Graphics g, Entity self, Vector2 position,	Vector2 dimension) {
				
				pastPos.add(0, position);
				
				if(pastPos.size() > MAX_HISTORY) {
					pastPos.remove(MAX_HISTORY);
				}
				
				int alpha = 255;
				for(Vector2 hPos: pastPos) {
					g.setColor(new Color(0,0,0, alpha));
					g.fillOval((int) hPos.x, (int) hPos.y, (int) dimension.x, (int) dimension.y);
					alpha = alpha / 2;
				}
			}
		});
	}
}
