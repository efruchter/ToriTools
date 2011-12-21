package samplegame.entrypoint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import samplegame.controls.KeyHolder;
import samplegame.entities.PlayerScript;
import samplegame.entities.WolfScript;
import samplegame.load.Importer;
import samplegame.scripting.EntityScript;
import samplegame.scripting.ScriptUtils;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 * 
 */
public class Game_J2d {

	/** Game title */
	private static String GAME_TITLE = "SampleGame";

	public static boolean debug = false;

	private static final Vector2 VIEWPORT = new Vector2(800, 600);

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
	}

	private static HashMap<String, String> globalVariables = new HashMap<String, String>();

	public static void setGlobalVar(final String key, final String value) {
		globalVariables.put(key, value);
	}

	public static String getGlobalVar(final String key) {
		return globalVariables.get(key);
	}

	public static JFrame frame;
	@SuppressWarnings("serial")
	public static JPanel panel = new JPanel() {
		{
			setPreferredSize(new Dimension((int) VIEWPORT.x, (int) VIEWPORT.y));
		}

		public void paintComponent(final Graphics g) {
			render(g);
		}
	};
	public static KeyHolder keys = new KeyHolder();

	/**
	 * Initialize the game
	 * 
	 * @throws Exception
	 *             if init fails
	 */
	private static void init() throws Exception {
		frame = new JFrame(GAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.addKeyListener(keys);
		frame.setFocusable(true);

		try {
			ScriptUtils.loadProfileVariables();
		} catch (Exception e) {
			ScriptUtils.saveProfileVariables();
		}

		level = Importer.importLevel(new File("levels/MoreLevel2.xml"));

		setupLevel();

		bufferImage = new BufferedImage((int) VIEWPORT.x, (int) VIEWPORT.y,
				BufferedImage.TYPE_INT_RGB);
		bufferGraphics = bufferImage.getGraphics();
	}

	private static Timer timer;

	/**
	 * Runs the game (the "main loop")
	 */
	private static void run() {
		timer = new Timer(17, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logic();
				frame.repaint();
			}
		});
		timer.start();
	}

	/**
	 * Do all calculations, handle input, etc.
	 */
	private static void logic() {

		if (newLevel != null) {
			level.onDeath(level, true);
			level = newLevel;
			setupLevel();
			newLevel = null;
		}

		level.onUpdate();

		if (keys.isPressed(KeyEvent.VK_I)) {
			zoom.x += .1;
			zoom.y += .1;
		}
		if (keys.isPressed(KeyEvent.VK_O)) {
			zoom.x -= .1;
			zoom.y -= .1;
			if (zoom.x < 1)
				zoom.set(1, 1);
		}
		debug = keys.isPressedThenRelease(KeyEvent.VK_K) ? !debug : debug; // debug
																			// control
		// Escape
		if (keys.isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
	}

	final static Vector2 zoom = new Vector2(1, 1);

	private static Image bufferImage;
	private static Graphics bufferGraphics;

	private static void render(final Graphics rootCanvas) {
		try {
			Vector2 playerPos = level.getEntityWithId("player").pos;
			Vector2 offset = VIEWPORT.scale(.5f).sub(playerPos);

			bufferGraphics.setColor(Color.BLACK);
			bufferGraphics.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
			for (int i = level.layers.size() - 1; i >= 0; i--)
				for (Entity e : level.layers.get(i)) {
					if (debug) {
						bufferGraphics.setColor(Color.RED);
						bufferGraphics.drawRect((int) (e.pos.x + offset.x),
								(int) (e.pos.y + offset.y), (int) e.dim.x,
								(int) e.dim.y);
					}
					if (e.visible)
						e.draw(bufferGraphics, offset);
				}
			/**
			 * Draw to the actual screen, scaled.
			 */
			int xScalePix = (int) (zoom.x * VIEWPORT.x);
			int yScalePix = (int) (zoom.y * VIEWPORT.y);
			rootCanvas.drawImage(bufferImage,
					-(int) ((xScalePix - VIEWPORT.x) / 2),
					-(int) ((yScalePix - VIEWPORT.y) / 2), xScalePix,
					yScalePix, null);
			rootCanvas.setColor(Color.white);
			String infoString = "[WASD] Move  | " + " [I/O] Zoom: " + zoom.x
					+ "  |  [K] Debug Mode: " + debug;

			rootCanvas.drawString(infoString, 5, (int) VIEWPORT.y - 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Level newLevel = null;

	public static void warpToLevel(final Level newLevel) {
		Game_J2d.newLevel = newLevel;
	}

	private static void setupLevel() {
		level.getEntityWithId("player").script = new PlayerScript();

		Entity temp = level.getEntityWithId("wolf");
		if (temp != null)
			temp.script = new WolfScript();

		temp = level.getEntityWithId("pushblock1");
		if (temp != null)
			temp.script = new EntityScript() {
				Entity player;

				public void onSpawn(Level level, Entity self) {
					player = level.getEntityWithId("player");
				}

				public void onUpdate(Level level, Entity self) {
					ScriptUtils.moveOut(self, player);
					ScriptUtils.moveOut(self,
							level.solids.toArray(new Entity[0]));
					ScriptUtils.moveOut(player, self);
				}

				public void onDeath(Level level, Entity self, boolean isRoomExit) {
				}
			};

		level.onSpawn();
	}
}
