package samplegame.entities;

import java.util.Random;

import samplegame.scripting.EntityScript;
import samplegame.scripting.ScriptUtils;
import samplegame.scripting.ScriptUtils.Direction;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;

public class WolfScript implements EntityScript {
	private Random rand = new Random();
	private float speed = 4;
	private float direction = 0;

	public void onSpawn(Level level, Entity self) {
		newDirection();
	}

	public void onUpdate(Level level, Entity self) {
		if (rand.nextDouble() > .99)
			newDirection();
		if (rand.nextDouble() > .8) {
			ScriptUtils.safeMove(self,
					Vector2.buildVector(direction).scale(speed),
					level.solids.toArray(new Entity[0]));
			self.sprite.nextFrame();
		}

		switch (Direction.findEnum(direction)) {
		case DOWN:
		case DOWN_RIGHT:
		case DOWN_LEFT:
			self.sprite.setCylcle(3);
			break;
		case UP:
		case UP_RIGHT:
		case UP_LEFT:
			self.sprite.setCylcle(0);
			break;
		case RIGHT:
			self.sprite.setCylcle(2);
			break;
		case LEFT:
			self.sprite.setCylcle(1);
			break;
		}

		if (ScriptUtils.isColliding(self, level.getIntityWithId("player"))) {
			System.out.println("MUNCH!");
		}
	}

	public void onDeath(Level level, Entity self, boolean isRoomExit) {
	}

	private void newDirection() {
		direction = rand.nextFloat() * 6.28f;
	}
}
