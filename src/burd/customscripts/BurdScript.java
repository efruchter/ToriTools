package burd.customscripts;

import java.awt.event.KeyEvent;
import java.util.List;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;
import burd.BurdGame;

public class BurdScript implements EntityScript {

	PhysicsModule physicsModule;

	private final Vector2 leftVect = new Vector2(-.5f, -.5f),
			rightVect = new Vector2(.5f, -.5f);

	private List<Entity> walls;

	private Vector2 startPos;

	public void onSpawn(Level level, Entity self) {

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

		walls = level.getEntityWithType("spike");
	}

	public void onUpdate(Level level, Entity self) {
		boolean leftKey = BurdGame.keys.isPressed(KeyEvent.VK_A);
		boolean rightKey = BurdGame.keys.isPressed(KeyEvent.VK_D);

		boolean moved = false;

		if (leftKey) {
			physicsModule.addVelocity(leftVect);
			self.sprite.setCylcle(1);
			moved = true;
		}

		if (rightKey) {
			physicsModule.addVelocity(rightVect);
			self.sprite.setCylcle(0);
			moved = true;
		}

		if (moved) {
			self.sprite.nextFrame();
		}

		ScriptUtils.safeMove(self, physicsModule.onUpdate(),
				level.solids.toArray(new Entity[0]));

		for (Entity spikes : walls) {
			if (ScriptUtils.isColliding(spikes, self)) {
				self.pos = startPos.clone();
			}
		}

	}

	public void onDeath(Level level, Entity self, boolean isRoomExit) {
		if (isRoomExit)
			System.out.println("Goodbye burd.");
	}
}
