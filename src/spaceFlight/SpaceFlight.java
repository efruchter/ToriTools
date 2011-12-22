package spaceFlight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import samplegame.load.Importer;
import toritools.controls.KeyHolder;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.Sprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 * 
 */
public class SpaceFlight {

	/** Game title */
	private static String GAME_TITLE = "Space Flight by toriscope";

	public static boolean debug = false;

	private static final Vector2 VIEWPORT = new Vector2(800, 600);

	/**
	 * The current working level.
	 */
	private static Level level;

	/**
	 * Player Data
	 */
	static class PlayerData {
		public static int maxHealth = 100, maxArmor = 100;
		public static int playerHealth = 75, playerArmor = 25;
	}

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
		frame.setResizable(false);
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

		level = Importer.importLevel(new File("spaceFlight/levels/Level1.xml"));

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

	final public static Vector2 zoom = new Vector2(1, 1);

	private static Image bufferImage;
	private static Graphics bufferGraphics;

	private static void render(final Graphics rootCanvas) {
		try {
			Vector2 offset = new Vector2();

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

	private static void setupLevel() {
		level.getEntityWithId("player").script = new EntityScript() {

			int shootTimer = 0;

			@Override
			public void onSpawn(Level level, Entity self) {
				self.sprite.setFrame(2);
			}

			@Override
			public void onUpdate(Level level, Entity self) {
				int speed = 3;
				shootTimer--;

				boolean moved = false;

				if (keys.isPressed(KeyEvent.VK_A)) {
					self.pos.x -= speed;
					self.sprite.setFrame(0);
					moved = true;
				}
				if (keys.isPressed(KeyEvent.VK_D)) {
					self.pos.x += speed;
					self.sprite.setFrame(2);
					moved = true;
				}
				if (keys.isPressed(KeyEvent.VK_W)) {
					self.pos.y -= speed;
					moved = true;
				}
				if (keys.isPressed(KeyEvent.VK_S)) {
					self.pos.y += speed;
					moved = true;
				}

				if (shootTimer <= 0 && keys.isPressed(KeyEvent.VK_SPACE)) {
					Entity e = BlastFactory.getShipBlast();
					e.pos = self.pos.clone();
					level.spawnEntity(e);
					shootTimer = 10;
				}

				if (!moved) {
					self.sprite.setFrame(1);
				}
			}

			@Override
			public void onDeath(Level level, Entity self, boolean isRoomExit) {
				// TODO Auto-generated method stub

			}

		};

		/**
		 * Draw the health bars for the ship.
		 */
		level.spawnEntity(new Entity() {
			{
				sprite = new Sprite() {
					final Rectangle healthBar = new Rectangle(0, 0, 200, 30);
					final Rectangle armorBar = new Rectangle(0, 30, 200, 30);
					public void draw(Graphics g, final Vector2 pos, final Vector2 dim) {
						g.setColor(Color.WHITE);
						g.fillRect(healthBar.x, healthBar.y, healthBar.width, healthBar.height);
						g.fillRect(armorBar.x, armorBar.y, armorBar.width, armorBar.height);
						g.setColor(Color.RED);
						g.fillRect(healthBar.x, healthBar.y, (int) (healthBar.width * ((float) PlayerData.playerHealth / PlayerData.maxHealth)), healthBar.height);
						g.setColor(Color.BLUE);
						g.fillRect(armorBar.x, armorBar.y, (int) (armorBar.width * ((float) PlayerData.playerArmor / PlayerData.maxArmor)), armorBar.height);
					}
				};
			}
		});

		level.onSpawn();
	}
}
