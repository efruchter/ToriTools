package audioProject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import maryb.player.Player;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator.HermiteKeyFrame;
import toritools.render.ColorUtils;
import toritools.scripting.ScriptUtils;
import audioProject.controller.WaveController;
import audioProject.entities.BadShipFactory;
import audioProject.entities.PlayerShip;
import audioProject.entities.ScrollingBackground;

/**
 * Template for our possible audio project.
 * 
 * @author toriscope
 * 
 */
public class AudioProject extends Binary {
	
	public static Player soundPlayer = new Player();
	public static WaveController controller;
   

	public static void main(String[] args) {
		new AudioProject();
	}

	public AudioProject() {
		super(new Vector2(640, 480), 60, "The Search for F.E.E.L.");
	}

	@Override
	protected boolean render(Graphics rootCanvas, Level level) {
		try {
			((Graphics2D) rootCanvas).setStroke(new BasicStroke(4));
			rootCanvas.setColor(bgColor);
			rootCanvas.fillRect(-1, -1, (int) VIEWPORT.x + 2, (int) VIEWPORT.y + 2);
			for (int i = level.getLayers().size() - 1; i >= 0; i--)
				for (Entity e : level.getLayers().get(i)) {
					if (e.isVisible())
						e.draw(rootCanvas, Vector2.ZERO);
				}
			rootCanvas.setColor(Color.BLACK);
			rootCanvas.drawString("Time: " + soundPlayer.getCurrentPosition(), 10, 20);
			rootCanvas.drawString("Feel: " + controller.getFeel(), 10 , 40);
			
		} catch (final Exception uhoh) {
			return false;
		}
		return true;
	}

	@Override
	protected void initialize() {
		soundPlayer.setCurrentVolume(1f);
		soundPlayer.setSourceLocation("unicorn.mp3");	
		controller = new WaveController(432000);
		soundPlayer.play();
		moments = entities = 0;
	}
	
	long moments, entities;
	
	Color bgColor = Color.black;

	@Override
	protected void globalLogic(Level level) {
		
		controller.setTime44100((long) (soundPlayer.getCurrentPosition()* 0.001));
		
		ScrollingBackground bg = (ScrollingBackground) level.getEntityWithId("bg");
		PlayerShip player = (PlayerShip) level.getEntityWithId("player");
		
		bg.setFocus(player.getPos(), .5f);
		bg.setSpeed(5 * controller.getFeel());
		
		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
		
		if (Math.random() < .015 * Math.abs(controller.getFeel())) {
			HermiteKeyFrame s1 = new HermiteKeyFrame(VIEWPORT.scale(1, (float) Math.random()), Vector2.ONE.scale(10 * (.5f + (float) Math.random()), 10 * (.5f + (float) Math.random())), 0);
			HermiteKeyFrame s2 = new HermiteKeyFrame(new Vector2(VIEWPORT.x * (float) Math.random(), VIEWPORT.y * (float) Math.random()),Vector2.ONE.scale(10 * (.5f + (float) Math.random()), 10 * (.5f + (float) Math.random())), (float) Math.random() * 1000 + 4000);
			HermiteKeyFrame s3 = s1.clone();
			s3.time = s2.time * 2;
			HermiteKeyFrameInterpolator path = new HermiteKeyFrameInterpolator(s1, s2, s3);
			level.spawnEntity(BadShipFactory.makePathedEnemy(path, .5f));
		}
		
		bgColor =  ColorUtils.blend(new Color(245,137,104), Color.LIGHT_GRAY, .4f * Math.abs(AudioProject.controller.getFeel()));
	}

	@Override
	protected void setupCurrentLevel(Level levelBeingLoaded) {
		levelBeingLoaded.spawnEntity(new PlayerShip());
		levelBeingLoaded.spawnEntity(new ScrollingBackground(VIEWPORT, 1, 30, 2.3f, .614f, 100, 100));
		
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
