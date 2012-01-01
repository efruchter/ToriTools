package burd.customscripts;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class BreadScript implements EntityScript {

	MidpointChain chain;

	Vector2 origPos;

	@Override
	public void onSpawn(Level level, Entity self) {

		Vector2 playerPos = level.getEntityWithId("player").pos;

		// First spawn
		if (origPos == null) {
			origPos = self.pos.clone();
		}

		chain = new MidpointChain(playerPos, origPos, 30);

		self.pos = chain.getA().clone();

		self.active = false;
	}

	@Override
	public void onUpdate(Level level, Entity self) {
		if (!self.active) {
			if (!(self.active = Vector2.dist(chain.getA(), origPos) < 5)) {
				chain.smoothTowardB();
				self.pos = chain.getA().clone();
			}
		}
	}

	@Override
	public void onDeath(Level level, Entity self, boolean isRoomExit) {
		// TODO Auto-generated method stub

	}

}
