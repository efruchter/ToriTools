package ttt.organization;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Element;
import toritools.entity.sprite.AbstractSprite;
import ttt.io.XMLSerializeable;
import ttt.organization.managers.TTT_TypeManager;

public class TTT_Entity implements XMLSerializeable {
	// a view
	public final AbstractSprite sprite;

	// scripts
	public final List<TTT_EntityScript> scripts;

	// variables
	public final TTT_VariableCase variables;

	// types
	public final TTT_TypeManager types;

	public TTT_Entity() {
		sprite = AbstractSprite.DEFAULT;
		scripts = new LinkedList<TTT_EntityScript>();
		variables = new TTT_VariableCase();
		types = new TTT_TypeManager();
	}

	@Override
	public Element writeToElement() {
		Element e = new Element(getElementName());
		e.appendChild(variables.writeToElement());
		e.appendChild(types.writeToElement());
		return e;
	}

	@Override
	public void assembleFromElement(Element entity) {
		variables.assembleFromElement(entity.getChildElements(
				variables.getElementName()).get(0));
		types.assembleFromElement(entity.getChildElements(
				types.getElementName()).get(0));

	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "entity";
	}
}
