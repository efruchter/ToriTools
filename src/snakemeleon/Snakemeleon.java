package snakemeleon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;

import snakemeleon.types.ChameleonScript;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.physics.Universe;
import toritools.scripting.ScriptUtils;

public class Snakemeleon extends Binary {

    public static void main(String[] args) {
        new Snakemeleon();
    }

    public Snakemeleon() {
        super(new Vector2(800, 600), 60, "Snakemeleon");
        super.getApplicationFrame().setIconImage(new ImageIcon("snakemeleon/chameleon_head.png").getImage());
    }

    @Override
    protected void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void globalLogic(Level level) {

        // body.applyLinearImpulse(new Vec2(100, 0), body.getPosition());
        // body.applyTorque(500f);

        uni.step(60 / 1000f);
    }

    @Override
    protected boolean render(Graphics rootCanvas, Level level) {
        try {
            ((Graphics2D) rootCanvas).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Entity player = level.getEntityWithId("player");
            if (player == null) {
                System.out.println("You need to make an entity with id set to player!");
                System.exit(1);
            }
            // Vector2 playerPos = player.getPos();
            Vector2 offset = Vector2.ZERO; // VIEWPORT.scale(.5f).sub(playerPos);

            rootCanvas.setColor(Color.BLACK);
            rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
            for (int i = level.getLayers().size() - 1; i >= 0; i--) {
                for (Entity e : level.getLayers().get(i)) {
                    if (e.isVisible() && e.isInView())
                        e.draw(rootCanvas, offset);
                    if (!"BACKGROUND".equals(e.getType()) && ScriptUtils.isDebugMode()) {
                        rootCanvas.setColor(Color.RED);
                        rootCanvas.drawRect((int) (e.getPos().x + offset.x), (int) (e.getPos().y + offset.y),
                                (int) e.getDim().x, (int) e.getDim().y);
                    }

                }
            }

            rootCanvas.setColor(Color.RED);
            for (Entity wall : level.getEntitiesWithType("WALL")) {
                rootCanvas.fillRect(wall.getPos().getWidth(), wall.getPos().getHeight(), wall.getDim().getWidth(), wall
                        .getDim().getHeight());
            }

        } catch (final Exception e) {
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    Universe uni;

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {
        uni = new Universe(new Vector2(0, 9.81f));
        for (Entity e : levelBeingLoaded.getEntitiesWithType("player")) {
            e.addScript(new ChameleonScript());
            uni.addEntity(e, true, false, true);
        }
        for (Entity e : levelBeingLoaded.getEntitiesWithType("WALL")) {
            uni.addEntity(e, false, true, true);
        }
    }

    @Override
    protected Level getStartingLevel() {
        try {
            return Importer.importLevel(new File("snakemeleon/TestLevel.xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
