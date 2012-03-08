package toritools.entity.sprite;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;

import toritools.entity.Entity;
import toritools.math.Vector2;

/**
 * This represents the visual component of an entity.
 * 
 * @author toriscope
 * 
 */
public interface AbstractSprite {

	/**
	 * Advance the animation by one frame.
	 */
	void nextFrame();

	/**
	 * Set the current frame independent of the timestretch.
	 */
	void nextFrameAbsolute();

	void setFrame(final int frame);

	void setCycle(final int cycle);

	void set(final int frame, final int cycle);

	/**
	 * Stretch the animation by a factor.
	 * 
	 * @param timeStretch
	 */
	void setTimeStretch(final int timeStretch);

	/**
	 * Increase the dimensions of the sprite by a size.
	 * 
	 * @param sizeOffset
	 */
	void setsizeOffset(final int sizeOffset);

	/**
	 * Override this to implement your own drawing mechanism!
	 * 
	 * @param g
	 * @param self
	 * @param position
	 * @param dimension
	 */
	void draw(final Graphics g, final Entity self, final Vector2 position,
			final Vector2 dimension);

	File getImageIndex();

	Dimension getTileDimension();
	
	/**
	 * An Sprite adapter with the methods all concrete, in standard java adapter style.
	 * @author toriscope
	 *
	 */
	public static class AbstractSpriteAdapter implements AbstractSprite {
		@Override
		public void nextFrame() {}

		@Override
		public void nextFrameAbsolute() {}

		@Override
		public void setFrame(int frame) {}

		@Override
		public void setCycle(int cycle) {}

		@Override
		public void set(int frame, int cycle) {}

		@Override
		public void setTimeStretch(int timeStretch) {}

		@Override
		public void setsizeOffset(int sizeOffset) {}

		@Override
		public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {}

		@Override
		public File getImageIndex() {return null;}

		@Override
		public Dimension getTileDimension() {return null;}
	}
}
