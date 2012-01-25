package audioProject.entities;

import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.ReservedTypes;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript.EntityScriptAdapter;

public class ScrollingBackground extends Entity {
	public ScrollingBackground() {

		type = ReservedTypes.BACKGROUND.toString();
		
		layer = 9;

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void nextFrame() {
				// TODO Auto-generated method stub

			}

			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.draw3DRect(10, 20, 300, 300, true);
			}
		});

		addScript(new EntityScriptAdapter() {
			@Override
			public void onSpawn(Entity self) {
				self.getSprite().nextFrame();
			}
		});
	}
}
