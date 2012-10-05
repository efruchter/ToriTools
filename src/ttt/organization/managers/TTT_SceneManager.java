package ttt.organization.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import nu.xom.Element;
import nu.xom.Elements;
import ttt.TTT_Constants;
import ttt.io.XMLSerializeable;
import ttt.organization.TTT_Scene;

public class TTT_SceneManager implements XMLSerializeable {

	private final HashMap<String, TTT_Scene> scenes;
	private final List<TTT_Scene> sceneList;
	private TTT_Scene currentSceneRef;

	public TTT_SceneManager() {
		scenes = new HashMap<String, TTT_Scene>();
		sceneList = new ArrayList<TTT_Scene>();
	}

	private TTT_Scene getScene(final String id) {
		return scenes.get(id);
	}

	public List<TTT_Scene> getSceneList() {
		return sceneList;
	}

	public void addScene(final TTT_Scene scene) {
		String key = scene.variables.loadString(TTT_Constants.ID_KEY);
		while (scenes.containsKey(key)) {
			key += "+";
		}
		scene.variables.store(TTT_Constants.ID_KEY, key);
		scenes.put(scene.variables.loadString(TTT_Constants.ID_KEY), scene);
		if (!sceneList.contains(scene)) {
			sceneList.add(scene);
		}
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
		sceneList.clear();
		Elements children = entity.getChildElements();
		for (int i = 0; i < children.size(); i++) {
			Element scene = children.get(i);
			TTT_Scene ts = new TTT_Scene();
			ts.assembleFromElement(scene);
			if (!ts.variables.has(TTT_Constants.ID_KEY)) {
				ts.variables.store(TTT_Constants.ID_KEY, JOptionPane
						.showInputDialog("Please supply a scene name:"));
			}
			addScene(ts);
		}

	}

	@Override
	public String getElementName() {
		return "scenes";
	}

	public TTT_Scene getCurrentScene() {
		return currentSceneRef;
	}

	public void moveToBlankScene() {
		currentSceneRef = new TTT_Scene();
	}

	public void moveToScene(final String id) {
		TTT_Scene s = getScene(id);
		if (s != null) {
			currentSceneRef = s;
		} else {
			moveToBlankScene();
		}
	}

	public String toString() {
		return getElementName();
	}
}
