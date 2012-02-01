package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.render.ColorUtils;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import audioProject.AudioProject;

/**
 * A background with controllable perspective and speed.
 * @author toriscope
 *
 */
public class ScrollingBackground extends Entity {
	
	private Color color;
	private int speed;
	private int barAmount;
	private float prespectiveRatio;
	private float centerRatio;
	private int topSpacing;
	private int bottomSpacing;

	public ScrollingBackground(Vector2 dim, int speed,  int barAmount,  float prespectiveRatio,  float centerRatio,  int topSpacing,  int bottomSpacing) {
		
		this.speed = speed;
		this.barAmount = barAmount;
		this.prespectiveRatio = prespectiveRatio;
		this.centerRatio = centerRatio;
		this.topSpacing = topSpacing;
		this.bottomSpacing = bottomSpacing;
		
		setPos(Vector2.ZERO);
		setDim(dim);

		type = ReservedTypes.BACKGROUND.toString();
		
		layer = 9;
		
		getVariableCase().setVar("id", "bg");
		
		setSprite(new AbstractSpriteAdapter() {
			
			long time = 0;
			
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(getColor());
				g.fillRect((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
				g.setColor(Color.GREEN);
				
				int spacing = (int) dimension.x / getBarAmount();
				int center = (int) (dimension.x * getCenterRatio());
				
				time = (time - getSpeed()) % spacing;
				
				for(int i = (int) (time); i < dimension.x; i+= spacing) {
					//Middle
					g.drawLine(i, getTopSpacing(), i, (int) dimension.y - getBottomSpacing());
					//top
					g.drawLine(i, getTopSpacing(), (int) (center - (center - i) * getPrespectiveRatio()), 0);
					//bottom
					g.drawLine(i, (int) dimension.y - getBottomSpacing(), (int) (center - (center - i) * getPrespectiveRatio()), (int) dimension.y);
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
	
	public void setColor( Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getBarAmount() {
		return barAmount;
	}

	public void setBarAmount(int barAmount) {
		this.barAmount = barAmount;
	}

	public float getPrespectiveRatio() {
		return prespectiveRatio;
	}

	public void setPrespectiveRatio(float prespectiveRatio) {
		this.prespectiveRatio = prespectiveRatio;
	}

	public float getCenterRatio() {
		return centerRatio;
	}

	public void setCenterRatio(float centerRatio) {
		this.centerRatio = centerRatio;
	}

	public int getTopSpacing() {
		return topSpacing;
	}

	public void setTopSpacing(int topSpacing) {
		this.topSpacing = topSpacing;
	}

	public int getBottomSpacing() {
		return bottomSpacing;
	}

	public void setBottomSpacing(int bottomSpacing) {
		this.bottomSpacing = bottomSpacing;
	}
}
