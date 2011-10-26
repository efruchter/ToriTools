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
				moveOutClosest(self, x, null, level);

				if (Keyboard.isKeyDown(Keyboard.KEY_W))
					self.pos.y -= speed;

				if (Keyboard.isKeyDown(Keyboard.KEY_S))
					self.pos.y += speed;
				// Detect and correct for y collisions
				moveOutClosest(self, null, y, level);

			}

			public void onDeath(Level level, Entity self, boolean isRoomExit) {
			}

			private void moveOutClosest(Entity self, Float oldX, Float oldY,
					final Level level) {

				Entity e = self.isCollidingWithSolid(level.solids);
				if (e != null && oldY != null) {
					float y = self.pos.y;
					float midY = y + self.dim.y / 2f;
					float oMidY = e.pos.y + e.dim.y / 2f;
					if (oMidY < midY) {
						// self on bottom
						y = e.pos.y + e.dim.y + 1;
					} else if (oMidY > midY) {
						// self on top
						y = e.pos.y - self.dim.y - 1;
					}
					self.pos.y = y;
				}
				if (e != null && oldX != null) {
					float x = self.pos.x;
					float midX = x + self.dim.x / 2f;
					float oMidX = e.pos.x + e.dim.x / 2f;
					if (oMidX < midX) {
						// self on right
						x = e.pos.x + e.dim.x + 1;
					} else if (oMidX > midX) {
						// self on left
						x = e.pos.x - self.dim.x - 1;
					}
					self.pos.x = x;
				}
			}
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
