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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assembleFromElement(Element entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}
}
