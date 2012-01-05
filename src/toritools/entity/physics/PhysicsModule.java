package toritools.entity.physics;

import toritools.entity.Entity;
import toritools.math.Vector2;

public class PhysicsModule {

	/**
	 * Current acceleration
	 */
	private Vector2 acc = new Vector2();

	/**
	 * Previous position
	 */
	private Vector2 prePos = new Vector2();

	/**
	 * Global accel.
	 */
	private final Vector2 g;

	private float gDrag;

	private final Entity self;

	public PhysicsModule(final Vector2 globalAcceleration,
			final float globalDrag, final Entity self) {
		g = globalAcceleration;
		this.self = self;
		gDrag = globalDrag;
		prePos = self.pos;
	}

	/**
	 * Calculates forces on entity, and generates the movement vector.
	 * 
	 * @param self
	 *            the entity.
	 * @return
	 */
	public Vector2 onUpdate() {

		// Add global accel
		acc = acc.add(g);

		Vector2 velocity = self.pos.sub(prePos).add(acc);

		// cap the speed
		velocity = velocity.unit().scale(velocity.mag());
		
		velocity = velocity.unit().scale(velocity.mag() * getgDrag());

		prePos = self.pos.clone();

		acc.clear();
		
		return velocity;
	}

	public void addVelocity(final Vector2 vel) {
		prePos = prePos.sub(vel);
	}
	
	public Vector2 getCurrentVelocity() {
		return self.pos.sub(prePos);
	}
	
	public void clearVelocity() {
		prePos = self.pos.clone();
	}
	
	public void clearXVelocity() {
		prePos.x = self.pos.x;
	}
	
	public void clearYVelocity() {
		prePos.y = self.pos.y;
	}
	
	public void addAcceleration(final Vector2 a) {
		acc = acc.add(a);
	}
	
	public float getMass() {
		return 1f;
	}
	
	public void setgDrag(final float drag) {
		gDrag = drag;
	}

	public float getgDrag() {
		return gDrag;
	}
}
