package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import audioProject.AudioProject;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.render.ColorUtils;
import toritools.scripting.EntityScript.EntityScriptAdapter;

public class ScrollingBackground extends Entity {
	
	private Color color;
	
	public void setColor(final Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	public ScrollingBackground(final Vector2 dim) {
		
		setPos(Vector2.ZERO);
		setDim(dim);

		type = ReservedTypes.BACKGROUND.toString();
		
		layer = 9;

		setSprite(new AbstractSpriteAdapter() {

			@Override
			public void nextFrame() {
				
			}

			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(getColor());
				g.fillRect((int) self.getPos().x, (int) self.getPos().y, (int) self.getDim().x, (int) self.getDim().y);
			}
		});

		addScript(new EntityScriptAdapter() {
			@Override
			public void onUpdate(Entity self, float time, Level level) {
				setColor(ColorUtils.blend(Color.CYAN, Color.PINK, Math.abs(AudioProject.controller.getFeel())));
			}
		});
	}
}
