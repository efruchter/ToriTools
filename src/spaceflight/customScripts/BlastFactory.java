package spaceflight.customScripts;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.Sprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class BlastFactory {
	public static Entity getShipBlast() {
		final Entity blast = new Entity();
		blast.dim = new Vector2(30, 30);
		blast.variables.setVar("damage", "20");
		blast.type = "goodBlast";
		blast.solid = true;
		blast.sprite = new Sprite() {
			@Override
			public void draw(final Graphics g, final Entity self,
					final Vector2 pos, final Vector2 dim) {
				g.setColor(Color.CYAN);
				g.drawOval((int) pos.x, (int) pos.y, (int) dim.x, (int) dim.y);
			}
		};
		blast.script = new EntityScript() {

			int speed = 20;

			@Override
			public void onSpawn(Level level, Entity self) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(Level level, Entity self) {
				self.pos.y -= speed;
				if (self.pos.y < -100)
					level.killEntity(self);
			}

			@Override
			public void onDeath(Level level, Entity self, boolean isRoomExit) {
				// TODO Auto-generated method stub

			}
		};
		return blast;
	}

	public static Entity getExplosion(final int spread) {
		final Entity blast = new Entity();
		blast.dim = new Vector2(0, 0);
		blast.solid = false;

		blast.sprite = new Sprite() {
			int time = 0;

			@Override
			public void draw(final Graphics g, final Entity self,
					final Vector2 pos, final Vector2 dim) {
				g.setColor(Color.red);
				g.drawOval((int) pos.x, (int) pos.y, ++time, time);
			}
		};
		blast.script = new EntityScript() {

			final int MAX_TIME = (int) (Math.random() * spread);
			int time = 0;

			@Override
			public void onSpawn(Level level, Entity self) {
				self.pos.x += -spread + Math.random() * spread * 2;
				self.pos.y += -spread + Math.random() * spread * 2;

			}

			@Override
			public void onUpdate(Level level, Entity self) {
				if (time > MAX_TIME)
					level.killEntity(self);
				time++;
			}

			@Override
			public void onDeath(Level level, Entity self, boolean isRoomExit) {
				// TODO Auto-generated method stub
			}
		};
		return blast;
	}
}
