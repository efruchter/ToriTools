package samplegame.entities;

import java.io.File;
import java.io.FileNotFoundException;

import samplegame.entrypoint.SampleGame;
import samplegame.load.Importer;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

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
				visible = SampleGame.debug;
				if (isWarp && ScriptUtils.isColliding(self, player)) {
					ScriptUtils.setVar("warpTo", variables.getVar("warpTo"));
					try {
						SampleGame.warpToLevel(Importer.importLevel(new File(variables.getVar("level"))));
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