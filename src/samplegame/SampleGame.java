/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * Windowed mode does not yet work as intended.  Issue with VIEWPORT and render()
 * 
 * Note the Graphics class is Graphics2D, not Graphics3D.  This inconsistency is not yet
 * corrected in current versions of Java, so we must check the superclass.
 * Exempli gratia:
 * 		if (g instanceof Graphics2D) {
 Graphics2D g2 = (Graphics2D)g;
 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 }g.drawString("abcdefghijklmnopqrstuvwxyz.", 200, 200);
 * 
 * @author toriscope
 */
package samplegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;

import samplegame.customscripts.PlayerScript;
import samplegame.customscripts.WolfScript;
import samplegame.customscripts.WorldPortalScript;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class SampleGame extends Binary {
	
	public static void main(String[] args) {
		new SampleGame();
	}

	public static String savePrefix = "secondchance";
	public Vector2 zoom = new Vector2(1, 1);
	public static boolean inDialog = false;
	private static String displayString = "";

	@Override
	protected boolean render(Graphics rootCanvas) {
		try {
			Level level = ScriptUtils.getCurrentLevel();
			Vector2 playerPos = level.getEntityWithId("player").getPos();
			Vector2 offset = VIEWPORT.scale(.5f).sub(playerPos);

			rootCanvas.setColor(Color.BLACK);
			rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
			for (int i = level.getLayers().size() - 1; i >= 0; i--)
				for (Entity e : level.getLayers().get(i)) {
					if (e.isVisible() && e.isInView())
						e.draw(rootCanvas, offset);
					if (!"BACKGROUND".equals(e.getType())
							&& ScriptUtils.isDebugMode()) {
						rootCanvas.setColor(Color.RED);
						rootCanvas.drawRect((int) (e.getPos().x + offset.x),
								(int) (e.getPos().y + offset.y),
								(int) e.getDim().x, (int) e.getDim().y);
					}

				}

			rootCanvas.setColor(Color.white);
			String infoString = "[WASD] Move" + "  |  [K] Debug Mode: "
					+ ScriptUtils.isDebugMode() + "  |  [Esc] Quit";

			rootCanvas.drawString(infoString, 5, (int) VIEWPORT.y - 5);

			if (displayString != null) {
				rootCanvas.drawString(displayString, (int) VIEWPORT.x / 2,
						(int) VIEWPORT.y / 2 + 64);
			}
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void initialize() {

	}

	@Override
	protected void globalLogic() {
		displayString = null;

		ScriptUtils.setDebugMode(ScriptUtils.getKeyHolder()
				.isPressedThenRelease(KeyEvent.VK_K) ? !ScriptUtils
				.isDebugMode() : ScriptUtils.isDebugMode());

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
	}

	@Override
	protected void setupCurrentLevel(final Level level) {
		/*
		 * This custom script attaching for player, wolf and block will soon by
		 * nullified by a the Rhino module.
		 */
		level.getEntityWithId("player").setScript(new PlayerScript());

		Entity temp = level.getEntityWithId("wolf");
		if (temp != null)
			temp.setScript(new WolfScript());

		temp = level.getEntityWithId("pushblock1");
		if (temp != null) {
			temp.setScript(new EntityScript() {
				Entity player;

				public void onSpawn(Entity self) {
					player = level.getEntityWithId("player");
				}

				public void onUpdate(Entity self) {
					ScriptUtils.moveOut(self, true, player);
					ScriptUtils.moveOut(self, true, level.getSolids());
					ScriptUtils.moveOut(player, true, self);
				}

				public void onDeath(Entity self, boolean isRoomExit) {
				}
			});
		}

		// Set up world portals.
		for (Entity e : level.getEntitiesWithType("worldPortal")) {
			e.setScript(new WorldPortalScript());
		}

	}

	@Override
	protected File getStartingLevel() {
		return new File("levels/MoreLevel.xml");
	}

	// private int resolutionWidth =
	// Toolkit.getDefaultToolkit().getScreenSize().width,
	// resolutionHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
	// private final Vector2 VIEWPORT = new Vector2(resolutionWidth,
	// resolutionHeight);
	// /**
	// * The current working level.
	// */
	// private Level level;
	// public static Level newLevel
	//
	// /**
	// * Application initiation
	// *
	// * @param args
	// * Commandline arguments
	// * @throws Exception
	// */
	// public static void main(String[] args) throws Exception {
	//
	// if (System.getProperty("os.name").contains("Windows ")) {
	// System.setProperty("sun.java2d.d3d", "True");
	// System.setProperty("sun.java2d.accthreshold", "0");
	// } else {
	// System.setProperty("sun.java2d.opengl=true", "True");
	// }
	//
	// SampleGame sampleGame = new SampleGame();
	//
	// sampleGame.init();
	// sampleGame.run();
	// }
	//
	// /**
	// * Interface to video card/chip for hardware acceleration
	// */
	// private GraphicsDevice gd;
	//
	// /**
	// * JFrame with game title
	// */
	// private JFrame frame = new JFrame("SampleGame");
	// @SuppressWarnings("serial")
	// private JPanel panel = new JPanel() {
	// public void paintComponent(final Graphics g) {
	// render(g);
	// }
	// };
	// Cursor hiddenCursor = Toolkit.getDefaultToolkit().createCustomCursor(
	// new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
	// new Point(0, 0), "Hidden Cursor");
	//
	// /**
	// * Initialize the game
	// *
	// * @throws Exception
	// * if init fails
	// */
	// public void init() throws IOException {
	// GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
	// .getLocalGraphicsEnvironment();
	// gd = graphicsEnvironment.getDefaultScreenDevice();
	//
	// frame.setCursor(hiddenCursor); // Hide cursor by default
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
	// frame.add(panel);
	// frame.addKeyListener(ScriptUtils.getKeyHolder());
	// frame.setFocusable(true);
	//
	// try {
	// ScriptUtils.loadProfileVariables(savePrefix);
	// } catch (Exception e) {
	// ScriptUtils.saveProfileVariables(savePrefix);
	// }
	//
	// // First level to load.
	// level = Importer.importLevel(new File("levels/MoreLevel.xml"));
	//
	// setupLevel();
	//
	// setFullScreen(true);
	//
	// frame.requestFocusInWindow();
	// }
	//
	// private java.util.Timer timer;
	//
	// /**
	// * Runs the game (the "main loop")
	// */
	// private void run() {
	// /*
	// * This is not 60 fps, but really close!
	// */
	// timer = new java.util.Timer();
	// timer.scheduleAtFixedRate(new TimerTask() {
	//
	// @Override
	// public void run() {
	// logic();
	// frame.repaint();
	// }
	// }, 0l, 17l);
	// }
	//
	// /**
	// * Do all calculations, handle input, etc.
	// */
	// private void logic() {
	//
	// displayString = null;
	//
	// if (newLevel != null) {
	// level.onDeath(true);
	// level = newLevel;
	// setupLevel();
	// newLevel = null;
	// }
	//
	// level.onUpdate();
	//
	// if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_I)) {
	// zoom.x += .1;
	// zoom.y += .1;
	// }
	// if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_O)) {
	// zoom.x -= .1;
	// zoom.y -= .1;
	// if (zoom.x < 1)
	// zoom.set(1, 1);
	// }
	// if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_L)) {
	// setFullScreen(!isInFullScreen);
	// }
	// ScriptUtils.setDebugMode(ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_K)
	// ? !ScriptUtils.isDebugMode() : ScriptUtils.isDebugMode());
	//
	// if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
	// frame.setCursor(null); // Restore cursor for menu
	// System.exit(0);
	// }
	//
	// ScriptUtils.getKeyHolder().freeQueuedKeys();
	// }
	//
	// private void setupLevel() {
	// /*
	// * This custom script attaching for player, wolf and block will soon by
	// * nullified by a the Rhino module.
	// */
	// level.getEntityWithId("player").setScript(new PlayerScript());
	//
	// Entity temp = level.getEntityWithId("wolf");
	// if (temp != null)
	// temp.setScript(new WolfScript());
	//
	// temp = level.getEntityWithId("pushblock1");
	// if (temp != null) {
	// temp.setScript(new EntityScript() {
	// Entity player;
	//
	// public void onSpawn(Entity self) {
	// player = level.getEntityWithId("player");
	// }
	//
	// public void onUpdate(Entity self) {
	// ScriptUtils.moveOut(self, true, player);
	// ScriptUtils.moveOut(self, true, level.getSolids());
	// ScriptUtils.moveOut(player, true, self);
	// }
	//
	// public void onDeath(Entity self, boolean isRoomExit) {
	// }
	// });
	// }
	//
	// level.onSpawn();
	//
	// // Set up world portals.
	// for (Entity e : level.getEntitiesWithType("worldPortal")) {
	// e.setScript(new WorldPortalScript());
	// e.onSpawn();
	// }
	// }
	//
	// private void render(final Graphics rootCanvas) {
	// if (level == null) {
	// rootCanvas.clearRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
	// rootCanvas.setColor(Color.BLACK);
	// rootCanvas.drawString("Loading...", (int) VIEWPORT.x / 2,
	// (int) VIEWPORT.y / 2);
	// return;
	// }
	//
	// Vector2 playerPos = level.getEntityWithId("player").getPos();
	// Vector2 offset = VIEWPORT.scale(.5f).sub(playerPos);
	//
	// rootCanvas.setColor(Color.BLACK);
	// rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
	// for (int i = level.getLayers().size() - 1; i >= 0; i--)
	// for (Entity e : level.getLayers().get(i)) {
	// if (e.isVisible() && e.isInView())
	// e.draw(rootCanvas, offset);
	// if (!"BACKGROUND".equals(e.getType()) && ScriptUtils.isDebugMode()) {
	// rootCanvas.setColor(Color.RED);
	// rootCanvas.drawRect((int) (e.getPos().x + offset.x),
	// (int) (e.getPos().y + offset.y), (int) e.getDim().x,
	// (int) e.getDim().y);
	// }
	//
	// }
	//
	// rootCanvas.setColor(Color.white);
	// String infoString = "[WASD] Move" + "  |  [K] Debug Mode: " +
	// ScriptUtils.isDebugMode()
	// + "  |  [L] Full Screen: " + isInFullScreen + "  |  [Esc] Quit";
	//
	// rootCanvas.drawString(infoString, 5, (int) VIEWPORT.y - 5);
	//
	// if (displayString != null) {
	// rootCanvas.drawString(displayString, (int) VIEWPORT.x / 2,
	// (int) VIEWPORT.y / 2 + 64);
	// }
	// }
	//
	// /**
	// * Display mode of user's monitor. Uses 32-bit color depth.
	// */
	// private DisplayMode displayMode = new DisplayMode(resolutionWidth,
	// resolutionHeight, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
	// private boolean isInFullScreen;
	//
	// /**
	// * Sets full screen or reverts screen to normal. In the future, this
	// method
	// * will be able to restore to windowed mode and not simply hide it.
	// *
	// * To my knowledge, full screen only supports one monitor, for I have no
	// * other monitors to test with.
	// *
	// * @param isFullScreen
	// * Enable (true) or disable (false) full screen mode
	// */
	// public void setFullScreen(boolean isFullScreen) {
	// if (isFullScreen) {
	// frame.dispose(); // Without disposing first, the frame remains
	// // displayed.
	// frame.setUndecorated(true);
	// frame.setResizable(false); // Safety check
	// gd.setFullScreenWindow(frame); // This must be placed after
	// // setUndecorated.
	// isInFullScreen = true;
	//
	// if (displayMode != null && gd.isDisplayChangeSupported()) {
	// try {
	// gd.setDisplayMode(displayMode);
	// } catch (Exception e) {
	// System.out.println("Error: Could not set display mode!");
	// }
	// }
	// } else {
	// Window window = gd.getFullScreenWindow();
	//
	// if (window != null) {
	// window.dispose(); // Dispose resources when closed.
	// }
	// frame.setUndecorated(false);
	// frame.setResizable(false);
	// frame.setPreferredSize(new Dimension(800, 600));
	// frame.pack();
	// frame.setVisible(true);
	// gd.setFullScreenWindow(null); // Actual revert of window
	// isInFullScreen = false;
	// }
	// }
	//
	// /**
	// * On next update, switch to a new level.
	// *
	// * @param newLevel
	// * the level to switch to.
	// */
	// public static void warpToLevel(final Level newLevel) {
	// SampleGame.newLevel = newLevel;
	// }
	//
	// /**
	// * Set a string to be displayed in a prompt on screen for 1 frame.
	// *
	// * @param s
	// * the string to set.
	// */
	public static void setDisplayPrompt(final String s) {
		displayString = s;
	}
}
