package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator.HermiteKeyFrame;
import toritools.render.ColorUtils;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import audioProject.AudioProject;

import static audioProject.AudioProject.getFloat;

public class BadShipFactory {
	
	public static Entity makeDefaultEnemy(final Vector2 screen) {
		List<HermiteKeyFrame> keyList = new ArrayList<HermiteKeyFrame>();
		
		float time = 10000 * AudioProject.controller.getFeel() * (.2f + getFloat() * .8f);
		
		int selection = (int) (getFloat() * 4);

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
		} else if (selection == 2 || selection == 3) {
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
				entity.setDim(new Vector2(health * .50f));
			}

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				allTime += time * Math.abs(AudioProject.controller.getFeel());
				if (allTime > path.getEndTime()) {
					level.despawnEntity(self);
				} else if (self.getVariableCase().getFloat("health") <= 0) {
					level.despawnEntity(self);
					level.spawnEntity(new Explosion(self.getPos(), Color.RED, self.getDim().x, 30));
				} else {
					self.setPos(path.getPositionDeltaAtTime(allTime));
					if (AudioProject.controller.isBeat()) {
						//Vector2 middle = self.getPos().add(self.getDim().scale(.5f));
						float scalar = .1f + Math.abs(AudioProject.controller.getFeel()) * .1f;
						float radius = self.getDim().x / 5 + 5;
						if (bulletChase) {
							Vector2 middle = self.getPos().add(self.getDim().scale(.5f));
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
		
		final Color color = ColorUtils.blend(new Color(255, 0, 128), Color.RED, AudioProject.controller.getFeel());
		
		entity.setSprite(new AbstractSpriteAdapter() {
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(color);
				int extra = 0;//AudioProject.controller.isBeat() ? 10 : 0;
				g.fillOval((int) position.x - extra / 2, (int) position.y - extra / 2, (int) dimension.x + extra, (int) dimension.y + extra);
			}
		});

		return entity;
	}

}