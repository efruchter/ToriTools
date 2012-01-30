package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator;
import toritools.scripting.EntityScript.EntityScriptAdapter;

public class BadShipFactory {

	public static Entity makePathedEnemy(final HermiteKeyFrameInterpolator path, final float aggressionRatio) {

		final Entity entity = new Entity();
		entity.setDim(new Vector2(50, 50));
		entity.setType("enemy");

		entity.addScript(new EntityScriptAdapter() {

			float allTime;

			@Override
			public void onSpawn(Entity self, Level level) {
				allTime = 0;
				self.setPos(path.getPositionDeltaAtTime(0));
				self.getVariableCase().setVar("health", "100");
			}

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				allTime += time;
				if (allTime > path.getEndTime() || self.getVariableCase().getFloat("health") <= 0) {
					level.despawnEntity(self);
				} else {
					self.setPos(path.getPositionDeltaAtTime(allTime));
				}
			}
		});
		
		entity.setSprite(new AbstractSpriteAdapter() {
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(Color.RED);
				g.fillOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}
		});

		return entity;
	}

}