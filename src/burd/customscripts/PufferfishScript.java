package burd.customscripts;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class PufferfishScript implements EntityScript {

	Entity player;
	final int activeDist = 200;

	@Override
	public void onSpawn(Level level, Entity self) {
		player = level.getEntityWithId("player");
	}

	@Override
	public void onUpdate(Level level, Entity self) {
		if (Vector2.dist(player.pos, self.pos) < activeDist) {
			Vector2 move;
			self.pos = self.pos.add(move = Vector2.toward(self.pos, player.pos).unit());
			self.sprite.setFrame(move.x > 0 ? 1 : 0);
			ScriptUtils.moveOut(self, false, level.getEntitiesWithType("inAir"));
			ScriptUtils.moveOut(self, false, level.solids);
			if (Math.random() < .04) {
				Entity bubble = BubbleScript.getBubbleEntity();
				bubble.pos = self.pos.clone();
				level.spawnEntity(bubble);
			}
		}
	}

	@Override
	public void onDeath(Level level, Entity self, boolean isRoomExit) {

	}

}
