package ttt.organization;

import nu.xom.Element;
import toritools.math.Vector2;
import ttt.io.XMLSerializeable;
import ttt.organization.managers.TTT_ScriptManager;
import ttt.organization.managers.TTT_TypeManager;

public class TTT_Entity implements XMLSerializeable {
	// a view
	public final TTT_EntityView view;

	// scripts
	public final TTT_ScriptManager scripts;

	// variables
	public final TTT_VariableCase variables;

	// types
	public final TTT_TypeManager types;

	public TTT_Entity() {
		view = TTT_EntityView.DEFAULT;
		variables = new TTT_VariableCase();
		types = new TTT_TypeManager();
		scripts = new TTT_ScriptManager();
		setPos(Vector2.ZERO);
		setDim(Vector2.ZERO);
		setDir(0);
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

	public String toString() {
		return getElementName();
	}

	public int getDir() {
		return (int) variables.loadFloat("dir");
	}

	public Vector2 getPos() {
		return variables.loadVector("pos");
	}

	public Vector2 getDim() {
		return variables.loadVector("dim");
	}

	public void setDir(final int dir) {
		variables.store("dir", dir);
	}

	public void setPos(final Vector2 pos) {
		variables.store("pos", pos);
	}

	public void setDim(final Vector2 dim) {
		variables.store("dim", dim);
	}
}
