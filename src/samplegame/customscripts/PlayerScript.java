package samplegame.customscripts;

import java.awt.event.KeyEvent;

import samplegame.SampleGame;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class PlayerScript implements EntityScript {
	public void onSpawn(Level level, Entity self) {
		System.out.println("The kid is spawned!");
		String warpTo;
		if ((warpTo = ScriptUtils.getVar("warpTo")) != null) {
			Entity portal;
			if ((portal = level.getEntityWithId(warpTo)) != null) {
				self.pos = portal.pos.clone();
				ScriptUtils.setVar("warpTo", null);
			} else {
				System.out.println("Could not warp player to " + warpTo + "!");
			}
		}
	}

	public void onUpdate(Level level, Entity self) {
		int speed = 3;
		boolean walked = false;
		Vector2 delta = new Vector2();

		if (SampleGame.keys.isPressed(KeyEvent.VK_A)) {
			walked = true;
			delta.x -= speed;
			self.sprite.setCylcle(1);
		}
		if (SampleGame.keys.isPressed(KeyEvent.VK_D)) {
			walked = true;
			delta.x += speed;
			self.sprite.setCylcle(2);
		}

		if (SampleGame.keys.isPressed(KeyEvent.VK_W)) {
			walked = true;
			delta.y -= speed;
			self.sprite.setCylcle(3);
		}

		if (SampleGame.keys.isPressed(KeyEvent.VK_S)) {
			walked = true;
			delta.y += speed;
			self.sprite.setCylcle(0);
		}

		ScriptUtils.safeMove(self, delta, level.solids.toArray(new Entity[0]));

		if (walked)
			self.sprite.nextFrame();
	}

	public void onDeath(Level level, Entity self, boolean isRoomExit) {
		if (isRoomExit)
			System.out.println("The kid has been lost forever (room closed).");
	}
}
