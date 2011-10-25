package samplegame.entity;

import java.io.File;
import java.io.FileNotFoundException;

import org.lwjgl.util.vector.Vector2f;

import toritools.map.ToriMapIO;
import toritools.map.VariableCase;

public class EntityImporter {
	/**
	 * Imports the entity template. Sets basic template stuff.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Entity importEntity(final File file)
			throws FileNotFoundException {
		VariableCase entityMap = ToriMapIO.readVariables(file);
		Entity e = new Entity();

		/**
		 * Extract the basic template data.
		 */
		// DIMENSION
		try {
			e.dim = new Vector2f(Float.parseFloat(entityMap
					.getVar("dimensions.x")), Float.parseFloat(entityMap
					.getVar("dimensions.y")));
		} catch (Exception er) {
			e.dim = new Vector2f();
		}
		// SOLID
		try {
			e.solid = Boolean.parseBoolean(entityMap.getVar("solid"));
		} catch (Exception er) {
			e.solid = false;
		}
		// TITLE
		e.title = entityMap.getVar("solid");
		e.title = e.title != null ? e.title : "DEFAULT";

		return e;
	}

	public static Object importLevel(final File levelFile) {
		return null;
	}
}
