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
		new AudioProject();
	}

	public AudioProject() {
		super(new Vector2(640, 480), 60);
	}

	@Override
	protected boolean render(Graphics rootCanvas) {
		try {
			rootCanvas.setColor(Color.WHITE);
			rootCanvas.fillRect(-1, -1, (int) VIEWPORT.x + 2, (int) VIEWPORT.y + 2);
			for (int i = ScriptUtils.getCurrentLevel().getLayers().size() - 1; i >= 0; i--)
				for (Entity e : ScriptUtils.getCurrentLevel().getLayers().get(i))
					if (e.isVisible())
						e.draw(rootCanvas, Vector2.ZERO);
		} catch (final Exception uhoh) {
			return false;
		}
		return true;
	}

	@Override
	protected void initialize() {

	}
	
	int enemyTimer = 0;

	@Override
	protected void globalLogic() {
		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
		
		if(enemyTimer-- <= 0) {
			enemyTimer = 100;
			ScriptUtils.getCurrentLevel().spawnEntity(new BadShip(new Vector2(VIEWPORT.x, (float) Math.random() * VIEWPORT.y)));
		}
	}

	@Override
	protected void setupCurrentLevel(Level levelBeingLoaded) {
		levelBeingLoaded.spawnEntity(new PlayerShip());
		//levelBeingLoaded.spawnEntity(new ScrollingBackground());
		
		addLevelBounds(levelBeingLoaded);
	}
	
	private void addLevelBounds(final Level levelBeingLoaded) {
		Entity l, r, u , d;

		l = new Entity();
		l.setSolid(true);
		l.setPos(new Vector2(-20, 0));
		l.setDim(new Vector2(20, VIEWPORT.y));
		
		r = new Entity();
		r.setSolid(true);
		r.setPos(new Vector2(VIEWPORT.x, 0));
		r.setDim(new Vector2(VIEWPORT.x, VIEWPORT.y));
		
		u = new Entity();
		u.setSolid(true);
		u.setPos(new Vector2(0, -20));
		u.setDim(new Vector2(VIEWPORT.x, 20));
		
		d = new Entity();
		d.setSolid(true);
		d.setPos(new Vector2(0, VIEWPORT.y));
		d.setDim(new Vector2(VIEWPORT.x, 20));
		
		levelBeingLoaded.spawnEntity(l);
		levelBeingLoaded.spawnEntity(r);
		levelBeingLoaded.spawnEntity(u);
		levelBeingLoaded.spawnEntity(d);
	}

	@Override
	protected Level getStartingLevel() {
		Level level = new Level();
		level.setDim(VIEWPORT);
		return level;
	}

}
