package samplegame.math;

import java.awt.Point;

/**
 * 2D Float Vector with basic math functions
 * 
 * @author efruchter
 * 
 */
public class Vector2 {

	public float x, y;

	public Vector2(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(final Vector2 base) {
		this.x = base.getX();
		this.y = base.getY();
	}

	public Vector2(final Point base) {
		this.x = base.x;
		this.y = base.y;
	}

	public Vector2() {
		this(0, 0);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public void setY(final float y) {
		this.y = y;
	}

	public Vector2 add(final Vector2 o) {
		return new Vector2(this.x + o.getX(), this.y + o.getY());
	}
	public Vector2 add(final float add) {
		return new Vector2(this.x + add, this.y + add);
	}

	public Vector2 sub(final Vector2 o) {
		return new Vector2(this.x - o.getX(), this.y - o.getY());
	}
	
	public Vector2 sub(final float o) {
		return new Vector2(this.x - o, this.y - o);
	}

	public Vector2 scale(final float scalar) {
		return new Vector2(this.x * scalar, this.y * scalar);
	}
	
	public Vector2 scale(final float x, final Float y) {
		return new Vector2(this.x * x, this.y * y);
	}

	public float dot(final Vector2 o) {
		return this.x * o.getX() + this.y * o.getY();
	}

	public Vector2 clone() {
		return new Vector2(this);
	}

	public float mag() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public Vector2 unit() {
		return this.scale(1f / this.mag());
	}

	public static Vector2 min(final Vector2 a, final Vector2 b) {
		return new Vector2(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b
				.getY()));
	}

	public static Vector2 max(final Vector2 a, final Vector2 b) {
		return new Vector2(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b
				.getY()));
	}

	public String toString() {
		return "[" + getX() + ", " + getY() + "]";
	}

	public static float dist(final Vector2 a, final Vector2 b) {
		return (float) Math.sqrt(Math.pow(a.getX() - b.getX(), 2)
				+ Math.pow(a.getY() - b.getY(), 2));
	}

	public Point toPoint() {
		return new Point((int) getX(), (int) getY());
	}

	/**
	 * Rotate the vector by a number of radians.
	 * 
	 * @param angle
	 *            the angle to rotate by.
	 * @return the rotated vector.
	 */
	public Vector2 rotate(final float angle) {
		return new Vector2((float) (x * Math.cos(angle) - y * Math.sin(angle)),
				(float) (x * Math.sin(angle) + y * Math.cos(angle)));
	}

	/**
	 * Rotate the vector by a number of degrees.
	 * 
	 * @param angle
	 *            the angle to rotate by.
	 * @return the rotated vector.
	 */
	public Vector2 rotateDeg(final float angle) {
		return rotate((float) Math.toRadians(angle));
	}

	/**
	 * Get the unit direction from start to end.
	 * 
	 * @param start
	 *            the start vector
	 * @param end
	 *            the destination vector
	 * @return the unit vector pointing from start to end
	 */
	public static Vector2 getDirectionTo(final Vector2 start, final Vector2 end) {
		return end.sub(start).unit();
	}
}
