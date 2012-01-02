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

		// First spawn
		if (origPos == null) {
			origPos = self.pos.clone();
		}

		chain = new MidpointChain(self.pos.clone(), origPos, 20);

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
		for (int i = 0; i < 5; i++) {
			Entity crumb = VolcanoParticleScript.getBreadCrumb();
			crumb.pos = self.pos.clone();
			level.spawnEntity(crumb);
		}
	}

}