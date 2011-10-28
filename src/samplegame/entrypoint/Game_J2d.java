package samplegame.entrypoint;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import samplegame.controls.KeyHolder;
import samplegame.entity.Entity;
import samplegame.entity.Level;
import samplegame.load.Importer;
import samplegame.math.Vector2;
import samplegame.scripting.EntityScript;
import toritools.map.VariableCase;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 * 
 */
public class Game_J2d {

	/** Game title */
	public static final String GAME_TITLE = "USE WASD!";

	public static final Vector2 BOUNDS = new Vector2(800, 600);

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
			setPreferredSize(new Dimension((int) BOUNDS.x, (int) BOUNDS.y));
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
				if (keys.isPressed(KeyEvent.VK_A))
					self.pos.x -= speed;
				if (keys.isPressed(KeyEvent.VK_D))
					self.pos.x += speed;
				// Detect and correct for collisions
				self.moveOutX(x, level.solids.toArray(new Entity[0]));

				if (keys.isPressed(KeyEvent.VK_W))
					self.pos.y -= speed;

				if (keys.isPressed(KeyEvent.VK_S))
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
		for (Entity e : level.solids)
			e.onSpawn(level);
	}

	private static Timer timer;

	/**
	 * Runs the game (the "main loop")
	 */
	private static void run() {
		timer = new Timer(16, new ActionListener() {
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

	}

	private static void render(final Graphics g) {
		if (level == null)
			return;
		for (Entity e : level.nonSolids) {
			e.draw(g);
		}
		for (Entity e : level.solids) {
			e.draw(g);
		}
	}
}
