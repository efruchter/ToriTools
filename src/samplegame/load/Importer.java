package samplegame.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.Sprite;
import toritools.map.ToriMapIO;
import toritools.map.VariableCase;
import toritools.math.Vector2;
import toritools.xml.ToriXML;

public class Importer {
	/**
	 * Imports the entity template. Sets basic template stuff.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Entity importEntity(final File file,
			final HashMap<String, String> instanceMap)
			throws FileNotFoundException {
		VariableCase entityMap = ToriMapIO.readVariables(file);
		entityMap.getVariables().putAll(instanceMap);
		Entity e = new Entity();

		/**
		 * Extract the basic template data.
		 */
		// DIMENSION
		try {
			e.dim = new Vector2(Float.parseFloat(entityMap
					.getVar("dimensions.x")), Float.parseFloat(entityMap
					.getVar("dimensions.y")));
		} catch (Exception er) {
			e.dim = new Vector2();
		}
		// SOLID
		try {
			e.solid = Boolean.parseBoolean(entityMap.getVar("solid").trim());
		} catch (Exception er) {
			e.solid = false;
		}
		// TITLE
		e.title = entityMap.getVar("solid");
		e.title = e.title != null ? e.title : "DEFAULT";

		e.editor = new Sprite(new ImageIcon(file.getParent() + "/"
				+ entityMap.getVar("sprites.editor")).getImage(), 1, 1);
		String inGame = entityMap.getVar("sprites.inGame");
		if (inGame != null) {
			// The key is sprite but not editor
			String[] value = inGame.split(",");
			// 0: file, 1: x tile, 2: y tile
			e.sprite = new Sprite(new ImageIcon(file.getParent() + "/"
					+ value[0].trim()).getImage(), Integer.parseInt(value[1]
					.trim()), Integer.parseInt(value[2].trim()));
			inGame = entityMap.getVar("sprites.timeScale");
			if (inGame != null) {
				e.sprite.timeStretch = Integer.parseInt(inGame.trim());
			}
			

		} else {
			e.sprite = e.editor;
		}
		inGame = entityMap.getVar("sprites.sizeOffset");
		if (inGame != null) {
			e.sprite.sizeOffset = Integer.parseInt(inGame.trim());
		}
		inGame = entityMap.getVar("visible");
		if (inGame != null) {
			e.visible= Boolean.parseBoolean(inGame.trim());
		}
		return e;
	}

	public static Level importLevel(final File file)
			throws FileNotFoundException {
		Level level = new Level();
		Document doc = ToriXML.parse(file);
		HashMap<String, String> props = ToriMapIO.readMap(doc
				.getElementsByTagName("level").item(0).getAttributes()
				.getNamedItem("map").getNodeValue());
		level.variables.setVariables(props);

		// Extract level instance info
		// levelSize.width = Integer.parseInt(props.get("width"));
		// levelSize.height = Integer.parseInt(props.get("height"));
		File workingDirectory = file.getParentFile();

		NodeList entities = doc.getElementsByTagName("entity");
		for (int i = 0; i < entities.getLength(); i++) {
			Node e = entities.item(i);
			HashMap<String, String> mapData = ToriMapIO.readMap(e
					.getAttributes().getNamedItem("map").getNodeValue());
			// int layer = Integer.parseInt(mapData.get("layer"));
			double x = Double.parseDouble(mapData.get("position.x"));
			double y = Double.parseDouble(mapData.get("position.y"));
			File f = new File(workingDirectory + mapData.get("template"));
			Entity ent = importEntity(f, mapData);
			ent.pos = new Vector2((float) x, (float) y);
			ent.layer = Integer.parseInt(mapData.get("layer"));
			// layerEditor.setLayerVisibility(layer, true);
			ent.variables.getVariables().putAll(mapData);
			level.addEntity(ent);
			if (mapData.containsKey("id"))
				level.idMap.put(mapData.get("id"), ent);
		}
		return level;
	}
}
