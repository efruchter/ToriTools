package burd.customscripts;

import java.awt.event.KeyEvent;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;
import burd.BurdGame;

public class BurdScript implements EntityScript {

	PhysicsModule physicsModule;

	/**
	 * defaults overwritten by player.entity settings.
	 */
	private float hSpeed = .2f, vSpeed = .4f;

	private Vector2 startPos;

	public void onSpawn(Level level, Entity self) {

		hSpeed = self.variables.getFloat("hSpeed");

		vSpeed = self.variables.getFloat("vSpeed");

		startPos = self.pos.clone();

		physicsModule = new PhysicsModule(new Vector2(0, 0.2f), 1f, self);

		System.out.println("The burd is spawned!");
		String warpTo;
		if ((warpTo = ScriptUtils.getVar("warpTo")) != null) {
			Entity portal;
			if ((portal = level.getEntityWithId(warpTo)) != null) {
				self.pos = portal.pos.clone();
				ScriptUtils.setVar("warpTo", null);
			} else {
				System.out.println("Could not warp burd to " + warpTo + "!");
			}
		}

		physicsModule.onStart();
	}

	public void onUpdate(Level level, Entity self) {
		boolean leftKey = BurdGame.keys.isPressed(KeyEvent.VK_Z);
		boolean rightKey = BurdGame.keys.isPressed(KeyEvent.VK_M);

		boolean moved = false;

		if (rightKey && leftKey) {
			physicsModule.addVelocity(new Vector2(0, vSpeed / 2));
			physicsModule.clearXVelocity();
			self.sprite.setCylcle(0);
			moved = true;
		} else if (leftKey) {
			physicsModule.addVelocity(new Vector2(-hSpeed, -vSpeed));
			self.sprite.setCylcle(1);
			moved = true;
		} else if (rightKey) {
			physicsModule.addVelocity(new Vector2(hSpeed, -vSpeed));
			self.sprite.setCylcle(2);
			moved = true;
		}

		if (moved) {
			self.sprite.nextFrame();
		}

		ScriptUtils.safeMove(self, physicsModule.onUpdate(),
				level.solids.toArray(new Entity[0]));

		for (Entity spike : level.getEntitiesWithType("spike")) {
			if (ScriptUtils.isColliding(spike, self)) {
				for (int i = 0; i < 10; i++) {
					Entity blood = ValcanoParticleScript.getBlood();
					blood.pos = self.pos.clone();
					level.spawnEntity(blood);
				}
				self.pos = startPos.clone();
				physicsModule.clearVelocity();
			}
		}

		for (Entity flag : level.getEntitiesWithType("flag"))
			if (ScriptUtils.isColliding(flag, self)) {
				startPos = flag.pos.clone();
				level.killEntity(flag);
			}

	}

	public void onDeath(Level level, Entity self, boolean isRoomExit) {
		if (isRoomExit)
			System.out.println("Goodbye burd.");
	}
}
