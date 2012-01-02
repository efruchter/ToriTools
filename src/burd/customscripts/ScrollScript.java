package burd.customscripts;

import toritools.entity.Entity;
import toritools.entity.Level;
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
	public void onSpawn(Level level, Entity self) {
		speed = self.variables.getFloatOrDefault("speed", speed);
	}

	@Override
	public void onUpdate(Level level, Entity self) {
		self.pos = self.pos.add(hor ? new Vector2(speed, 0) : new Vector2(0,
				speed));
		for (Entity e : level.solids) {
			if (e != self && ScriptUtils.isColliding(self, e)) {
				speed = speed * -1;
			}
		}

	}

	@Override
	public void onDeath(Level level, Entity self, boolean isRoomExit) {
		// TODO Auto-generated method stub

	}

}
