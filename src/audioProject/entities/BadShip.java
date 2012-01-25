package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

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
			
			//Entity player;
			Level level;


			@Override
			public void onSpawn(Entity self) {
				level = ScriptUtils.getCurrentLevel();
				//player = level.getEntityWithId("player");
			}

			@Override
			public void onUpdate(Entity self, float time) {
				
				self.setPos(self.getPos().add(-speed * time, 0));
				
				if(!ScriptUtils.isColliding(level, self) || variables.getFloat("health") <= 0) {
					level.despawnEntity(self);
				}
			}
		});

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void draw(Graphics g, Entity self, Vector2 position,	Vector2 dimension) {
				g.setColor(Color.RED);
				g.drawOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}
		});
	}
}
