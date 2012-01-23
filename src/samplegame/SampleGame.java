/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 */
package samplegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

	public SampleGame() {
		super(new Vector2(800, 600), 60);
	}

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
			((Graphics2D) rootCanvas).setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
		level.getEntityWithId("player").addScript(new PlayerScript());

		Entity temp = level.getEntityWithId("wolf");
		if (temp != null)
			temp.addScript(new WolfScript());

		temp = level.getEntityWithId("pushblock1");
		if (temp != null) {
			temp.addScript(new EntityScript() {
				Entity player;

				public void onSpawn(Entity self) {
					player = level.getEntityWithId("player");
				}

				public void onUpdate(Entity self, float time) {
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
			e.addScript(new WorldPortalScript());
		}

	}

	@Override
	protected File getStartingLevel() {
		return new File("levels/MoreLevel.xml");
	}

	/**
	 * Set a string to be displayed in a prompt on screen for 1 frame.
	 * 
	 * @param s
	 *            the string to set.
	 */
	public static void setDisplayPrompt(final String s) {
		displayString = s;
	}
}
