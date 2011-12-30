package samplegame.customscripts;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

import samplegame.SampleGame;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.io.Importer;
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
public class WorldPortalScript implements EntityScript {
    Entity player;
    boolean isWarp;

    @Override
    public void onSpawn(Level level, Entity self) {
        player = level.getEntityWithId("player");
        isWarp = self.variables.getVar("warpTo") != null;
    }

    @Override
    public void onUpdate(Level level, Entity self) {
        self.visible = SampleGame.debug;
        if (isWarp && ScriptUtils.isColliding(self, player)) {
            SampleGame.setDisplayPrompt("Enter <SPACE>");
            if (SampleGame.keys.isPressedThenRelease(KeyEvent.VK_SPACE)) {
                ScriptUtils.setVar("warpTo", self.variables.getVar("warpTo"));
                try {
                    SampleGame.warpToLevel(Importer.importLevel(new File(self.variables
                            .getVar("level"))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDeath(Level level, Entity self, boolean isRoomExit) {
        // TODO Auto-generated method stub

    }

}
