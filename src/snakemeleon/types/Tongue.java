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

    private MidpointChain tongueChain;

    private Vector2 mouthPoint = Vector2.ZERO;

    private boolean mouthClosed = true;

    public Tongue() {

        this.getVariableCase().setVar("id", "tongue");

        this.setDim(new Vector2(100, 100));
        this.setPos(Snakemeleon.mousePos);

        tongueChain = new MidpointChain(mouthPoint, SnakemeleonConstants.tongueLength);

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
                            mouthClosed = false;
                            break;
                        }
                    }
                }

                if (Snakemeleon.isMouseDragging && dragging != null) {
                    tongueChain.smooth();
                    Vector2 dragAnchor = dragging.getPos().add(dragging.getDim().scale(.5f));
                    tongueChain.setA(dragAnchor);
                    Snakemeleon.uni.setVeclocity(dragging,
                            Snakemeleon.mousePos.sub(dragAnchor).scale(1f / Snakemeleon.uni.PTM_RATIO));
                } else {
                    tongueChain.smoothTowardB();
                    tongueChain.smoothTowardB();
                    tongueChain.smoothTowardB();
                    dragging = null;

                }

                tongueChain.setB(mouthPoint);

                mouthClosed = mouthClosed || tongueChain.getA().dist(tongueChain.getB()) < 20;
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {

            }
        });

        this.setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics g, Entity self) {
                if (mouthClosed)
                    return;
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

    public void setMouthPoint(Vector2 mouthPoint) {
        this.mouthPoint = mouthPoint.add(SnakemeleonConstants.headWidth / 2);
    }
}
