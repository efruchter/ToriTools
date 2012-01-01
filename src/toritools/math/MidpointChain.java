package toritools.math;

/**
 * Midpoint-smoothing chain of vectors that can be used to fake momentum or
 * create smooth paths. Anchors around goals on opposing sides of the chain.
 * 
 * @author toriscope
 * 
 */
public class MidpointChain {

	private Vector2[] chain;

	/**
	 * Initialize a chain
	 * 
	 * @param a
	 *            one anchor end
	 * @param b
	 *            other anchor end
	 * @param size
	 *            number of links. Add more for smoother/slower movements
	 */
	public MidpointChain(final Vector2 a, final Vector2 b, final int size) {
		this.chain = new Vector2[size];
		for (int i = 0; i < this.chain.length - 1; i++) {
			this.chain[i] = a.clone();
		}
		chain[chain.length - 1] = b;
	}

	/**
	 * Smoothes chain forward, anchored on a
	 */
	public void smoothTowardA() {
		float x, y;
		for (int i = 1; i < chain.length; i++) {
			x = (chain[i].getX() + chain[i - 1].getX()) / 2;
			y = (chain[i].getY() + chain[i - 1].getY()) / 2;
			chain[i].setX(x);
			chain[i].setY(y);
		}
	}

	/**
	 * Smoothes chain backward, anchored on b
	 */
	public void smoothTowardB() {
		float x, y;
		for (int i = chain.length - 2; i >= 0; i--) {
			x = (chain[i].getX() + chain[i + 1].getX()) / 2;
			y = (chain[i].getY() + chain[i + 1].getY()) / 2;
			chain[i].setX(x);
			chain[i].setY(y);
		}
	}

	/**
	 * Smoothes toward a and b. This will cause the entire chain to appear to
	 * "shrink" and smooth.
	 */
	public void smooth() {
		smoothTowardA();
		smoothTowardB();
	}

	public void setA(final Vector2 a) {
		chain[0] = a;
	}

	public void setB(final Vector2 b) {
		chain[chain.length - 1] = b;
	}

	public Vector2 getA() {
		return chain[0];
	}

	public Vector2 getB() {
		return chain[chain.length - 1];
	}

	public Vector2[] getChain() {
		return chain;
	}
}
