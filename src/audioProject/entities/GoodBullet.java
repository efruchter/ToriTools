package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.additionaltypes.HistoryQueue;
import toritools.entity.Entity;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class GoodBullet extends Entity {

	public GoodBullet(final Vector2 position) {
		type = "GoodBullet";

		layer = 1;

		pos = position;
		dim = new Vector2(10, 10);
		
		final HistoryQueue<Vector2> pastPos = new HistoryQueue<Vector2>(3);

		addScript(new EntityScriptAdapter() {

			float damage = 5;

			Vector2 speed = new Vector2(.5f, 0);

			@Override
			public void onUpdate(Entity self, float time) {
				if (!ScriptUtils.isColliding(ScriptUtils.getCurrentLevel(), self)) {
					ScriptUtils.getCurrentLevel().despawnEntity(self);
				}

				self.setPos(self.getPos().add(speed.scale(time)));

				for (Entity enemy : ScriptUtils.getCurrentLevel().getEntitiesWithType("enemy")) {
					if (ScriptUtils.isColliding(self, enemy)) {
						ScriptUtils.getCurrentLevel().despawnEntity(self);
						enemy.getVariableCase().setVar("health", enemy.getVariableCase().getFloat("health") - damage + "");
						break;
					}
				}
				
				pastPos.push(self.getPos());
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {

				int alpha = 255;
				for (Vector2 hPos : pastPos) {
					g.setColor(new Color(0, 255, 0, alpha));
					g.fillOval((int) hPos.x, (int) hPos.y, (int) dimension.x, (int) dimension.y);
					alpha = alpha / 2;
				}
			}
		});
	}
}
