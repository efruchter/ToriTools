package toritools.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Level extends Entity {

	public HashMap<String, Entity> idMap = new HashMap<String, Entity>();
	public ArrayList<ArrayList<Entity>> layers = new ArrayList<ArrayList<Entity>>();

	/**
	 * The entity lists.
	 */
	public List<Entity> solids = new LinkedList<Entity>(),
			nonSolids = new LinkedList<Entity>();
	

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
		if (e.solid) {
			solids.add(e);
		} else {
			nonSolids.add(e);
		}

	}

	public void removeEntity(final Entity e) {
		layers.get(e.layer).remove(e);
		if (e.solid)
			solids.remove(e);
		else
			nonSolids.remove(e);
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
		for(int i = 0; i< 10; i++){
			layers.add(new ArrayList<Entity>());
		}
	}
}
