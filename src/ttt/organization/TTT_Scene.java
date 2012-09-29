package ttt.organization;

import nu.xom.Element;
import ttt.TTT_Constants;
import ttt.io.XMLSerializeable;
import ttt.organization.managers.TTT_EntityManager;

public class TTT_Scene implements XMLSerializeable {

	public final TTT_VariableCase variables;
	public final TTT_EntityManager entities;

	public TTT_Scene() {
		variables = new TTT_VariableCase();
		entities = new TTT_EntityManager();
		variables.set(TTT_Constants.ID_KEY, "DEFAULT");
	}

	@Override
	public Element writeToElement() {
		Element e = new Element(getElementName());
		e.appendChild(variables.writeToElement());
		e.appendChild(entities.writeToElement());
		return e;
	}

	@Override
	public void assembleFromElement(Element entity) {
		variables.assembleFromElement(entity.getChildElements(
				variables.getElementName()).get(0));
		entities.assembleFromElement(entity.getChildElements(
				entities.getElementName()).get(0));
	}

	@Override
	public String getElementName() {
		return "scene";
	}
}
