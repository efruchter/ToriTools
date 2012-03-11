package snakemeleon.types;

import java.awt.event.KeyEvent;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import snakemeleon.Snakemeleon;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class ChameleonScript implements EntityScript {

    Body body;

    @Override
    public void onSpawn(Entity self, Level level) {
        body = Snakemeleon.uni.getBody(self);
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        self.getSprite().nextFrame();

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A)) {
            body.applyForce(new Vec2(-1, 0), body.getWorldCenter());
        }
        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D)) {
            body.applyForce(new Vec2(1, 0), body.getWorldCenter());
        }

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_W)) {
            body.applyForce(new Vec2(0, -5), body.getWorldCenter());
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        // TODO Auto-generated method stub

    }

}
