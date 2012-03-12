package snakemeleon.types;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import snakemeleon.Snakemeleon;
import snakemeleon.SnakemeleonConstants;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Tongue extends Entity {

    MidpointChain tongueChain;

    public Tongue(final Entity stickToThis) {

        this.getVariableCase().setVar("id", "tongue");

        this.setDim(new Vector2(100, 100));
        this.setPos(Snakemeleon.mousePos);

        tongueChain = new MidpointChain(stickToThis.getPos().add(stickToThis.getDim().scale(.5f)),
                SnakemeleonConstants.tongueLength);

        this.addScript(new EntityScript() {

            // Body physicsBody;

            @Override
            public void onSpawn(Entity self, Level level) {
                System.out.println("Tongue spawned");
            }

            Entity dragging = null;

            @Override
            public void onUpdate(Entity self, float time, Level level) {

                if (Snakemeleon.isMouseDragging && dragging == null) {
                    for (Entity e : level.getEntitiesWithType(SnakemeleonConstants.dynamicPropType)) {
                        if (ScriptUtils.isPointWithin(e, Snakemeleon.mousePos)) {
                            dragging = e;
                            tongueChain.setA(e.getPos().add(e.getDim().scale(.5f)));
                            break;
                        }
                    }
                }

                if (Snakemeleon.isMouseDragging && dragging != null) {
                    tongueChain.smooth();
                    Vector2 dragAnchor = dragging.getPos().add(dragging.getDim().scale(.5f));
                    tongueChain.setA(dragAnchor);
                    Snakemeleon.uni.setVeclocity(
                            dragging,
                            Snakemeleon.mousePos.sub(dragAnchor).scale(
                                    1f / Snakemeleon.uni.PTM_RATIO));
                } else {
                    tongueChain.smoothTowardB();
                    tongueChain.smoothTowardB();
                    tongueChain.smoothTowardB();
                    dragging = null;
                    if (tongueChain.getA().dist(tongueChain.getB()) < .5f) {
                        level.despawnEntity(self);
                    }
                }

                tongueChain.setB(stickToThis.getPos().add(stickToThis.getDim().scale(.5f)));
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {
                System.out.println("Tongue retracted");
                Snakemeleon.uni.removeEntity(self);
            }
        });

        this.setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics g, Entity self) {
                g.setColor(Color.RED);
                ((Graphics2D) g).setStroke(new BasicStroke(5));
                Vector2[] chain = tongueChain.getChain();
                for (int x = 1; x < SnakemeleonConstants.tongueLength; x++) {
                    g.drawLine(chain[x - 1].getWidth(), chain[x - 1].getHeight(), chain[x].getWidth(),
                            chain[x].getHeight());
                }
            }
        });
    }
}
