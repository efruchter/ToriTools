package samplegame.entrypoint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import samplegame.audio.MP3;
import samplegame.controls.KeyHolder;
import samplegame.load.Importer;
import samplegame.scripting.EntityScript;
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

	private static boolean lighting = false;

	private static final Vector2 VIEWPORT = new Vector2(800, 600);

	private static MP3 bg_music;

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

		/**
		 * Create a blank level with size 1000x1000.
		 */
		level = Importer.importLevel(new File("levels/MoreLevel.xml"));

		level.idMap.get("player").script = new EntityScript() {
			public void onSpawn(Level level, Entity self) {
				System.out.println("The kid is spawned!");
			}

			public void onUpdate(Level level, Entity self) {
				float x = self.pos.x, y = self.pos.y;

				int speed = 3;
				boolean walked = false;
				if (keys.isPressed(KeyEvent.VK_A)) {
					walked = true;
					self.pos.x -= speed;
					self.sprite.setCylcle(1);
				}
				if (keys.isPressed(KeyEvent.VK_D)) {
					walked = true;
					self.pos.x += speed;
					self.sprite.setCylcle(2);
				}
				// Detect and correct for collisions
				self.moveOutX(x, level.solids.toArray(new Entity[0]));

				if (keys.isPressed(KeyEvent.VK_W)) {
					walked = true;
					self.pos.y -= speed;
					self.sprite.setCylcle(3);
				}

				if (keys.isPressed(KeyEvent.VK_S)) {
					walked = true;
					self.pos.y += speed;
					self.sprite.setCylcle(0);
				}

				// Lighting control
				if (keys.isPressed(KeyEvent.VK_K))
					lighting = false;
				if (keys.isPressed(KeyEvent.VK_L))
					lighting = true;

				// Detect and correct for y collisions
				self.moveOutY(y, level.solids.toArray(new Entity[0]));

				if (walked)
					self.sprite.nextFrame();
			}

			public void onDeath(Level level, Entity self, boolean isRoomExit) {
			}

		};
		level.idMap.get("pushblock1").script = new EntityScript() {
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
		level.idMap.get("wolf").script = new EntityScript() {
			private Random rand = new Random();

			public void onSpawn(Level level, Entity self) {
			}

			public void onUpdate(Level level, Entity self) {
				self.sprite.nextFrame();
				if (Math.random() > .98d) {
					self.sprite.setCylcle(rand.nextInt(4));
				}
			}

			public void onDeath(Level level, Entity self, boolean isRoomExit) {
			}
		};

		bufferImage = new BufferedImage((int) VIEWPORT.x, (int) VIEWPORT.y,
				BufferedImage.TYPE_INT_RGB);
		bufferGraphics = bufferImage.getGraphics();

		for (Entity e : level.nonSolids)
			e.onSpawn(level);
		for (Entity e : level.solids)
			e.onSpawn(level);
		bg_music = new MP3("resources/creep.mp3");
		bg_music.play();
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
		for (Entity e : level.solids)
			e.onUpdate(level);
		for (Entity e : level.nonSolids)
			e.onUpdate(level);
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
		if (keys.isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
	}

	private static ImageFilter lanternFilter = new RGBImageFilter() {
		public int markerRGB = Color.WHITE.getRGB() | 0xFF000000;

		public final int filterRGB(int x, int y, int rgb) {
			if ((rgb | 0xFF000000) == markerRGB) {
				return 0x00FFFFFF & rgb;
			} else {
				return 0xFE000000 & rgb;
			}
		}
	};

	final static Vector2 zoom = new Vector2(1, 1);

	private static Image bufferImage;
	private static Graphics bufferGraphics;

	private static void render(final Graphics rootCanvas) {
		try {
			Vector2 playerPos = level.idMap.get("player").getMid();
			Vector2 wolfPos = level.idMap.get("wolf").getMid();
			Vector2 offset = VIEWPORT.scale(.5f).sub(playerPos);

			bufferGraphics.setColor(Color.BLACK);
			bufferGraphics.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
			for (int i = level.layers.size() - 1; i >= 0; i--)
				for (Entity e : level.layers.get(i))
					if (e.visible)
						e.draw(bufferGraphics, offset);
			Image i = new BufferedImage((int) VIEWPORT.x, (int) VIEWPORT.y,
					BufferedImage.TYPE_INT_RGB);
			if (lighting) {
				Graphics lightLayer = i.getGraphics();
				lightLayer.setColor(Color.BLACK);
				lightLayer.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
				drawLanternAround(140, playerPos.add(offset), lightLayer);
				drawLanternAround(100, wolfPos.add(offset), lightLayer);
				i = Toolkit.getDefaultToolkit().createImage(
						new FilteredImageSource(i.getSource(), lanternFilter));
				bufferGraphics.drawImage(i, 0, 0, null);
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
			String infoString = "[WASD] Move |" + " [K/L] Hard Lighting:"
					+ (lighting ? "On" : "Off") + " |" + " [I/O] Zoom: "
					+ zoom.x;

			rootCanvas.drawString(infoString, 5, (int) VIEWPORT.y - 5);
		} catch (Exception e) {

		}
	}

	public static void drawLanternAround(final int radius, final Vector2 pos,
			final Graphics g) {
		g.setColor(Color.WHITE);
		g.fillOval((int) pos.x - radius, (int) pos.y - radius, 2 * radius,
				2 * radius);

	}
}
