package burd.customscripts;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
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

	/**
	 * defaults overwritten by player.entity settings.
	 */
	private float hSpeed = .1f, vSpeed = .3f;

	private Vector2 startPos;

	private List<Entity> breadsEaten = new ArrayList<Entity>();

	public void onSpawn(Level level, Entity self) {

//		hSpeed = self.variables.getFloat("hSpeed");
//
//		vSpeed = self.variables.getFloat("vSpeed");

		startPos = self.pos.clone();

		physicsModule = new PhysicsModule(new Vector2(0, 0.2f), 1f, self);

		physicsModule.onStart();
	}

	public void onUpdate(Level level, Entity self) {
		boolean leftKey = BurdGame.keys.isPressed(KeyEvent.VK_Z);
		boolean rightKey = BurdGame.keys.isPressed(KeyEvent.VK_M);

		boolean moved = false;

		if (rightKey && leftKey) {
			physicsModule.addVelocity(new Vector2(0, vSpeed / 4));
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
					Entity blood = VolcanoParticleScript.getBlood();
					blood.pos = self.pos.clone();
					level.spawnEntity(blood);
				}
				if (!breadsEaten.isEmpty()) {
					for (Entity bread : breadsEaten) {
						bread.pos = self.pos.clone();
						level.spawnEntity(bread);
					}
					breadsEaten.clear();
				}
				self.pos = startPos.clone();
				physicsModule.clearVelocity();
				break;
			}

		}

		for (Entity flag : level.getEntitiesWithType("flag"))
			if (ScriptUtils.isColliding(flag, self)
					&& (flag != latestFlag || !breadsEaten.isEmpty())) {
				latestFlag = flag;
				if (!breadsEaten.isEmpty())
					breadsEaten.clear();
				startPos = flag.pos.clone();
				for (int i = 0; i < 10; i++) {
					Entity sparkle = VolcanoParticleScript.getSparkle();
					sparkle.pos = self.pos.clone();
					level.spawnEntity(sparkle);
				}
			}

		List<Entity> breads = level.getEntitiesWithType("bread");
		if (breads == null || breads.isEmpty()) {
			BurdGame.nextLevel();
		} else {
			for (Entity bread : breads) {
				if (bread.active && ScriptUtils.isColliding(bread, self)) {
					level.killEntity(bread);
					breadsEaten.add(bread);
				}
			}
		}
	}

	private Entity latestFlag;

	public void onDeath(Level level, Entity self, boolean isRoomExit) {
	}
}
