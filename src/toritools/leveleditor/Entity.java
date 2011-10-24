package toritools.leveleditor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;

/**
 * A placed object in the level editor. Packaged scoped because Entity is a
 * really popular class name.
 * 
 * @author toriscope
 * 
 */
class Entity {
	private File file;
	private Image image;
	private Point2D pos;
	private Point2D dim;

	public Entity(File file, Image img, final Point2D pos, final Point2D dim) {
		this.file = file;
		this.image = img;
		this.pos = pos;
		this.dim = dim;
	}

	public File getFile() {
		return file;
	}

	public void setfile(File file) {
		this.file = file;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Point2D getPos() {
		return pos;
	}

	public void setPos(Point2D pos) {
		this.pos = pos;
	}

	public void draw(Graphics g) {
		g.drawImage(image, (int) pos.getX(), (int) pos.getY(),
				(int) dim.getX(), (int) dim.getY(), null);
	}

	public Point2D getDim() {
		return dim;
	}

	public void setDim(Point2D dim) {
		this.dim = dim;
	}
}