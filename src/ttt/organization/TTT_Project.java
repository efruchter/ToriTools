package ttt.organization;

import nu.xom.Element;
import ttt.io.XMLSerializeable;
import ttt.organization.managers.TTT_SceneManager;

public class TTT_Project implements XMLSerializeable {

	public final TTT_SceneManager sceneManager;
	public final TTT_VariableCase variables;

	public TTT_Project() {
		sceneManager = new TTT_SceneManager();
		variables = new TTT_VariableCase();
	}

	@Override
	public Element writeToElement() {
		Element e = new Element(getElementName());
		e.appendChild(sceneManager.writeToElement());
		e.appendChild(variables.writeToElement());
		return e;
	}

	@Override
	public void assembleFromElement(Element entity) {
		sceneManager.assembleFromElement(entity.getChildElements(
				sceneManager.getElementName()).get(0));
		variables.assembleFromElement(entity.getChildElements(
				variables.getElementName()).get(0));
	}

	@Override
	public String getElementName() {
		return "project";
	}

}
