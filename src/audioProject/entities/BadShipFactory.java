package audioProject.entities;

import static audioProject.AudioProject.getFloat;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import toritools.additionaltypes.HealthBar;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator.HermiteKeyFrame;
import toritools.scripting.ScriptUtils;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import audioProject.AudioProject;

public class BadShipFactory {
	
	public static Entity makeDefaultEnemy(final Vector2 screen) {
		List<HermiteKeyFrame> keyList = new ArrayList<HermiteKeyFrame>();
		
		float time = 100000 * AudioProject.controller.getFeel() * (.2f + getFloat() * .8f);
		
		int selection = (int) (getFloat() * 6);

		keyList.add(new HermiteKeyFrame(new Vector2(screen.x, 100 + getFloat() * (screen.y - 200)), screen.scale(0, .02f * screen.y * (-.5f + getFloat())), 0));
		
		boolean bulletChase = true;
		
		if(selection == 0) {
			//Float to top 
			Vector2 mag = new Vector2(0, -.2f * getFloat() * screen.y);
			keyList.add(new HermiteKeyFrame(new Vector2(screen.x * getFloat() - 150, -150), mag, time));
		} else if (selection == 1) {
			// Float to bottom
			Vector2 mag = new Vector2(0, .2f * getFloat() * screen.y);
			keyList.add(new HermiteKeyFrame(new Vector2(screen.x * getFloat() - 150,  screen.y), mag, time));
		} else if (selection > 1) {
			bulletChase = false;
			keyList.add(new HermiteKeyFrame(new Vector2(- 150, 100 + getFloat() * (screen.y - 200)), time));
		}
		
		return makePathedEnemy(new HermiteKeyFrameInterpolator(keyList.toArray(new HermiteKeyFrame[0])), bulletChase);
	}

	public static Entity makePathedEnemy(final HermiteKeyFrameInterpolator path, final boolean bulletChase) {

		final Entity entity = new Entity();
		entity.setType("enemy");
		entity.setLayer(1);

		entity.addScript(new EntityScriptAdapter() {

			float allTime;
			final float health = 10 + 200 * AudioProject.controller.getFeel();
			
			@Override
			public void onSpawn(Entity self, Level level) {
				allTime = 0;
				self.setPos(path.getPositionDeltaAtTime(0));
				self.getVariableCase().setVar("health", "" + health);
				entity.setDim(new Vector2(health * .30f));
			}

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				allTime += time * Math.abs(AudioProject.controller.getFeel());
				if (allTime > path.getEndTime()) {
					level.despawnEntity(self);
				} else if (self.getVariableCase().getFloat("health") <= 0) {
					level.despawnEntity(self);
					level.spawnEntity(new Explosion(self.getPos(), AudioProject.enemyColor, self.getDim().x, 30));
					if (AudioProject.getFloat() < .10)
						level.spawnEntity(new HealthPickup(self.getPos()));
				} else {
					self.setPos(path.getPositionDeltaAtTime(allTime));
					if (AudioProject.controller.isBeat()) {
						//Vector2 middle = self.getPos().add(self.getDim().scale(.5f));
						float scalar = .1f + Math.abs(AudioProject.controller.getFeel()) * .01f;
						float radius = self.getDim().x / 7 + 5;
						if (bulletChase) {
							Vector2 middle = self.getPos().add(self.getDim().scale(0, .5f));
							level.spawnEntity(new BadBullet(middle, Vector2.toward(middle, level.getEntityWithId("player").getPos()).scale(scalar), radius));
						} else {
							Vector2 middle = self.getPos().add(self.getDim().scale(0, .5f));
							level.spawnEntity(new BadBullet(middle, new Vector2(-1, 0).scale(scalar), radius));
							level.spawnEntity(new BadBullet(middle, new Vector2(-1, .7f).scale(scalar), radius));
							level.spawnEntity(new BadBullet(middle, new Vector2(-1, -.7f).scale(scalar), radius));
						}
					}
				}			
			}
		});
		
		entity.setSprite(new AbstractSpriteAdapter() {
			
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(AudioProject.enemyColor);
				g.fillOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}
		});

		return entity;
	}
	
	public static Entity makeBoss() {

		final Entity entity = new Entity();
		entity.setType("enemy");
		entity.setLayer(1);
		
		final HealthBar bar  = new HealthBar(10 + 10000 * AudioProject.controller.getAverageFeel(), Color.RED, Color.RED);

		
		
		entity.addScript(new EntityScriptAdapter() {
			
			MidpointChain chain;
			
			boolean healthGone = false;
			
			@Override
			public void onSpawn(Entity self, Level level) {
				self.setPos(level.getDim().scale(.5f));
				self.getVariableCase().setVar("health", "" + bar.getMaxHealth());
				entity.setDim(new Vector2(100));
				chain = new MidpointChain(level.getDim().scale(1.01f, .5f), 100);
				chain.setA(level.getDim().scale(.5f));
			}

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				
				if (AudioProject.getFloat() < .05) {
					float x = 100 + AudioProject.getFloat() * (level.getDim().x - 100);
					float y = 100 + AudioProject.getFloat() * (level.getDim().y - 100 - self.getDim().y);
					chain.setA(new Vector2(x, y));
				}
				
				chain.smoothTowardA();
				self.setPos(chain.getB());
				
				float health;
				if ((health = self.getVariableCase().getFloat("health")) <= 0) {
					level.despawnEntity(self);
					level.spawnEntity(new Explosion(self.getPos(), AudioProject.enemyColor, self.getDim().x, 300));
					level.spawnEntity(new Explosion(self.getPos(), AudioProject.enemyColor, self.getDim().x / 2, 300));
					level.spawnEntity(new Explosion(self.getPos(), AudioProject.enemyColor, self.getDim().x / 3, 300));
					healthGone = true;
				} else {
					if (AudioProject.controller.isBeat()) {
						Vector2 middle = self.getPos().add(self.getDim().scale(.5f));
						float scalar = .1f + Math.abs(AudioProject.controller.getFeel()) * .01f;
						float radius = self.getDim().x / 2 + 5;
						level.spawnEntity(new BadBullet(middle, Vector2.toward(middle, level.getEntityWithId("player").getPos()).scale(scalar), radius));
						level.spawnEntity(new BadBullet(middle, new Vector2(-1, 0).scale(scalar), radius));
						level.spawnEntity(new BadBullet(middle, new Vector2(-1, .7f).scale(scalar), radius));
						level.spawnEntity(new BadBullet(middle, new Vector2(-1, -.7f).scale(scalar), radius));
					}
				}
				bar.setHealth(health);
			}
			
			@Override
			public void onDeath(Entity self, final Level level, boolean isRoomExit) {
				if (healthGone) {
					ScriptUtils.getKeyHolder().clearKeys();
					AudioProject.win = true;
				}
			}
		});
		
		entity.setSprite(new AbstractSpriteAdapter() {
			
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				
				bar.draw(g, new Vector2(10, 90), new Vector2(200, 30));
				
				g.setColor(AudioProject.enemyColor);
				g.fillOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
			}
		});

		return entity;
	}

}