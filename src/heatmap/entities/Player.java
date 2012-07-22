package heatmap.entities;

import java.awt.event.KeyEvent;
import java.io.File;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.ImageSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Player extends Entity implements EntityScript {

	public Player() {
		this.addScript(this);

		this.setDim(new Vector2(64));
		this.setPos(new Vector2(200, 200));

		this.setSprite(new ImageSprite(new File("heatmap/ship.png"), 2, 1));
		this.getSprite().setTimeStretch(5);
	}

	@Override
	public void onSpawn(Entity self, Level level) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(Entity self, long time, Level level) {
		float speed = .15f;
		speed *= time;
		Vector2 delta = new Vector2();

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A)) {
			delta = delta.add(-speed, 0);
		}
		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D)) {
			delta = delta.add(speed, 0);
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_W)) {
			delta = delta.add(0, -speed);
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_S)) {
			delta = delta.add(0, speed);
		}

		self.setPos(self.getPos().add(delta));

		self.getSprite().nextFrame();
	}

	@Override
	public void onDeath(Entity self, Level level, boolean isRoomExit) {
		// TODO Auto-generated method stub

	}

}
