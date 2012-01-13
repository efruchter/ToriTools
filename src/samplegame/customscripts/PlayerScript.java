package samplegame.customscripts;

import java.awt.event.KeyEvent;

import toritools.entity.Entity;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class PlayerScript implements EntityScript {
	public void onSpawn(Entity self) {
		System.out.println("The kid is spawned!");
		String warpTo;
		if ((warpTo = ScriptUtils.getVar("warpTo")) != null) {
			Entity portal;
			if ((portal = ScriptUtils.getCurrentLevel().getEntityWithId(warpTo)) != null) {
				self.setPos(portal.getPos().clone());
				ScriptUtils.setVar("warpTo", null);
			} else {
				System.out.println("Could not warp player to " + warpTo + "!");
			}
		}
	}

	public void onUpdate(Entity self) {
		int speed = 3;
		boolean walked = false;
		Vector2 delta = new Vector2();

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A)) {
			walked = true;
			delta.x -= speed;
			self.getSprite().setCycle(1);
		}
		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D)) {
			walked = true;
			delta.x += speed;
			self.getSprite().setCycle(2);
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_W)) {
			walked = true;
			delta.y -= speed;
			self.getSprite().setCycle(3);
		}

		if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_S)) {
			walked = true;
			delta.y += speed;
			self.getSprite().setCycle(0);
		}

		self.setPos(self.getPos().add(delta));

		ScriptUtils.moveOut(self, false, ScriptUtils.getCurrentLevel().solids);

		if (walked)
			self.getSprite().nextFrame();
	}

	public void onDeath(Entity self, boolean isRoomExit) {
		if (isRoomExit)
			System.out.println("The kid has been lost forever (room closed).");
	}
}
