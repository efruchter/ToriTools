package toritools.entity.sprite;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import toritools.entity.Entity;
import toritools.math.Vector2;

public class Sprite {
	private int xSplit = 1, ySplit = 1, x = 0, y = 0;// w, h;
	private Image image;

	private Vector2 bRight;
	public int timeStretch = 1;
	public float sizeOffset = 0;

	/**
	 * Create a sprite with given image;
	 * 
	 * @param image
	 *            an image
	 * @param xTiles
	 *            tiles win x direction
	 * @param yTiles
	 *            tiles in y direction
	 */
	public Sprite(final Image image, final int xTiles, final int yTiles) {
		this.image = image;
		this.xSplit = xTiles;
		this.ySplit = yTiles;
		bRight = new Vector2(image.getWidth(null) / xSplit,
				image.getHeight(null) / ySplit);
	}

	/**
	 * Use this constructor if you plan on overriding draw(); Everything will be
	 * null.
	 */
	public Sprite() {

	}

	public void nextFrame() {
		x = ++x % (xSplit * timeStretch);
	}
	
	public void nextFrameAbsolute() {
		int timeStretch = 1;
		x = ++x % (xSplit * timeStretch);
	}

	public void setFrame(final int frame) {
		x = frame * timeStretch % (xSplit * timeStretch);
	}

	public void setCycle(final int cycle) {
		y = cycle % ySplit;
	}

	public void set(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Override this to implement your own drawing mechanism!
	 * 
	 * @param g
	 * @param self
	 * @param position
	 * @param dimension
	 */
	public void draw(final Graphics g, final Entity self,
			final Vector2 position, final Vector2 dimension) {
		int x = this.x / timeStretch;
		Vector2 dim = dimension.add(sizeOffset * 2);
		Vector2 pos = position.sub(sizeOffset);

		if (self.direction != 0) {
			BufferedImage bimage = new BufferedImage((int) dim.x, (int) dim.y,
					BufferedImage.TYPE_INT_ARGB);

			bimage.getGraphics().drawImage(image, (int) 0, (int) 0,
					(int) dim.x, (int) dim.y, x * (int) bRight.x,
					y * (int) bRight.y, x * (int) bRight.x + (int) bRight.x,
					y * (int) bRight.y + (int) bRight.y, null);

			AffineTransform affineTransform = new AffineTransform();
			// rotate with the anchor point as the mid of the image
			affineTransform.translate(pos.x, pos.y);
			affineTransform.rotate(Math.toRadians(self.direction), dim.x / 2,
					dim.y / 2);

			((Graphics2D) g).drawImage(bimage, affineTransform, null);
		} else {
			g.drawImage(image, (int) pos.x, (int) pos.y,
					(int) (pos.x + dim.x), (int) (pos.y + dim.y), x * (int) bRight.x,
					y * (int) bRight.y, x * (int) bRight.x + (int) bRight.x,
					y * (int) bRight.y + (int) bRight.y, null);
		}
	}

	public Image getImage() {
		return image;
	}

	public Dimension getTileDimension() {
		return new Dimension(x, y);
	}
}
