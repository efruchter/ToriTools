package samplegame.entity.sprite;

import java.awt.Graphics;
import java.awt.Image;

import samplegame.math.Vector2;

public class Sprite {
	private int xSplit = 1, ySplit = 1, x = 0, y = 0, w, h;
	public Image image;
	private Vector2 bRight;

	public Sprite(final Image image, final int xTiles, final int yTiles) {
		this.image = image;
		this.xSplit = xTiles;
		this.ySplit = yTiles;
		w = image.getWidth(null);
		h = image.getHeight(null);
		bRight = new Vector2(w / xSplit, h / ySplit);
	}

	public void nextFrame() {
		x = ++x % xSplit;
		System.err.println(x);
	}

	public void setFrame(final int frame) {
		y = frame % ySplit;
	}

	public void setCylcle(final int cycle) {
		y = cycle % ySplit;
	}

	public void draw(Graphics g, final Vector2 pos, final Vector2 dim) {
		g.drawImage(image, (int) pos.x, (int) pos.y, (int) pos.x + (int) dim.x,
				(int) pos.y + (int) dim.y, x * (int) bRight.x, y
						* (int) bRight.y, x * (int) bRight.x
						+ (int) bRight.x, y * (int) bRight.y
						+ (int) bRight.y, null);
	}

}
