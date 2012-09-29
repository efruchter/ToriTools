package ttt.organization;

import nu.xom.Element;
import toritools.entity.sprite.AbstractSprite;
import ttt.io.XMLSerializeable;
import ttt.organization.managers.TTT_ScriptManager;
import ttt.organization.managers.TTT_TypeManager;

public class TTT_Entity implements XMLSerializeable {
	// a view
	public final AbstractSprite sprite;

	// scripts
	public final TTT_ScriptManager scripts;

	// variables
	public final TTT_VariableCase variables;

	// types
	public final TTT_TypeManager types;

	public TTT_Entity() {
		sprite = AbstractSprite.DEFAULT;
		variables = new TTT_VariableCase();
		types = new TTT_TypeManager();
		scripts = new TTT_ScriptManager();
	}

	@Override
	public Element writeToElement() {
		Element e = new Element(getElementName());
		e.appendChild(variables.writeToElement());
		e.appendChild(types.writeToElement());
		e.appendChild(scripts.writeToElement());
		return e;
	}

	@Override
	public void assembleFromElement(Element entity) {
		variables.assembleFromElement(entity.getChildElements(
				variables.getElementName()).get(0));
		types.assembleFromElement(entity.getChildElements(
				types.getElementName()).get(0));
		scripts.assembleFromElement(entity.getChildElements(
				scripts.getElementName()).get(0));

	}

	@Override
	public String getElementName() {
		return "entity";
	}
}
