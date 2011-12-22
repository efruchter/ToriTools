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

	/**
	 * This list is modified the moment something is staged for add/removal.
	 */
	public List<Entity> allEntities = new ArrayList<Entity>();

	private List<Entity> trash = new ArrayList<Entity>();
	private List<Entity> newEntities = new ArrayList<Entity>();

	private void addEntity(final Entity e) {
		layers.get(e.layer).add(e);
		allEntities.add(e);
		if (e.solid) {
			solids.add(e);
		} else {
			nonSolids.add(e);
		}
	}

	private void removeEntityUnsafe(final Entity e) {
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

	public void spawnEntity(final Entity entity) {
		newEntities.add(entity);
		String id;
		if ((id = entity.variables.getVar("id")) != null) {
			idMap.put(id, entity);
		}
		allEntities.add(entity);
	}

	public void killEntity(final Entity entity) {
		trash.add(entity);
		allEntities.remove(entity);

	}

	private void spawnNewEntities() {
		for (Entity e : newEntities) {
			addEntity(e);
		}
		for (Entity e : newEntities) {
			e.onSpawn(this);
		}
		newEntities.clear();
	}

	private void takeOutTrash() {
		for (Entity e : trash) {
			e.onDeath(this, false);
		}
		for (Entity e : trash) {
			removeEntityUnsafe(e);
			e.onDeath(this, false);
		}
		trash.clear();
	}

	public void onSpawn() {
		onSpawn(this);
	}

	@Override
	public void onSpawn(final Level level) {
		spawnNewEntities();
	}

	public void onUpdate() {
		onUpdate(this);
	}

	@Override
	public void onUpdate(final Level level) {
		spawnNewEntities();
		for (Entity e : solids) {
			e.onUpdate(level);
		}
		for (Entity e : nonSolids) {
			e.onUpdate(level);
		}
		takeOutTrash();
	}

	public Level() {
		super();
		for (int i = 0; i < 10; i++) {
			layers.add(new ArrayList<Entity>());
		}
	}

	public Entity getEntityWithId(final String id) {
		return idMap.get(id);
	}
}
