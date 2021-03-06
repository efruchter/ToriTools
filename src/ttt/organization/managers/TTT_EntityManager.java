package ttt.organization.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ttt.TTT_Constants;
import ttt.io.XMLSerializeable;
import ttt.organization.TTT_Entity;

import nu.xom.Element;
import nu.xom.Elements;

public class TTT_EntityManager implements XMLSerializeable {
	private final HashMap<String, List<TTT_Entity>> entitiesByType;
	private final List<TTT_Entity> entities;
	private final HashMap<String, TTT_Entity> idMap;

	public TTT_EntityManager() {
		entitiesByType = new HashMap<String, List<TTT_Entity>>();
		entities = new LinkedList<TTT_Entity>();
		idMap = new HashMap<String, TTT_Entity>();
	}

	public void addEntity(final TTT_Entity entity) {
		for (String type : entity.types.getTypeList()) {
			if (!entitiesByType.containsKey(type)) {
				entitiesByType.put(type, new LinkedList<TTT_Entity>());
			}
			entitiesByType.get(type).add(entity);
		}
		entities.add(entity);
		if (entity.variables.has(TTT_Constants.ID_KEY)) {
			idMap.put(entity.variables.loadString(TTT_Constants.ID_KEY), entity);
		}
	}

	public void removeEntity(final TTT_Entity entity) {
		for (String type : entity.types.getTypeList()) {
			if (entitiesByType.containsKey(type)) {
				entitiesByType.get(type).remove(entity);
			}
		}
		entities.remove(entity);
		if (entity.variables.has(TTT_Constants.ID_KEY)) {
			idMap.remove(entity.variables.loadString(TTT_Constants.ID_KEY));
		}
	}

	public List<TTT_Entity> getEntitiesByType(final String... types) {
		List<TTT_Entity> en = new LinkedList<TTT_Entity>();
		for (String type : types) {
			if (entitiesByType.containsKey(type)) {
				en.addAll(entitiesByType.get(type));
			}
		}
		return en;
	}

	public TTT_Entity getEntityById(final String id) {
		return idMap.get(id);
	}

	/**
	 * Return a shallow copy of the list of entities.
	 * 
	 * @return shallow list copy
	 */
	public List<TTT_Entity> getAllEntities() {
		return new LinkedList<TTT_Entity>(entities);
	}

	public List<TTT_Entity> getAllEntitiesFast() {
		return entities;
	}

	private void clearAllEntities() {
		entities.clear();
		entitiesByType.clear();
		idMap.clear();
	}

	@Override
	public Element writeToElement() {
		Element ele = new Element(getElementName());
		for (TTT_Entity ent : entities) {
			ele.appendChild(ent.writeToElement());
		}
		return ele;
	}

	@Override
	public void assembleFromElement(Element entity) {
		clearAllEntities();
		Elements ents = entity.getChildElements();
		for (int i = 0; i < entity.getChildCount(); i++) {
			TTT_Entity e = new TTT_Entity();
			e.assembleFromElement(ents.get(i));
			addEntity(e);
		}
	}

	@Override
	public String getElementName() {
		return "entities";
	}

	public String toString() {
		return getElementName();
	}
}
