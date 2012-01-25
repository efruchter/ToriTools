package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

public class BadShip extends Entity {

	public BadShip(final Vector2 startingPosition) {

		variables.setVar("health", "40");
		type = "enemy";

		pos = startingPosition;
		dim = new Vector2(30, 30);

		addScript(new EntityScriptAdapter() {

			float speed = .2f;

			// Entity player;
			Level level;

			@Override
			public void onSpawn(Entity self) {
				level = ScriptUtils.getCurrentLevel();
				// player = level.getEntityWithId("player");
			}

			@Override
			public void onUpdate(Entity self, float time) {

				self.setPos(self.getPos().add(-speed * time, 0));

				if (!ScriptUtils.isColliding(level, self)
						|| variables.getFloat("health") <= 0) {
					level.despawnEntity(self);
				}
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			List<Vector2> pastPos = new LinkedList<Vector2>();
			final int MAX_HISTORY = 3;

			@Override
			public void draw(Graphics g, Entity self, Vector2 position,
					Vector2 dimension) {

				pastPos.add(0, position);

				if (pastPos.size() > MAX_HISTORY) {
					pastPos.remove(MAX_HISTORY);
				}

				int alpha = 255;
				for (Vector2 hPos : pastPos) {
					g.setColor(new Color(255, 0, 0, alpha));
					g.fillOval((int) hPos.x, (int) hPos.y, (int) dimension.x,
							(int) dimension.y);
					alpha = alpha / 2;
				}
			}
		});
	}
}
