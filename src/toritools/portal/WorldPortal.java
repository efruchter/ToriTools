package toritools.portal;

import java.io.FileNotFoundException;

import samplegame.scripting.EntityScript;
import samplegame.scripting.ScriptUtils;
import toritools.entity.Entity;
import toritools.entity.Level;

public class WorldPortal extends Entity {
	public WorldPortal() {
		this.script = new EntityScript() {

			Entity player;
			boolean isWarp;

			@Override
			public void onSpawn(Level level, Entity self) {
				player = level.getEntityWithId("player");
				isWarp = variables.getVar("warpTo") != null;
			}

			@Override
			public void onUpdate(Level level, Entity self) {
				if (isWarp && ScriptUtils.isColliding(self, player)) {
					ScriptUtils.setVar("warpTo", variables.getVar("warpTo"));
					try {
						ScriptUtils.changeLevel(variables.getVar("level"));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onDeath(Level level, Entity self, boolean isRoomExit) {
				// TODO Auto-generated method stub

			}

		};
	}
}
