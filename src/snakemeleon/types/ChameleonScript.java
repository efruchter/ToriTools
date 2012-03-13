package snakemeleon.types;

import java.awt.event.KeyEvent;
import java.io.File;

import snakemeleon.Snakemeleon;
import snakemeleon.SnakemeleonConstants;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.ImageSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class ChameleonScript implements EntityScript {

    Entity head;
    Tongue tongue;

    @Override
    public void onSpawn(Entity self, Level level) {
        level.spawnEntity(tongue = new Tongue());
        head = new Entity();
        head.setDim(new Vector2(SnakemeleonConstants.headWidth));
        head.setSprite(new ImageSprite(new File("snakemeleon/objects/chameleon/cham_head.png"), 2, 2));
        level.spawnEntity(head);
        
        head.getSprite().setCycle(1);
        self.getSprite().setCycle(0);
    }

    boolean facing = false;
    Vector2 mouthSpot = Vector2.ZERO;

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        head.getSprite().setFrame(Snakemeleon.isMouseDragging ? 1 : 0);

        float dx = 0, dy = 0;
        boolean isStanding = Snakemeleon.uni.isStanding(self);
        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A))
            dx += -3;

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D))
            dx += 3;

        if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_W) && isStanding)
            dy += -3f;

        if (dx != 0 || dy != 0) {
            Snakemeleon.uni.applyForce(self, new Vector2(dx, 0));
            Snakemeleon.uni.addVelocity(self, new Vector2(0, dy));
            if (Math.abs(dx) > .1)
                self.getSprite().nextFrame();
        }

        if (dx < 0) {
            head.getSprite().setCycle(1);
            self.getSprite().setCycle(0);
            facing = false;

        } else if (dx > 0) {
            head.getSprite().setCycle(0);
            self.getSprite().setCycle(1);
            facing = true;
        }

        if (facing)
            head.setPos(mouthSpot = self.getPos().add(self.getDim().x * 2 / 3, self.getDim().y / 4));
        else
            head.setPos(mouthSpot = self.getPos().add(-self.getDim().x / 3, self.getDim().y / 4));

        tongue.setMouthPoint(mouthSpot);
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        System.out.println("Removed Chameleon");
    }

}
