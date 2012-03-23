package snakemeleon.types;

import java.io.File;
import java.io.FileNotFoundException;

import org.jbox2d.dynamics.BodyType;

import snakemeleon.Snakemeleon;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.io.Importer;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Collectable implements EntityScript {

    private static int collectablesRemaining = 0;

    private Entity player;

    @Override
    public void onSpawn(Entity self, Level level) {
        player = level.getEntityWithId("player");
        collectablesRemaining++;
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        if (ScriptUtils.isColliding(self, player)) {
            level.despawnEntity(self);
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        collectablesRemaining--;
        try {
            for (int i = 0; i < 2; i++)
                for (int i2 = 0; i2 < 2; i2++) {
                    Entity e = Importer.importEntity(new File("snakemeleon/objects/collectable/appleBit.entity"), null);
                    e.setPos(self.getPos());
                    e.getSprite().set(i, i2);
                    level.spawnEntity(e);
                    Snakemeleon.uni.addEntity(e, BodyType.DYNAMIC, true, true, .01f, .03f);
                }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getCollectablesRemaining() {
        return collectablesRemaining;
    }

}
