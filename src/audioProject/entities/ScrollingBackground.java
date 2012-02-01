package audioProject.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.render.ColorUtils;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import audioProject.AudioProject;

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
			
			long time = 0;
			int speed = 1;
			int barAmount = 15;
			float prespectiveRatio = 2, centerRatio = .64f;
			
			int topSpacing = 50, bottomSpacing = 100;
			
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(getColor());
				g.fillRect((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
				g.setColor(Color.GREEN);
				
				int spacing = (int) dimension.x / barAmount;
				int center = (int) (dimension.x * centerRatio);
				
				time = (time - speed) % spacing;
				
				for(int i = (int) (time); i < dimension.x; i+= spacing) {
					((Graphics2D)g).setStroke(new BasicStroke(2));
					//Middle
					g.drawLine(i, topSpacing, i, (int) dimension.y - bottomSpacing);
					//top
					g.drawLine(i, topSpacing, (int) (center - (center - i) * prespectiveRatio), 0);
					//bottom
					g.drawLine(i, (int) dimension.y - bottomSpacing, (int) (center - (center - i) * prespectiveRatio), (int) dimension.y);
				}
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
