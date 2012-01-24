package audioProject.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import toritools.controls.KeyHolder;
import toritools.entity.Entity;
import toritools.entity.sprite.AbstractSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class PlayerShip extends Entity {

	public PlayerShip() {
		pos = new Vector2(50, 50);
		dim = new Vector2(30, 30);
		addScript(controlScript);
		setSprite(sprite);
	}

	EntityScript controlScript = new EntityScript() {

		final char UP = KeyEvent.VK_W, DOWN = KeyEvent.VK_S,
				RIGHT = KeyEvent.VK_D, LEFT = KeyEvent.VK_A, 
				SHOOT = KeyEvent.VK_SPACE;

		float speed = .2f;

		KeyHolder keys;

		@Override
		public void onSpawn(Entity self) {
			keys = ScriptUtils.getKeyHolder();
		}

		@Override
		public void onUpdate(Entity self, float time) {
			float speed = this.speed * time;

			if (keys.isPressed(UP)) {
				self.setPos(self.getPos().add(new Vector2(0, -speed)));
			}

			if (keys.isPressed(DOWN)) {
				self.setPos(self.getPos().add(new Vector2(0, speed)));
			}

			if (keys.isPressed(LEFT)) {
				self.setPos(self.getPos().add(new Vector2(-speed, 0)));
			}

			if (keys.isPressed(RIGHT)) {
				self.setPos(self.getPos().add(new Vector2(speed, 0)));
			}
			
			if (keys.isPressed(SHOOT)) {
				ScriptUtils.getCurrentLevel().spawnEntity(new GoodBullet(self.getPos().add(self.getDim().scale(.5f))));
			}
		}

		@Override
		public void onDeath(Entity self, boolean isRoomExit) {
			// TODO Auto-generated method stub

		}

	};

	AbstractSprite sprite = new AbstractSprite() {

		@Override
		public void nextFrame() {
			// TODO Auto-generated method stub

		}

		@Override
		public void nextFrameAbsolute() {
			// TODO Auto-generated method stub

		}

		@Override
		public void setFrame(int frame) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setCycle(int cycle) {
			// TODO Auto-generated method stub

		}

		@Override
		public void set(int frame, int cycle) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setTimeStretch(int timeStretch) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setsizeOffset(int sizeOffset) {
			// TODO Auto-generated method stub

		}

		@Override
		public void draw(Graphics g, Entity self, Vector2 position,
				Vector2 dimension) {
			g.setColor(Color.black);
			g.drawOval((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);

		}

		@Override
		public Image getImage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Dimension getTileDimension() {
			// TODO Auto-generated method stub
			return null;
		}

	};
}
