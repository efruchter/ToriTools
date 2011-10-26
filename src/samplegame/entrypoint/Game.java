package samplegame.entrypoint;

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
	public static final String GAME_TITLE = "SampleGame";

	/** Exit the game */
	private static boolean finished;

	/** Desired frame time */
	private static final int FRAMERATE = 60;

	public static final Vector2f BOUNDS = new Vector2f(800, 600);

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
			public void onSpawn(Level level, Entity self) {}
			public void onUpdate(Level level, Entity self) {
				int speed = 4;
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					self.pos.x -= speed;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					self.pos.x += speed;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
					self.pos.y -= speed;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
					self.pos.y += speed;
				}
			}
			public void onDeath(Level level, Entity self, boolean isRoomExit) {}
		};
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

	private static Vector2f test = new Vector2f(20, 20);
	private static Level level;

	private static void render() {
		Render2D.setup2D(BOUNDS);
		Render2D.clearScreen();
		GL11.glPushMatrix();
		for (Entity e : level.solids)
			Render2D.drawRect(e.pos, Vector2f.add(e.pos, e.dim, null));
		for (Entity e : level.nonSolids)
			Render2D.drawRect(e.pos, Vector2f.add(e.pos, e.dim, null));
		GL11.glPopMatrix();
	}
}
