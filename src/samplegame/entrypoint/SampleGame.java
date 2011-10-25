package samplegame.entrypoint;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import samplegame.entity.Entity;
import samplegame.level.Level;
import samplegame.render.Render2D;
import samplegame.scripting.EntityScript;
import toritools.map.VariableCase;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 * 
 */
public class SampleGame {

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
		level = new Level(cas, EntityScript.BLANK);
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

		/*
		 * TEMPORARY KEYBOARD CONTROLS.
		 */
		int speed = 4;
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			test.x -= speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			test.x += speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			test.y -= speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			test.y += speed;
		}

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
