package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class GoodBullet extends Entity {

	public GoodBullet(final Vector2 position) {
		type = "GoodBullet";

		variables.setVar("damage", 10 + "");

		pos = position;
		dim = new Vector2(10, 10);

		addScript(new EntityScriptAdapter() {

			Vector2 speed = new Vector2(.5f, -.5f + 1 * (float) Math.random());

			@Override
			public void onUpdate(Entity self, float time) {
				if (!ScriptUtils.isColliding(ScriptUtils.getCurrentLevel(), self)) {
					ScriptUtils.getCurrentLevel().despawnEntity(self);
				}
				self.setPos(self.getPos().add(speed.scale(time)));
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(Color.green);
				g.drawRect((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}

		});
	}
}
