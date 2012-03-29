package snakemeleon.types;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

import maryb.player.Player;

import org.jbox2d.dynamics.BodyType;

import snakemeleon.Snakemeleon;
import snakemeleon.SnakemeleonConstants;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.ImageSprite;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;
import toritools.scripting.ScriptUtils.Direction;

public class ChameleonScript implements EntityScript {

    Entity head;
    Tongue tongue;
    ChameleonFootSensor sensor;

    private static Player yellPlayer;

    static {
        yellPlayer = new Player();
        yellPlayer.setSourceLocation("snakemeleon/sounds/yell.mp3");
    }

    @Override
    public void onSpawn(Entity self, Level level) {
        level.spawnEntity(tongue = new Tongue());
        head = new Entity();
        head.setDim(new Vector2(SnakemeleonConstants.headWidth));
        head.setSprite(new ImageSprite(new File("snakemeleon/objects/chameleon/cham_head.png"), 2, 2));
        level.spawnEntity(head);

        head.getSprite().setCycle(1);
        self.getSprite().setCycle(0);

        level.spawnEntity(sensor = new ChameleonFootSensor(self));

        Snakemeleon.uni.addEntity(self, BodyType.DYNAMIC, true, true, 1f, .3f).setAngularDamping(5);
    }

    boolean facing = false;
    Vector2 mouthSpot = Vector2.ZERO;

    long deathCounter = 120;

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        if (!self.isActive()) {
            if (self.isVisible()) {
                yellPlayer.play();
                makeAppleBits(self, level);
            }
            self.setVisible(false);
            head.setVisible(false);
            if (deathCounter-- < 0) {
                Snakemeleon.restartLevel();
            }
        } else {

            head.getSprite().setFrame(Snakemeleon.isMouseDragging ? 1 : 0);

            float dx = 0, dy = 0;

            if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_A))
                dx += -.1;

            if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_D))
                dx += .1;

            if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_W) && sensor.canJump())
                dy += -1f;

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
                mouthSpot = mouthSpot.add(
                        SnakemeleonConstants.neckWidth * (float) Math.cos(self.getDirection() / 57.3),
                        SnakemeleonConstants.neckWidth * (float) Math.sin(self.getDirection() / 57.3));
                mouthSpot = mouthSpot.sub(head.getDim().scale(.5f));
            } else {
                mouthSpot = self.getPos().add(self.getDim().scale(.5f));
                mouthSpot = mouthSpot.sub(
                        SnakemeleonConstants.neckWidth * (float) Math.cos(self.getDirection() / 57.3),
                        SnakemeleonConstants.neckWidth * (float) Math.sin(self.getDirection() / 57.3));
                mouthSpot = mouthSpot.sub(head.getDim().scale(.5f));
            }

            head.setPos(mouthSpot);
            tongue.setMouthPoint(mouthSpot);
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {
        Debug.print("Removed Chameleon");
    }

    public void makeAppleBits(Entity self, Level level) {
        try {
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < 2; i++) {
                    for (int i2 = 0; i2 < 2; i2++) {
                        Entity e = Importer.importEntity(new File("snakemeleon/objects/collectable/appleBit.entity"),
                                null);
                        e.setPos(self.getPos());
                        e.getSprite().set(i, i2);
                        level.spawnEntity(e);
                        Snakemeleon.uni.addEntity(e, BodyType.DYNAMIC, true, true, .01f, .03f);
                        Snakemeleon.uni.applyLinearImpulse(e, new Vector2((float) (-.5 + Math.random()) * .000008f,
                                (float) (-.5 + Math.random()) * .000008f));
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
