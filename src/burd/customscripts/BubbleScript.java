package burd.customscripts;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class BubbleScript implements EntityScript {

	@Override
	public void onSpawn(Level level, Entity self) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(Level level, Entity self) {
		self.pos = self.pos
				.add(new Vector2(-1 + (float) Math.random() * 3, -1));
		for (Entity wall : level.solids) {
			if (ScriptUtils.isColliding(self, wall)) {
				level.killEntity(self);
				break;
			}
		}
		for (Entity wall : level.getEntitiesWithType("inAir")) {
			if (ScriptUtils.isColliding(self, wall)) {
				level.killEntity(self);
				break;
			}
		}

	}

	@Override
	public void onDeath(Level level, Entity self, boolean isRoomExit) {
		// TODO Auto-generated method stub

	}

}
