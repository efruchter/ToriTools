package samplegame.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import samplegame.entity.Entity;
import samplegame.entity.Level;
import samplegame.math.Vector2;
import toritools.map.ToriMapIO;
import toritools.map.VariableCase;
import toritools.xml.ToriXML;

public class Importer {
	/**
	 * Imports the entity template. Sets basic template stuff.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Entity importEntity(final File file, final HashMap<String, String> instanceMap)
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

		// Editor sprite : more later!
		e.spriteMap.put("editor", new ImageIcon(file.getParent() + "/"
				+ entityMap.getVar("sprites.editor")).getImage());

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
