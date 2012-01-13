package burd.customscripts;

import toritools.entity.Entity;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class PufferfishScript implements EntityScript {

	Entity player;
	final int activeDist = 200;

	@Override
	public void onSpawn(Entity self) {
		player = ScriptUtils.getCurrentLevel().getEntityWithId("player");
	}

	@Override
	public void onUpdate(Entity self) {
		if (Vector2.dist(player.getPos(), self.getPos()) < activeDist) {
			Vector2 move;
			self.setPos(self.getPos().add(move = Vector2.toward(self.getPos(), player.getPos()).unit()));
			self.getSprite().setFrame(move.x > 0 ? 1 : 0);
			ScriptUtils.moveOut(self, false, ScriptUtils.getCurrentLevel().getEntitiesWithType("inAir"));
			ScriptUtils.moveOut(self, false, ScriptUtils.getCurrentLevel().solids);
			if (Math.random() < .04) {
				Entity bubble = BubbleScript.getBubbleEntity();
				bubble.setPos(self.getPos().clone());
				ScriptUtils.getCurrentLevel().spawnEntity(bubble);
			}
		}
	}

	@Override
	public void onDeath(Entity self, boolean isRoomExit) {

	}

}
