package audioProject.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import toritools.entity.Entity;
import toritools.entity.sprite.AbstractSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class GoodBullet extends Entity {

	public GoodBullet(final Vector2 position) {
		type = "GoodBullet";

		variables.setVar("damage", 10 + "");

		pos = position;
		dim = new Vector2(10, 10);

		addScript(new EntityScript() {
			
			float speed = .5f;

			@Override
			public void onSpawn(Entity self) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(Entity self, float time) {
				if(!ScriptUtils.isColliding(ScriptUtils.getCurrentLevel(), self)) {
					ScriptUtils.getCurrentLevel().despawnEntity(self);
				}
				self.setPos(self.getPos().add(time * speed, 0));
			}

			@Override
			public void onDeath(Entity self, boolean isRoomExit) {
				// TODO Auto-generated method stub

			}

		});

		sprite = new AbstractSprite() {

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
			public void draw(Graphics g, Entity self, Vector2 position, Vector2 dimension) {
				g.setColor(Color.green);
				g.drawRect((int) position.x, (int) position.y, (int) dimension.x, (int) dimension.y);
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
}
