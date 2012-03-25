package snakemeleon.types;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.dynamics.BodyType;

import snakemeleon.Snakemeleon;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class ChameleonFootSensor extends Entity {

    public ChameleonFootSensor(final Entity chameleon) {
        setDim(new Vector2(20, 20));
        addScript(new EntityScript() {

            @Override
            public void onSpawn(Entity self, Level level) {

            }

            @Override
            public void onUpdate(Entity self, float time, Level level) {
                Snakemeleon.uni.setTransform(self, chameleon.getPos().add(chameleon.getDim().scale(.5f, 1)), 0);
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {

            }

        });

        setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics2D g, Entity self) {
                if (Debug.showDebugPrintouts) {
                    g.setColor(canJump() ? Color.GREEN : Color.RED);
                    g.drawRect(self.getPos().getWidth(), self.getPos().getHeight(), self.getDim().getWidth(), self
                            .getDim().getHeight());
                }
            }
        });

        setPos(chameleon.getPos().add(chameleon.getDim()));
        Snakemeleon.uni.addEntity(this, BodyType.DYNAMIC, true, true, 1f, .3f, null, true);
    }

    public boolean canJump() {
        return Snakemeleon.jumpTouchQueue > 0 && !ChameleonStickyScript.isGrabbing;
    }
}
