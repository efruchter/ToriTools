package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;

public class Explosion extends Entity {
	
	public Explosion (final Vector2 pos, final Color color, final float startingRadius, final float time) {
		
		final float MAX_RADIUS = time + startingRadius;
		
		setPos(pos);
		
		setDim(new Vector2(startingRadius));
		
		addScript(new EntityScriptAdapter(){

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				self.setDim(self.getDim().add(2));
				self.setPos(self.getPos().sub(1));
				if (self.getDim().x >= MAX_RADIUS) {
					level.despawnEntity(self);
				}
			}
		});
		
		setSprite(new AbstractSpriteAdapter(){
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(color);
				g.drawOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}
		});
	}
	
}
