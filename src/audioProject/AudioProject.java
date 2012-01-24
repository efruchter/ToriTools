package audioProject;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;
import audioProject.entities.PlayerShip;

/**
 * Template for our possible audio project.
 * @author toriscope
 *
 */
public class AudioProject extends Binary{
	
	public static void main(String[] args) {
		new AudioProject(new Vector2(640, 480), 60);
	}

	public AudioProject(Vector2 VIEWPORT_SIZE, int frameRate) {
		super(VIEWPORT_SIZE, frameRate);
	}

	@Override
	protected boolean render(Graphics rootCanvas) {
		try {
			for(Entity ent: ScriptUtils.getCurrentLevel().getAll())
				ent.draw(rootCanvas, Vector2.ZERO);
		} catch(final Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void initialize() {
		
	}

	@Override
	protected void globalLogic() {
		if(ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
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
