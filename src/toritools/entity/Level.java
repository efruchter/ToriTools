package toritools.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Level extends Entity {

	private HashMap<String, Entity> idMap = new HashMap<String, Entity>();
	public ArrayList<ArrayList<Entity>> layers = new ArrayList<ArrayList<Entity>>();

	/**
	 * The entity lists.
	 */
	public List<Entity> solids = new ArrayList<Entity>(),
			nonSolids = new ArrayList<Entity>();
	public List<Entity> allEntities = new ArrayList<Entity>();

	/**
	 * Add an entity, and give it to a layer.
	 * 
	 * @param e
	 *            the entity
	 * @param layer
	 *            the layer/depth.
	 */
	public void addEntity(final Entity e) {
		layers.get(e.layer).add(e);
		allEntities.add(e);
		if (e.solid) {
			solids.add(e);
		} else {
			nonSolids.add(e);
		}
		String id;
		if ((id = e.variables.getVar("id")) != null) {
			idMap.put(id, e);
		}

	}

	public void removeEntity(final Entity e) {
		layers.get(e.layer).remove(e);
		allEntities.remove(e);
		if (e.solid)
			solids.remove(e);
		else
			nonSolids.remove(e);		
		String id;
		if ((id = e.variables.getVar("id")) != null) {
			idMap.remove(id);
		}
	}

	/**
	 * Call this each update cycle. WIll update all entities.
	 */
	public void onUpdate() {
		for (Entity e : allEntities) {
			e.onUpdate(this);
		}
	}

	public Level() {
		super();
		for (int i = 0; i < 10; i++) {
			layers.add(new ArrayList<Entity>());
		}
	}

	public Entity getIntityWithId(final String id) {
		return idMap.get(id);
	}
}
