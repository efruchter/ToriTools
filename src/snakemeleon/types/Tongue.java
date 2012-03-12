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
                // physicsBody = Snakemeleon.uni.addEntity(self,
                // BodyType.KINEMATIC, false, true, 5);
            }

            @Override
            public void onUpdate(Entity self, float time, Level level) {

                if (Snakemeleon.isMouseDragging) {
                    tongueChain.smooth();
                    for (Entity prop : level.getEntitiesWithType(SnakemeleonConstants.dynamicPropType)) {
                        Snakemeleon.uni.applyForce(prop, Vector2.toward(prop.getPos(), tongueChain.getA()));
                    }
                } else {
                    tongueChain.smoothTowardB();
                    tongueChain.smoothTowardB();
                    tongueChain.smoothTowardB();
                    if (tongueChain.getA().dist(tongueChain.getB()) < .5f) {
                        level.despawnEntity(self);
                    }
                }

                tongueChain.setB(stickToThis.getPos().add(stickToThis.getDim().scale(.5f)));

                if (Snakemeleon.isMouseDragging) {
                    tongueChain.setA(Snakemeleon.mousePos);
                }

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
