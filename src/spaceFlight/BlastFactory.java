package spaceFlight;

import java.awt.Color;
import java.awt.Graphics;

import samplegame.scripting.EntityScript;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.Sprite;
import toritools.math.Vector2;

public class BlastFactory {
	public static Entity getShipBlast() {
		final Entity blast = new Entity();
		blast.dim = new Vector2(30, 30);
		blast.variables.setVar("damage", "20");
		blast.variables.setVar("title", "goodBlast");
		blast.solid = true;
		blast.sprite = new Sprite() {
			public void draw(Graphics g, final Vector2 pos, final Vector2 dim) {
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
}
