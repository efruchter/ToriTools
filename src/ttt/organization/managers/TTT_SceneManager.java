package ttt.organization.managers;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import nu.xom.Element;
import nu.xom.Elements;
import ttt.TTT_Constants;
import ttt.io.XMLSerializeable;
import ttt.organization.TTT_Scene;

public class TTT_SceneManager implements XMLSerializeable {

	private HashMap<String, TTT_Scene> scenes;

	public TTT_SceneManager() {
		scenes = new HashMap<String, TTT_Scene>();
	}

	public TTT_Scene getScene(final String id) {
		return scenes.get(id);
	}

	public void addScene(final TTT_Scene scene) {
		scenes.put(scene.variables.getString(TTT_Constants.LEVEL_ID), scene);
	}

	@Override
	public Element writeToElement() {
		Element e = new Element(getElementName());
		for (Entry<String, TTT_Scene> scene : scenes.entrySet()) {
			e.appendChild(scene.getValue().writeToElement());
		}
		return e;
	}

	@Override
	public void assembleFromElement(Element entity) {
		scenes.clear();
		Elements children = entity.getChildElements();
		for (int i = 0; i < children.size(); i++) {
			Element scene = children.get(i);
			TTT_Scene ts = new TTT_Scene();
			ts.assembleFromElement(scene);
			if (!ts.variables.hasVariable(TTT_Constants.LEVEL_ID)) {
				ts.variables.setString(TTT_Constants.LEVEL_ID, JOptionPane
						.showInputDialog("Please supply a scene name:"));
			}
			scenes.put(ts.variables.getString(TTT_Constants.LEVEL_ID), ts);
		}

	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return "scenes";
	}

}
