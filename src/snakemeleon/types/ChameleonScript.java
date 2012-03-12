package snakemeleon.types;

import java.awt.event.KeyEvent;

import snakemeleon.Snakemeleon;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class ChameleonScript implements EntityScript {

    @Override
    public void onSpawn(Entity self, Level level) {

    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        if (Snakemeleon.isMouseDragging && level.getEntityWithId("tongue") == null)
            level.spawnEntity(new Tongue(self));

        float dx = 0, dy = 0;
        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A))
            dx += -1;

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D))
            dx += 1;

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_W))
            dy += -5;
        if (dx != 0 || dy != 0) {
            Snakemeleon.uni.applyForce(self, new Vector2(dx, dy));
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        System.out.println("Removed Chameleon");
    }

}
