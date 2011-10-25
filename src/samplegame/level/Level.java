package samplegame.level;

import java.util.LinkedList;
import java.util.List;

import samplegame.entity.Entity;

public class Level extends Entity {
	/**
	 * entity vars pos = meaningless, dim = dim of level.
	 */

	/**
	 * The entity lists.
	 */
	public List<Entity> solids = new LinkedList<Entity>(),
			nonSolids = new LinkedList<Entity>();

	public void addEntity(final Entity e) {
		String isSolid = e.variables.getVar("solid");
		if (isSolid != null && isSolid.equalsIgnoreCase("true")) {
			solids.add(e);
		} else {
			nonSolids.add(e);
		}
	}

	/**
	 * Call this each update cycle. WIll update all entities.
	 */
	public void onUpdate() {
		for (Entity e : solids) {
			e.onUpdate(this);
		}
		for (Entity e : nonSolids) {
			e.onUpdate(this);
		}
	}

	public Level() {
		super();
	}
}
