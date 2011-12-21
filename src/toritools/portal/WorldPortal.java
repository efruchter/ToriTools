package toritools.portal;

import java.io.FileNotFoundException;

import samplegame.entrypoint.Game_J2d;
import samplegame.scripting.EntityScript;
import samplegame.scripting.ScriptUtils;
import toritools.entity.Entity;
import toritools.entity.Level;

/**
 * A level portal! To use it in the editor, set teh following two instance
 * params for a particular portal- "level": the relative path to the file.
 * "warpTo": the id of the portal to warp to. Obviously, you should set the id
 * param int he correponding portal!
 * 
 * @author toriscope
 * 
 */
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
				visible = Game_J2d.debug;
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
