package samplegame.scripting;

import org.lwjgl.util.vector.Vector2f;

import samplegame.entity.Entity;

public class CollisionResult {
	public Entity self, other;
	public Vector2f closestOut;
	public boolean collision = false;
}
