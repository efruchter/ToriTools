package audioProject.entities;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator.HermiteKeyFrame;
import toritools.render.ColorUtils;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import audioProject.AudioProject;

import static java.lang.Math.random;

public class BadShipFactory {
	
	public static Entity makeDefaultEnemy(final Vector2 screen) {
		HermiteKeyFrame k1 = new HermiteKeyFrame(new Vector2(screen.x, (float) random() * screen.y), 0);
		HermiteKeyFrame k2 = new HermiteKeyFrame(new Vector2(-150, (float) random() * screen.y), 16000 * (1 - AudioProject.controller.getFeel()));
		return makePathedEnemy(new HermiteKeyFrameInterpolator(k1, k2));
	}

	public static Entity makePathedEnemy(final HermiteKeyFrameInterpolator path) {

		final Entity entity = new Entity();
		entity.setType("enemy");

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
						Vector2 middle = self.getPos().add(self.getDim().scale(.5f));
						float scalar = .1f + Math.abs(AudioProject.controller.getFeel()) * .1f;
						float radius = self.getDim().x / 5 + 5;
						level.spawnEntity(new BadBullet(middle, new Vector2(-1, 0).scale(scalar), radius));
						level.spawnEntity(new BadBullet(middle, new Vector2(-1, .7f).scale(scalar), radius));
						level.spawnEntity(new BadBullet(middle, new Vector2(-1, -.7f).scale(scalar), radius));
					}
				}			
			}
		});
		
		final Color color = ColorUtils.blend(new Color(255, 0, 128), Color.RED, Math.random());
		
		entity.setSprite(new AbstractSpriteAdapter() {
			@Override
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(color);
				int extra = AudioProject.controller.isBeat() ? 10 : 0;
				g.fillOval((int) position.x - extra / 2, (int) position.y - extra / 2, (int) dimension.x + extra, (int) dimension.y + extra);
			}
		});

		return entity;
	}

}