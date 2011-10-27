package samplegame.entrypoint;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import samplegame.entity.Entity;
import samplegame.entity.Level;
import samplegame.load.Importer;
import samplegame.render.Render2D;
import samplegame.scripting.EntityScript;
import toritools.map.VariableCase;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 * 
 */
public class Game {

	/** Game title */
	public static final String GAME_TITLE = "USE WASD!";

	/** Exit the game */
	private static boolean finished;

	/** Desired frame time */
	private static final int FRAMERATE = 60;

	public static final Vector2f BOUNDS = new Vector2f(800, 600);

	/**
	 * The current working level.
	 */
	private static Level level;

	/**
	 * Application init
	 * 
	 * @param args
	 *            Commandline args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		init();
		run();

		cleanup();
		System.exit(0);
	}

	private static HashMap<String, String> globalVariables = new HashMap<String, String>();

	public static void setGlobalVar(final String key, final String value) {
		globalVariables.put(key, value);
	}

	public static String getGlobalVar(final String key) {
		return globalVariables.get(key);
	}

	/**
	 * Initialize the game
	 * 
	 * @throws Exception
	 *             if init fails
	 */
	private static void init() throws Exception {
		/**
		 * Display windows stuff.
		 */
		Display.setTitle(GAME_TITLE);
		Display.setVSyncEnabled(true);
		Display.setDisplayMode(new DisplayMode((int) BOUNDS.getX(),
				(int) BOUNDS.getY()));
		Display.create();

		/**
		 * Create a blank level with size 1000x1000.
		 */
		VariableCase cas = new VariableCase();
		cas.setVar("dimensions.x", 1000 + "");
		cas.setVar("dimensions.y", 1000 + "");
		level = Importer.importLevel(new File("levels/MoreLevel.xml"));
		level.idMap.get("player").script = new EntityScript() {
			public void onSpawn(Level level, Entity self) {
			}

			public void onUpdate(Level level, Entity self) {
				float x = self.pos.x, y = self.pos.y;

				int speed = 3;
				if (Keyboard.isKeyDown(Keyboard.KEY_A))
					self.pos.x -= speed;
				if (Keyboard.isKeyDown(Keyboard.KEY_D))
					self.pos.x += speed;
				// Detect and correct for collisions
				self.moveOutX(x, level.solids.toArray(new Entity[0]));

				if (Keyboard.isKeyDown(Keyboard.KEY_W))
					self.pos.y -= speed;

				if (Keyboard.isKeyDown(Keyboard.KEY_S))
					self.pos.y += speed;
				// Detect and correct for y collisions
				self.moveOutY(y, level.solids.toArray(new Entity[0]));

			}

			public void onDeath(Level level, Entity self, boolean isRoomExit) {
			}

		};
		level.idMap.get("blockbuddy").script = new EntityScript() {
			public void onSpawn(Level level, Entity self) {
			}

			public void onUpdate(Level level, Entity self) {
				self.moveOut(self.pos.x, null, level.idMap.get("player"));
				self.moveOutX(self.pos.x, level.solids.toArray(new Entity[0]));
				level.idMap.get("player").moveOutX(self.pos.x, self);
			}

			public void onDeath(Level level, Entity self, boolean isRoomExit) {
			}
		};

		for (Entity e : level.nonSolids)
			e.onSpawn(level);
		Render2D.setColor(Color.RED);
		for (Entity e : level.solids)
			e.onSpawn(level);
	}

	/**
	 * Runs the game (the "main loop")
	 */
	private static void run() {
		while (!finished) {
			Display.update();
			if (Display.isCloseRequested()) {
				finished = true;
			} else if (Display.isActive()) {
				logic();
				render();
				Display.sync(FRAMERATE);
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				logic();
				if (Display.isVisible() || Display.isDirty()) {
					render();
				}
			}
		}
	}

	/**
	 * Do any game-specific cleanup
	 */
	private static void cleanup() {
		// Close the window
		Display.destroy();
	}

	/**
	 * Do all calculations, handle input, etc.
	 */
	private static void logic() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			finished = true;
		}

		for (Entity e : level.solids)
			e.onUpdate(level);
		for (Entity e : level.nonSolids)
			e.onUpdate(level);

	}

	private static void render() {
		Render2D.setup2D(BOUNDS);
		Render2D.clearScreen();
		GL11.glPushMatrix();
		Render2D.setColor(Color.BLUE);
		for (Entity e : level.nonSolids)
			Render2D.drawRect(e.pos, Vector2f.add(e.pos, e.dim, null));
		Render2D.setColor(Color.RED);
		for (Entity e : level.solids)
			Render2D.drawRect(e.pos, Vector2f.add(e.pos, e.dim, null));

		GL11.glPopMatrix();
	}
}
