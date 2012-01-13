package burd.customscripts;

import java.io.File;
import java.io.FileNotFoundException;

import toritools.entity.Entity;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class BubbleScript implements EntityScript {

	@Override
	public void onSpawn(Entity self) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(Entity self) {
		self.setPos(self.getPos().add(new Vector2(-1 + (float) Math.random() * 3, -1)));
		for (Entity wall : ScriptUtils.getCurrentLevel().solids) {
			if (ScriptUtils.isColliding(self, wall)) {
				ScriptUtils.getCurrentLevel().killEntity(self);
				break;
			}
		}
		for (Entity wall : ScriptUtils.getCurrentLevel().getEntitiesWithType("inAir")) {
			if (ScriptUtils.isColliding(self, wall)) {
				ScriptUtils.getCurrentLevel().killEntity(self);
				break;
			}
		}

	}

	@Override
	public void onDeath(Entity self, boolean isRoomExit) {

	}

	public static Entity getBubbleEntity() {
		Entity bubble;
		try {
			bubble = Importer.importEntity(new File("burd/objects/bubble.entity"), null);
			bubble.setScript(new BubbleScript());
			bubble.setDim(bubble.getDim().scale((float) Math.random()));
			return bubble;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
