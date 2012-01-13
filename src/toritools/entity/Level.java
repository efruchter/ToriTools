package toritools.entity;

import java.awt.Image;
import java.awt.image.BufferedImage;
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
		layers.get(e.getLayer()).add(e);
		if (e.isSolid()) {
			solids.add(e);
		}
		else {
			nonSolids.add(e);
		}
		if (!typeMap.containsKey(e.getType())) {
			typeMap.put(e.getType(), new ArrayList<Entity>());
		}
		typeMap.get(e.getType()).add(e);
	}

	private void removeEntityUnsafe(final Entity e) {
		layers.get(e.getLayer()).remove(e);
		if (e.isSolid())
			solids.remove(e);
		else
			nonSolids.remove(e);
		String id;
		if ((id = e.getVariableCase().getVar("id")) != null) {
			idMap.remove(id);
		}
		typeMap.get(e.getType()).remove(e);
	}

	public void spawnEntity(final Entity entity) {
		newEntities.add(entity);
		String id;
		if ((id = entity.getVariableCase().getVar("id")) != null) {
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
			e.onSpawn();
		}
	}

	private void takeOutTrash() {
		List<Entity> tempList = new ArrayList<Entity>(trash);
		trash.clear();
		for (Entity e : tempList) {
			removeEntityUnsafe(e);
		}
		for (Entity e : tempList) {
			e.onDeath(false);
		}
	}

	@Override
	public void onSpawn() {
		spawnNewEntities();
	}

	@Override
	public void onUpdate() {
		spawnNewEntities();
		for (Entity e : solids) {
			e.onUpdate();
		}
		for (Entity e : nonSolids) {
			e.onUpdate();
		}
		takeOutTrash();

		if (viewPort != null) {
			for (Entity e : solids) {
				e.setInView(ScriptUtils.isColliding(viewPort, e));
				specialActions(e);
			}
			for (Entity e : nonSolids) {
				e.setInView(ScriptUtils.isColliding(viewPort, e));
				specialActions(e);
			}
		}
	}

	private void specialActions(final Entity e) {
		// Scrolling
		String data;
		if ((data = e.getVariableCase().getVar("vScroll")) != null) {
			float val = Float.parseFloat(data);
			e.getPos().y += val;
			if (ScriptUtils.isColliding(e, solids)) {
				e.getVariableCase().setVar("vScroll", -val + "");
			}
		}
		if ((data = e.getVariableCase().getVar("hScroll")) != null) {
			float val = Float.parseFloat(data);
			e.getPos().x += val;
			if (ScriptUtils.isColliding(e, solids)) {
				e.getVariableCase().setVar("hScroll", -val + "");
			}
		}
	}

	@Override
	public void onDeath(final boolean isRoomExit) {
		for (Entity e : solids) {
			e.onDeath(isRoomExit);
		}
		for (Entity e : nonSolids) {
			e.onDeath(isRoomExit);
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

	public List<Entity> getEntitiesWithType(final String... types) {
		List<Entity> ents = new ArrayList<Entity>();
		for (String type : types) {
			List<Entity> tE = typeMap.get(type);
			if (tE != null)
				ents.addAll(tE);
		}
		return ents;
	}

	public void setViewportData(final Vector2 pos, final Vector2 dim) {
		if (viewPort == null) {
			viewPort = new Entity();
		}
		viewPort.setPos(pos);
		viewPort.setDim(dim);
	}

	private Image baked;

	public Image preBakeBackground() {
		baked = new BufferedImage((int) getDim().x, (int) getDim().y,
				BufferedImage.TYPE_INT_ARGB);
		for (Entity e : nonSolids) {
			if ("BACKGROUND".equals(e.getType())) {
				killEntity(e);
				e.draw(baked.getGraphics(), new Vector2());
			}
		}
		return baked;
	}

	public Image getBakedBackground() {
		return baked;
	}
}
