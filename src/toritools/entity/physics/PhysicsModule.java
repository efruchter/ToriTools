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

	private final float gDrag;

	private final Entity self;

	public PhysicsModule(final Vector2 globalAcceleration,
			final float globalDrag, final Entity self) {
		g = globalAcceleration;
		this.self = self;
		gDrag = globalDrag;
	}

	/**
	 * Call this during the parents onStart()
	 */
	public void onStart() {
		prePos = self.pos.clone();
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
		velocity = velocity.unit().scale(Math.min(velocity.mag(), 25) * gDrag);

		prePos = self.pos.clone();

		acc.clear();

		return velocity;
	}

	public void addVelocity(final Vector2 vel) {
		prePos = prePos.sub(vel);
	}
	
	public void clearVelocity() {
		prePos = self.pos.clone();
	}
}
