package samplegame;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * Full screen currently runs by default.  I have yet to implement windowed mode.
 * 
 * Note the Graphics class is Graphics2D, not Graphics3D.
 * This inconsistency is not yet corrected in current versions of Java, so we must check the superclass.
 * Exempli gratia:
 * 		if (g instanceof Graphics2D)
 {
 Graphics2D g2 = (Graphics2D)g;
 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 }g.drawString("abcdefghijklmnopqrstuvwxyz.", 200, 200);
 * 
 * @author toriscope
 */

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import samplegame.customScripts.PlayerScript;
import samplegame.customScripts.WolfScript;
import samplegame.customScripts.WorldPortalScript;
import toritools.controls.KeyHolder;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class SampleGame {
	private int resolutionWidth = Toolkit.getDefaultToolkit().getScreenSize().width,
			resolutionHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	private final Vector2 VIEWPORT = new Vector2(resolutionWidth,
			resolutionHeight);
	/**
	 * The current working level.
	 */
	private Level level;
	public static Level newLevel;
	public static boolean debug = false;
	public Vector2 zoom = new Vector2(1, 1);
	public static KeyHolder keys = new KeyHolder();

	/**
	 * Application initiation
	 * 
	 * @param args
	 *            Commandline arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SampleGame sampleGame = new SampleGame();

		sampleGame.init();
		sampleGame.run();
	}

	/**
	 * Interface to video card/chip for hardware acceleration
	 */
	private GraphicsDevice gd;

	/**
	 * JFrame with game title
	 */
	private JFrame frame = new JFrame("SampleGame");
	@SuppressWarnings("serial")
	private JPanel panel = new JPanel() {
		public void paintComponent(final Graphics g) {
			render(g);
		}
	};

	/**
	 * Initialize the game
	 * 
	 * @throws Exception
	 *             if init fails
	 */
	public void init() throws IOException {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		gd = graphicsEnvironment.getDefaultScreenDevice();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.addKeyListener(keys);
		frame.setFocusable(true);

		try {
			ScriptUtils.loadProfileVariables();
		} catch (Exception e) {
			ScriptUtils.saveProfileVariables();
		}

		// First level to load.
		level = Importer.importLevel(new File("levels/MoreLevel.xml"));

		setupLevel();

		setFullScreen(true);
	}

	private Timer timer;

	/**
	 * Runs the game (the "main loop")
	 */
	private void run() {
		/*
		 * This is not 60 fps, but really close!
		 */
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
	private void logic() {
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
		if (keys.isPressed(KeyEvent.VK_ESCAPE)) // escape key. Potentially to be
												// used for menu access.
		{
			setFullScreen(false);
			System.exit(0);
		}
	}

	private void setupLevel() {
		/*
		 * This custom script attaching for player, wolf and block will soon by
		 * nullified by a the Rhino module.
		 */
		level.getEntityWithId("player").script = new PlayerScript();

		Entity temp = level.getEntityWithId("wolf");
		if (temp != null)
			temp.script = new WolfScript();

		temp = level.getEntityWithId("pushblock1");
		if (temp != null) {
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
		}

		// Set up world portals.
		for (Entity e : level.allEntities) {
			if (e.type.equals("worldPortal")) {
				e.script = new WorldPortalScript();
			}
		}

		level.onSpawn();
	}

	private Image bufferImage = new BufferedImage((int) VIEWPORT.x,
			(int) VIEWPORT.y, BufferedImage.TYPE_INT_RGB);;
	private Graphics bufferGraphics = bufferImage.getGraphics();

	private void render(final Graphics rootCanvas) {
		if (level == null) {
			rootCanvas.clearRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
			rootCanvas.setColor(Color.BLACK);
			rootCanvas.drawString("Loading...", (int) VIEWPORT.x / 2,
					(int) VIEWPORT.y / 2);
			return;
		}

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
		// Draw to the actual screen, scaled.
		int xScalePix = (int) (zoom.x * VIEWPORT.x);
		int yScalePix = (int) (zoom.y * VIEWPORT.y);
		rootCanvas.drawImage(bufferImage,
				-(int) ((xScalePix - VIEWPORT.x) / 2),
				-(int) ((yScalePix - VIEWPORT.y) / 2), xScalePix, yScalePix,
				null);
		rootCanvas.setColor(Color.white);
		String infoString = "[WASD] Move  | " + " [I/O] Zoom: " + zoom.x
				+ "  |  [K] Debug Mode: " + debug + "  |  [Esc] Quit";

		rootCanvas.drawString(infoString, 5, (int) VIEWPORT.y - 5);
	}

	/**
	 * Display mode of user's monitor. 32-bit color depth is used.
	 */
	private DisplayMode displayMode = new DisplayMode(resolutionWidth,
			resolutionHeight, 32, DisplayMode.REFRESH_RATE_UNKNOWN);

	/**
	 * Sets full screen or reverts screen to normal. In the future, this method
	 * will be able to restore to windowed mode and not simply hide it.
	 * 
	 * To my knowledge, full screen only supports one monitor, for I have no
	 * other monitors to test with.
	 * 
	 * @param isFullScreen
	 *            Enable (true) or disable (false) full screen mode?
	 */
	public void setFullScreen(boolean isFullScreen) {
		if (isFullScreen) {
			frame.setUndecorated(true);
			frame.setResizable(false);
			gd.setFullScreenWindow(frame);

			if (displayMode != null && gd.isDisplayChangeSupported()) {
				try {
					gd.setDisplayMode(displayMode);
				} catch (Exception e) {
					System.out.println("Error: Could not set display mode!");
				}
			}
		} else {
			Window window = gd.getFullScreenWindow();

			if (window != null) {
				window.dispose(); // dispose resources when closed
			}
			gd.setFullScreenWindow(null); // actual reverting of window
		}
	}

	/**
	 * On next update, switch to a new level.
	 * 
	 * @param newLevel
	 *            the level to switch to.
	 */
	public static void warpToLevel(final Level newLevel) {
		SampleGame.newLevel = newLevel;
	}
}
