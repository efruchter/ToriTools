package burd;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.io.FontLoader;
import toritools.io.Importer;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;
import toritools.timing.StopWatch;
import burd.customscripts.BreadScript;
import burd.customscripts.BurdScript;
import burd.customscripts.PufferfishScript;

public class BurdGame extends Binary {

	public static void main(String[] args) {
		new BurdGame();
	}

	private static StopWatch stopWatch;
	private Image SKY_BACKGROUND;
	private Font uIFont;
	private MidpointChain camera;
	private int levelNumber = 1;

	@Override
	protected void initialize() {
		SKY_BACKGROUND = Toolkit.getDefaultToolkit().getImage("burd/backgrounds/sky.jpg");
		FontLoader.loadFonts(new File("burd/fonts"));
		uIFont = new Font("Earth's Mightiest", Font.TRUETYPE_FONT, 40);
		camera = new MidpointChain(new Vector2(), new Vector2(), 10);
		stopWatch = new StopWatch();
	}

	@Override
	protected File getStartingLevel() {
		return new File("burd/level" + ++levelNumber + ".xml");
	}

	@Override
	public void logic() {

		if (ScriptUtils.getCurrentLevel().getEntitiesWithType("bread").isEmpty()) {
			nextLevel();
		}

		ScriptUtils.getCurrentLevel().onUpdate();

		if (ScriptUtils.isDebugMode()) {

			if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_P)) {
				nextLevel();
			}

			if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_L)) {
				try {
					Entity e = Importer.importEntity(new File("burd/objects/bread.entity"), null);
					e.setPos(ScriptUtils.getCurrentLevel().getEntityWithId("player").getPos().clone());
					e.setScript(new BreadScript());
					ScriptUtils.getCurrentLevel().spawnEntity(e);
				} catch (final Exception w) {
					w.printStackTrace();
				}
			}
		}

		ScriptUtils.setDebugMode(ScriptUtils.getKeyHolder()
				.isPressedThenRelease(KeyEvent.VK_K) ? !ScriptUtils
				.isDebugMode() : ScriptUtils.isDebugMode());

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}

		ScriptUtils.getKeyHolder().freeQueuedKeys();

		camera.setA(ScriptUtils.getCurrentLevel().getEntityWithId("player").getPos().clone());
		camera.smoothTowardA();

		ScriptUtils.getCurrentLevel().setViewportData(camera.getB().sub(VIEWPORT.scale(.5f)), VIEWPORT);
	}

	public boolean render(final Graphics rootCanvas) {
		try {
			Level level = ScriptUtils.getCurrentLevel();

			rootCanvas.setFont(uIFont);

			Vector2 offset = VIEWPORT.scale(.5f).sub(camera.getB());

			rootCanvas.drawImage(SKY_BACKGROUND, 0, 0, (int) VIEWPORT.x,
					(int) VIEWPORT.y, null);
			rootCanvas.drawImage(level.getBakedBackground(), (int) offset.x,
					(int) offset.y, (int) level.getDim().x,
					(int) level.getDim().y, null);
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
			level.getEntityWithId("player").draw(rootCanvas, offset);

			rootCanvas.setColor(Color.BLACK);
			String infoString = "[Esc] Quit  [K] Debug Mode: " + ScriptUtils.isDebugMode();
			if (ScriptUtils.isDebugMode())
				infoString = infoString + "  [P] Next Level  [L] SPAWN EGGS";
			rootCanvas.drawString(infoString, 5, (int) VIEWPORT.y - 5);

			/*
			 * HUD
			 */
			String title = level.getVariableCase().getVar("title");
			title = (title == null) ? "" : title;
			rootCanvas.drawString(
					title + "        Time: " + stopWatch.getElapsedTimeSecs(),
					(int) 20, 40);

			int xIndex = 0;
			for (Entity bread : level.getEntitiesWithType("bread")) {
				bread.getSprite().draw(rootCanvas, bread, new Vector2(20 + xIndex++ * bread.getDim().x * 1.5f, 50), bread.getDim());
			}
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void setupCurrentLevel(final Level level) {
		try {
			level.getEntityWithId("player").setScript(new BurdScript());
			level.getEntityWithId("player").setVisible(false);
			camera = new MidpointChain(level.getEntityWithId("player").getPos(), level.getEntityWithId("player").getPos(), 10);
		} catch (final NullPointerException e) {
			JOptionPane.showMessageDialog(null, "This level is missing a Burd (player.entity)!");
			System.exit(0);
		}

		/*
		 * Special spawns, will be fixed.
		 */

		for (Entity e : level.getEntitiesWithType("bread")) {
			e.setScript(new BreadScript());
		}

		for (Entity e : level.getEntitiesWithType("puffer")) {
			e.setScript(new PufferfishScript());
		}

		ScriptUtils.getCurrentLevel().bakeBackground();

		ScriptUtils.getCurrentLevel().onSpawn();

		stopWatch.start();
	}
	
	public void nextLevel() {
		try {
			File f = new File("burd/level" + ++levelNumber + ".xml");
			if(!f.canRead())
				throw new FileNotFoundException();
			ScriptUtils.queueLevelSwitch(Importer.importLevel(f));
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "You beat all the levels!");
			System.exit(0);
		}
	}
}
