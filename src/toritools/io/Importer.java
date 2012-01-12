package toritools.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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

	private static HashMap<File, Image> imageCache = new HashMap<File, Image>();

	/**
	 * Check to see if an image is cached, and get the reference if it is.
	 * Otherwise, cache it.
	 * 
	 * @param file
	 *            file of image
	 * @param image
	 *            the image reference.
	 * @return the image reference if cached.
	 */
	public static Image cacheImage(final File file, final Image image) {
		if (imageCache.containsKey(file)) {
			return imageCache.get(file);
		} else {
			imageCache.put(file, image);
			return image;
		}
	}

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
		if (instanceMap != null)
			entityMap.getVariables().putAll(instanceMap);
		Entity e = new Entity();

		e.getVariableCase().getVariables().putAll(entityMap.getVariables());

		e.setFile(file.getPath());

		/**
		 * Extract the basic template data.
		 */
		// DIMENSION
		try {
			e.setDim(new Vector2(Float.parseFloat(entityMap
					.getVar("dimensions.x")), Float.parseFloat(entityMap
					.getVar("dimensions.y"))));
		} catch (Exception er) {
			e.setDim(new Vector2());
		}
		// SOLID
		try {
			e.setSolid(Boolean.parseBoolean(entityMap.getVar("solid").trim()));
		} catch (Exception er) {
			e.setSolid(false);
		}

		// ID
		String id;
		if ((id = entityMap.getVar("id")) != null) {
			e.getVariableCase().setVar("id", id);
		}

		// TITLE
		e.setType(entityMap.getVar("type"));
		if (e.getType() == null)
			e.setType("DEFAULT");

		String inGame = entityMap.getVar("sprite.sheet");
		if (inGame != null) {
			// The key is sprite but not editor
			String[] value = inGame.split(",");
			int x = 1, y = 1;
			if (value.length != 1) {
				x = Integer.parseInt(value[1].trim());
				y = Integer.parseInt(value[2].trim());
			}
			// 0: file, 1: x tile, 2: y tile
			File spriteFile = new File(file.getParent() + "/" + value[0].trim());
			if (spriteFile.canRead()) {
				e.setSprite(new Sprite(cacheImage(
						new File(spriteFile.getAbsolutePath()), new ImageIcon(
								spriteFile.getAbsolutePath()).getImage()), x, y));

			}
			inGame = entityMap.getVar("sprite.timeScale");
			if (inGame != null) {
				e.getSprite().timeStretch = Integer.parseInt(inGame.trim());
			}

		}
		inGame = entityMap.getVar("sprite.sizeOffset");
		if (inGame != null) {
			e.getSprite().sizeOffset = Integer.parseInt(inGame.trim());
		}
		inGame = entityMap.getVar("visible");
		if (inGame != null) {
			e.setVisible(Boolean.parseBoolean(inGame.trim()));
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
		level.getVariableCase().setVariables(props);
		level.getDim().x = level.getVariableCase().getFloat("width");
		level.getDim().y = level.getVariableCase().getFloat("height");

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
			float x = Float.parseFloat(mapData.get("position.x"));
			float y = Float.parseFloat(mapData.get("position.y"));
			if (mapData.get("type") != null
					&& mapData.get("type").equals("WALL")) {
				float w = Float.parseFloat(mapData.get("dimensions.x"));
				float h = Float.parseFloat(mapData.get("dimensions.y"));
				Entity wall = makeWall(new Vector2(x, y), new Vector2(w, h));
				wall.setLayer(Integer.parseInt(mapData.get("layer")));
				wall.getVariableCase().getVariables().putAll(mapData);
				level.spawnEntity(wall);
			} else if (mapData.get("type") != null
					&& mapData.get("type").equals("BACKGROUND")) {
				float w = Float.parseFloat(mapData.get("dimensions.x"));
				float h = Float.parseFloat(mapData.get("dimensions.y"));

				File imageFile = new File(workingDirectory
						+ mapData.get("image"));

				Image image = cacheImage(new File(imageFile.getAbsolutePath()),
						new ImageIcon(imageFile.getAbsolutePath()).getImage());

				int xTile = Integer.parseInt(mapData.get("xTile"));
				int yTile = Integer.parseInt(mapData.get("yTile"));

				int xTiles = Integer.parseInt(mapData.get("xTiles"));
				int yTiles = Integer.parseInt(mapData.get("yTiles"));

				Entity background = makeBackground(new Vector2(x, y),
						new Vector2(w, h), image, mapData.get("image"), xTile,
						yTile, xTiles, yTiles);
				background.setLayer(Integer.parseInt(mapData.get("layer")));
				background.getVariableCase().getVariables().putAll(mapData);
				level.spawnEntity(background);
			} else {
				File f = new File(workingDirectory + mapData.get("template"));
				Entity ent = importEntity(f, mapData);
				ent.setPos(new Vector2((float) x, (float) y));
				ent.setLayer(Integer.parseInt(mapData.get("layer")));
				// layerEditor.setLayerVisibility(layer, true);
				ent.getVariableCase().getVariables().putAll(mapData);
				ent.setFile(f.getPath());
				level.spawnEntity(ent);
			}
		}
		return level;
	}

	public static Entity makeBackground(final Vector2 pos, final Vector2 dim,
			final Image image, final String relativeLink, final int x,
			final int y, final int xTiles, final int yTiles) {
		Entity bg = new Entity();
		bg.setPos(pos.clone());
		bg.setDim(dim.clone());

		bg.getVariableCase().setVar("xTiles", xTiles + "");
		bg.getVariableCase().setVar("yTiles", yTiles + "");
		bg.getVariableCase().setVar("xTile", x + "");
		bg.getVariableCase().setVar("yTile", y + "");
		bg.getVariableCase().setVar("image", relativeLink);

		bg.getVariableCase().setVar("dimensions.x", dim.x + "");
		bg.getVariableCase().setVar("dimensions.y", dim.y + "");
		bg.setType("BACKGROUND");
		bg.getVariableCase().setVar("type", bg.getType());
		bg.setSprite(new Sprite(image, xTiles, yTiles));
		bg.getSprite().setFrame(x);
		bg.getSprite().setCycle(y);
		return bg;
	}

	public static Entity makeWall(final Vector2 pos, final Vector2 dim) {
		Entity wall = new Entity();
		wall.setPos(pos.clone());
		wall.setDim(dim.clone());
		wall.getVariableCase().setVar("dimensions.x", dim.x + "");
		wall.getVariableCase().setVar("dimensions.y", dim.y + "");
		wall.setSolid(true);
		wall.getVariableCase().setVar("solid", "true");
		wall.setType("WALL");
		wall.getVariableCase().setVar("type", "WALL");
		wall.setVisible(false);
		wall.getVariableCase().setVar("visible", "false");
		wall.setSprite(new Sprite() {
			@Override
			public void draw(final Graphics g, final Entity self,
					final Vector2 pos, final Vector2 dim) {
				((Graphics2D) g).setStroke(new BasicStroke(2));
				g.setColor(Color.RED);
				g.drawLine((int) pos.x, (int) pos.y, (int) pos.x + (int) dim.x,
						(int) pos.y + (int) dim.y);
				g.drawLine((int) pos.x, (int) pos.y + (int) dim.y, (int) pos.x
						+ (int) dim.x, (int) pos.y);
				g.draw3DRect((int) pos.x, (int) pos.y, (int) dim.x,
						(int) dim.y, true);
			}
		});
		return wall;
	}
}
