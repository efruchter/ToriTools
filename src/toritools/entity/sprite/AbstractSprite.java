package toritools.entity.sprite;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import toritools.entity.Entity;
import toritools.math.Vector2;

public interface AbstractSprite {

	void nextFrame();

	void nextFrameAbsolute();

	void setFrame(final int frame);

	void setCycle(final int cycle);

	void set(final int frame, final int cycle);

	void setTimeStretch(final int timeStretch);

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

	Image getImage();

	Dimension getTileDimension();
}
