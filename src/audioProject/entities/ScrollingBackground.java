package audioProject.entities;

import static audioProject.AudioProject.getFloat;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

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
	private float speed;
	private float barAmount;
	private float prespectiveRatio;
	private float centerRatio;
	private float topSpacing;
	private float bottomSpacing;
	private float verticalOffset;
	
	public void setFocus(final Vector2 focus, final float verticalOffsetScalar) {
		centerRatio = focus.x / dim.x;
		verticalOffset = verticalOffsetScalar * (focus.y - dim.y / 2);
	}
	
	private int red = 0, green = 0, blue = 0;
	private boolean redUp = false, greenUp = false, blueUp = false;
	private int lowerColor = 128, higherColor = 150;
	
	private void cycleColors() {
		float feel = 5 * AudioProject.controller.getFeel(); 
		red = (int) (red + getFloat() * feel * (redUp ? 1 : -1));
		if (red < lowerColor)
			redUp = true;
		else if (red > higherColor)
			redUp = false;
		
		green = (int) (green + getFloat() * feel * (greenUp ? 1 : -1));
		if (green < lowerColor)
			greenUp = true;
		else if (green > higherColor)
			greenUp = false;
		
		blue = (int) (blue + getFloat() * feel * (blueUp ? 1 : -1));
		if (blue < lowerColor)
			blueUp = true;
		else if (blue > higherColor)
			blueUp = false;
		
	}
	
	private void setColors(final Color color) {
		red = color.getRed();
		green = color.getGreen();
		blue = color.getBlue();
	}

	public ScrollingBackground(Vector2 dim, int speed,  int barAmount,  float prespectiveRatio,  float centerRatio,  int topSpacing,  int bottomSpacing, final float averageSin) {
		
		this.speed = speed;
		this.barAmount = barAmount;
		this.prespectiveRatio = prespectiveRatio;
		this.centerRatio = centerRatio;
		this.topSpacing = topSpacing;
		this.bottomSpacing = bottomSpacing;
		this.verticalOffset = 0;
		
		setPos(Vector2.ZERO);
		setDim(dim);

		type = ReservedTypes.BACKGROUND.toString();
		
		layer = 9;
		
		getVariableCase().setVar("id", "bg");
		
		this.addScript(new EntityScriptAdapter(){
			@Override
			public void onUpdate(Entity self, float time, Level level) {
				setColors(ColorUtils.blend(new Color(42, 245, 235), new Color(42, 245, 130), AudioProject.controller.getFeel()));
			}
		});
		
		setSprite(new AbstractSpriteAdapter() {
			
			float time = 0;
			
			float sinTimer = 0;
			
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(new Color(232, 243, 178));
				//g.fillRect((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
				
				float spacing = dimension.x / getBarAmount();
				float center = (dimension.x * getCenterRatio());
				
				float bottomSpacingLocal = getBottomSpacing();
				float topSpacingLocal = getTopSpacing();
				
				time = (time - getSpeed()) % spacing;
				
				sinTimer += averageSin;
				
				float feel = AudioProject.controller.getFeel();
				
				//cycleColors();
				g.setColor(new Color(red, green, blue));
				
				// Middle
				for(float i = time; i < dimension.x; i+= spacing) {
					topSpacingLocal = getTopSpacing() - getTopSpacing() * (float) sin(sinTimer + i * .01 * feel);
					bottomSpacingLocal = getBottomSpacing() - getBottomSpacing() * (float) cos(sinTimer + i * .01 * feel);
					g.drawLine((int) i,(int) (topSpacingLocal - verticalOffset), (int) i, (int) (dimension.y - bottomSpacingLocal - verticalOffset));
				}
				
				// Top
				for(float i = time; i < dimension.x; i+= spacing) {
					topSpacingLocal = getTopSpacing() - getTopSpacing() * (float) sin(sinTimer + i * .01 * feel);
					bottomSpacingLocal = getBottomSpacing() - getBottomSpacing() * (float) cos(sinTimer + i * .01 * feel);
					g.drawLine((int) i, (int) (topSpacingLocal - verticalOffset), (int) (center - (center - i) * getPrespectiveRatio()), 0);
				}
				// Bottom
				for(float i = time; i < dimension.x; i+= spacing) {
					topSpacingLocal = getTopSpacing() - getTopSpacing() * (float) sin(sinTimer + i * .01 * feel);
					bottomSpacingLocal = getBottomSpacing() - getBottomSpacing() * (float) cos(sinTimer + i * .01 * feel);
					g.drawLine((int) i, (int) (dimension.y - bottomSpacingLocal - verticalOffset), (int) (center - (center - i) * getPrespectiveRatio()), (int) dimension.y);
				}
			}
		});

		addScript(new EntityScriptAdapter() {
			@Override
			public void onUpdate(Entity self, float time, Level level) {

			}
		});
	}
	
	public void setColor( Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getBarAmount() {
		return barAmount;
	}

	public void setBarAmount(float barAmount) {
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

	public float getTopSpacing() {
		return topSpacing;
	}

	public void setTopSpacing(float topSpacing) {
		this.topSpacing = topSpacing;
	}

	public float getBottomSpacing() {
		return bottomSpacing;
	}

	public void setBottomSpacing(int bottomSpacing) {
		this.bottomSpacing = bottomSpacing;
	}

	public float getVerticalOffset() {
		return verticalOffset;
	}

	public void setVerticalOffset(float verticalOffset) {
		this.verticalOffset = verticalOffset;
	}
}
