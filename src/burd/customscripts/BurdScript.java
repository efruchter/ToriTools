package burd.customscripts;

import java.awt.event.KeyEvent;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class BurdScript implements EntityScript {

	private PhysicsModule physicsModule;

	private Entity latestFlag;

	private float hSpeed = .2f, vSpeed = .5f;

	private Vector2 startPos;

	public boolean inAir = true;

	public void onSpawn(Level level, Entity self) {

		startPos = self.getPos().clone();

		physicsModule = new PhysicsModule(new Vector2(0, 0.2f), 1f, self);
	}

	public void onUpdate(Level level, Entity self) {

		boolean flapped = false;

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_SPACE)) {
			physicsModule.addVelocity(new Vector2(0, -vSpeed));
			flapped = true;
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A)) {
			physicsModule.addVelocity(new Vector2(-hSpeed, 0));
		}
		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D)) {
			physicsModule.addVelocity(new Vector2(hSpeed, 0));

		}

		Vector2 delta = physicsModule.onUpdate();

		self.setPos(self.getPos().add(delta));
		boolean onGround = ScriptUtils.moveOut(self, true, level.solids).mag() != 0;

		if (onGround || !inAir)
			physicsModule.setgDrag(.92f);
		else
			physicsModule.setgDrag(1f);

		if (Math.abs(delta.x) < 1)
			self.getSprite().setCycle(0);
		else if (delta.x < 0)
			self.getSprite().setCycle(1);
		else
			self.getSprite().setCycle(2);

		if (flapped || (onGround && Math.abs(delta.x) > 1)) {
			self.getSprite().nextFrame();
		} else {
			// make the wing match the motion
			if (Math.abs(delta.y) < 5)
				self.getSprite().setFrame(1);
			else if (delta.y < 0)
				self.getSprite().setFrame(2);
			else
				self.getSprite().setFrame(0);
		}

		if (!ScriptUtils.isDebugMode()) {
			for (Entity spike : level.getEntitiesWithType("spike", "puffer")) {
				if (spike.isInView() && ScriptUtils.isColliding(spike, self)) {
					for (Entity bread : level.getEntitiesWithType("bread")) {
						bread.setActive(true);
					}
					for (int i = 0; i < 15; i++) {
						Entity blood = VolcanoParticleScript.getBlood();
						blood.setPos(self.getPos().clone());
						level.spawnEntity(blood);
					}
					self.setPos(startPos.clone());
					physicsModule.clearVelocity();
					break;
				}

			}
		}

		for (Entity flag : level.getEntitiesWithType("flag"))
			if (flag.isInView() && ScriptUtils.isColliding(flag, self)
					&& (flag != latestFlag)) {
				if (latestFlag != null)
					latestFlag.getSprite().setFrame(0);
				latestFlag = flag;
				latestFlag.getSprite().setFrame(1);
				startPos = flag.getPos().clone();
			}

		for (Entity air : level.getEntitiesWithType("inAir"))
			if (air.isInView() && ScriptUtils.isColliding(air, self)) {
				inAir = true;
			}
		for (Entity water : level.getEntitiesWithType("inWater"))
			if (water.isInView() && ScriptUtils.isColliding(water, self)) {
				inAir = false;
			}

		if (!inAir && Math.random() < .04) {
			Entity bubble = BubbleScript.getBubbleEntity();
			bubble.setPos(self.getPos().clone());
			level.spawnEntity(bubble);
		}
	}

	public void onDeath(Level level, Entity self, boolean isRoomExit) {
	}
}
