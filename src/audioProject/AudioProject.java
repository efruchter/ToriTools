package audioProject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;
import audioProject.entities.BadShip;
import audioProject.entities.PlayerShip;

/**
 * Template for our possible audio project.
 * 
 * @author toriscope
 * 
 */
public class AudioProject extends Binary {

	public static void main(String[] args) {
		new AudioProject(new Vector2(640, 480), 60);
	}

	public AudioProject(Vector2 VIEWPORT_SIZE, int frameRate) {
		super(VIEWPORT_SIZE, frameRate);
	}

	@Override
	protected boolean render(Graphics rootCanvas) {
		rootCanvas.setColor(Color.red);
		rootCanvas.drawRect(-1, -1, (int) VIEWPORT.x + 2, (int) VIEWPORT.y + 2);
		try {
			for (int i = ScriptUtils.getCurrentLevel().getLayers().size() - 1; i >= 0; i--)
				for (Entity e : ScriptUtils.getCurrentLevel().getLayers().get(i))
					if (e.isVisible())
						e.draw(rootCanvas, Vector2.ZERO);
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
		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}

		if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_E)) {
			ScriptUtils.getCurrentLevel().spawnEntity(
					new BadShip(new Vector2(VIEWPORT.x, (float) Math.random()
							* VIEWPORT.y)));
		}

		// System.err.println(ScriptUtils.getCurrentLevel().getAll().size());
	}

	@Override
	protected void setupCurrentLevel(Level levelBeingLoaded) {
		levelBeingLoaded.spawnEntity(new PlayerShip());
	}

	@Override
	protected Level getStartingLevel() {
		Level level = new Level();
		level.setDim(VIEWPORT);
		return level;
	}

}
