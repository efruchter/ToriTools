package snakemeleon.types;

import java.awt.event.KeyEvent;
import java.io.File;

import snakemeleon.Snakemeleon;
import snakemeleon.SnakemeleonConstants;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entity.sprite.ImageSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;
import toritools.scripting.ScriptUtils.Direction;

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
        boolean isStanding = Snakemeleon.uni.isCollidingWithType(self, ReservedTypes.WALL);
        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A))
            dx += -.1;

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D))
            dx += .1;

        if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_W) && isStanding)
            dy += -4f;

        if (dx != 0 || dy != 0) {
            Snakemeleon.uni.applyLinearImpulse(self, new Vector2(dx, dy));
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

        // Adjust head again, just in case
        Direction headDir = tongue.getTongueFacing();
        if (headDir == Direction.RIGHT) {
            head.getSprite().setCycle(0);
        } else if (headDir == Direction.LEFT) {
            head.getSprite().setCycle(1);
        }

        if (facing) {
            mouthSpot = self.getPos().add(self.getDim().scale(.5f));
            mouthSpot = mouthSpot.add(SnakemeleonConstants.neckWidth * (float) Math.cos(self.getDirection() / 57.3),
                    SnakemeleonConstants.neckWidth * (float) Math.sin(self.getDirection() / 57.3));
            mouthSpot = mouthSpot.sub(head.getDim().scale(.5f));
        } else {
            mouthSpot = self.getPos().add(self.getDim().scale(.5f));
            mouthSpot = mouthSpot.sub(SnakemeleonConstants.neckWidth * (float) Math.cos(self.getDirection() / 57.3),
                    SnakemeleonConstants.neckWidth * (float) Math.sin(self.getDirection() / 57.3));
            mouthSpot = mouthSpot.sub(head.getDim().scale(.5f));
        }

        head.setPos(mouthSpot);
        tongue.setMouthPoint(mouthSpot);
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        System.out.println("Removed Chameleon");
    }

}
