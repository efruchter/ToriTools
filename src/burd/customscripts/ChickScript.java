package burd.customscripts;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class ChickScript implements EntityScript {

	PhysicsModule physicsModule;

	int flapTime;
	int timer = 0;

	@Override
	public void onSpawn(Level level, Entity self) {
		physicsModule = new PhysicsModule(new Vector2(0, 0.06f), 1f, self);
		physicsModule.clearVelocity();
		flapTime = 100;
		self.getSprite().timeStretch = 1;
	}

	@Override
	public void onUpdate(Level level, Entity self) {

		if (--timer < 0) {
			timer = (int) (flapTime * Math.random());
			physicsModule.addVelocity(new Vector2(-2
					+ (int) (Math.random() * 6),
					-(1 + (int) (Math.random() * 5))));
			self.getSprite().nextFrame();
		}

		Vector2 delta = physicsModule.onUpdate();

		self.setPos(self.getPos().add(delta));
		ScriptUtils.moveOut(self, false, level.getEntitiesWithType("inWater"));
		boolean onGround = ScriptUtils.moveOut(self, false, level.solids).mag() != 0;

		if (onGround)
			physicsModule.setgDrag(.95f);
		else
			physicsModule.setgDrag(1f);

		if (Math.abs(delta.x) < 1)
			self.getSprite().setCycle(0);
		else if (delta.x < 0)
			self.getSprite().setCycle(1);
		else
			self.getSprite().setCycle(2);

		// if ((onGround && Math.abs(delta.x) > 1)) {
		// self.sprite.nextFrame();
		// } else {
		// // make the wing match the motion
		// if (Math.abs(delta.y) < 5)
		// self.sprite.setFrame(1);
		// else if (delta.y < 0)
		// self.sprite.setFrame(2);
		// else
		// self.sprite.setFrame(0);
		// }
	}

	@Override
	public void onDeath(Level level, Entity self, boolean isRoomExit) {

	}

}
