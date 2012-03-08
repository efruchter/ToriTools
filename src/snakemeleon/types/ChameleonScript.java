package snakemeleon.types;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class ChameleonScript implements EntityScript {

    @Override
    public void onSpawn(Entity self, Level level) {
        self.setDim(new Vector2(64, 64));
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        self.getSprite().nextFrame();
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        // TODO Auto-generated method stub

    }

}
