package burd.customscripts;

import toritools.entity.Entity;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class ScrollScript implements EntityScript {

	boolean hor;
	float speed = 1;

	public ScrollScript(final boolean hor) {
		this.hor = hor;
	}

	@Override
	public void onSpawn(Entity self) {
		speed = self.getVariableCase().getFloatOrDefault("speed", speed);
	}

	@Override
	public void onUpdate(Entity self) {
		self.setPos(self.getPos().add(hor ? new Vector2(speed, 0) : new Vector2(0,
				speed)));
		for (Entity e : ScriptUtils.getCurrentLevel().getSolids()) {
			if (e != self && ScriptUtils.isColliding(self, e)) {
				speed = speed * -1;
			}
		}

	}

	@Override
	public void onDeath(Entity self, boolean isRoomExit) {

	}

}
