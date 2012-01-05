package toritools.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

public class Level extends Entity {

	private HashMap<String, Entity> idMap = new HashMap<String, Entity>();
	public List<List<Entity>> layers = new ArrayList<List<Entity>>();

	/**
	 * The entity lists.
	 */
	public List<Entity> solids = new ArrayList<Entity>(),
			nonSolids = new ArrayList<Entity>();

	/**
	 * The type map
	 */
	private HashMap<String, List<Entity>> typeMap = new HashMap<String, List<Entity>>();

	public List<Entity> trash = new ArrayList<Entity>();
	public List<Entity> newEntities = new ArrayList<Entity>();

	private Entity viewPort;

	private void addEntity(final Entity e) {
		layers.get(e.layer).add(e);
		if (e.solid) {
			solids.add(e);
		} else {
			nonSolids.add(e);
		}
		if (!typeMap.containsKey(e.type)) {
			typeMap.put(e.type, new ArrayList<Entity>());
		}
		typeMap.get(e.type).add(e);
	}

	private void removeEntityUnsafe(final Entity e) {
		layers.get(e.layer).remove(e);
		if (e.solid)
			solids.remove(e);
		else
			nonSolids.remove(e);
		String id;
		if ((id = e.variables.getVar("id")) != null) {
			idMap.remove(id);
		}
		typeMap.get(e.type).remove(e);
	}

	public void spawnEntity(final Entity entity) {
		newEntities.add(entity);
		String id;
		if ((id = entity.variables.getVar("id")) != null) {
			idMap.put(id, entity);
		}
	}

	public void killEntity(final Entity entity) {
		trash.add(entity);
	}

	private void spawnNewEntities() {
		List<Entity> tempList = new ArrayList<Entity>(newEntities);
		newEntities.clear();
		for (Entity e : tempList) {
			addEntity(e);
		}
		for (Entity e : tempList) {
			e.onSpawn(this);
		}
	}

	private void takeOutTrash() {
		List<Entity> tempList = new ArrayList<Entity>(trash);
		trash.clear();
		for (Entity e : tempList) {
			removeEntityUnsafe(e);
		}
		for (Entity e : tempList) {
			e.onDeath(this, false);
		}
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

		if (viewPort != null) {
			for (Entity e : solids) {
				e.inView = ScriptUtils.isColliding(viewPort, e);
			}
			for (Entity e : nonSolids) {
				e.inView = ScriptUtils.isColliding(viewPort, e);
			}
		}
	}

	public void onDeath(final boolean isRoomExit) {
		onDeath(this, isRoomExit);
	}

	@Override
	public void onDeath(final Level level, final boolean isRoomExit) {
		for (Entity e : solids) {
			e.onDeath(level, isRoomExit);
		}
		for (Entity e : nonSolids) {
			e.onDeath(level, isRoomExit);
		}
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

	public List<Entity> getEntitiesWithType(final String type) {
		List<Entity> ents = typeMap.get(type);
		if (ents == null) {
			ents = new ArrayList<Entity>();
		}
		return ents;
	}

	public void setViewportData(final Vector2 pos, final Vector2 dim) {
		if (viewPort == null) {
			viewPort = new Entity();
		}
		viewPort.pos = pos;
		viewPort.dim = dim;
	}
}
