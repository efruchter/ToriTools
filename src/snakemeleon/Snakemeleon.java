package snakemeleon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;

import maryb.player.Player;

import snakemeleon.types.ChameleonScript;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entrypoint.Binary;
import toritools.io.Importer;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.physics.Universe;
import toritools.scripting.ScriptUtils;

public class Snakemeleon extends Binary {

    /*
     * CONSTANTS!
     */

    /**
     * The core universe. Reference this to apply forces, etc.
     */
    public static Universe uni;

    /*
     * The midpoint chain with the a tracking the player, and being the camera
     * offset.
     */
    private static MidpointChain camera;

    public static void main(String[] args) {
        new Snakemeleon();
    }

    public Snakemeleon() {
        super(new Vector2(800, 600), 60, "Snakemeleon");
        super.getApplicationFrame().setIconImage(new ImageIcon("snakemeleon/chameleon_head.png").getImage());
    }

    @Override
    protected void initialize() {
        Player player = new Player();
        player.setSourceLocation("snakemeleon/sounds/BGM/Wallpaper.mp3");
        player.play();
    }

    @Override
    protected void globalLogic(Level level) {
        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }

        uni.step(60 / 1000f);

        // Camera step
        camera.setA(level.getEntityWithId(SnakemeleonConstants.playerTypeId).getPos());
        camera.smoothTowardA();
    }

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {

        uni = new Universe(SnakemeleonConstants.gravity);

        camera = new MidpointChain(levelBeingLoaded.getEntityWithId(SnakemeleonConstants.playerTypeId).getPos(), SnakemeleonConstants.cameraLag);

        for (Entity en : levelBeingLoaded.getEntitiesWithType(SnakemeleonConstants.playerTypeId)) {
            en.addScript(new ChameleonScript());
            uni.addEntity(en, true, true, true, 1);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType(ReservedTypes.WALL)) {
            uni.addEntity(e, false, false, false, .5f);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType(SnakemeleonConstants.dynamicPropType)) {
            uni.addEntity(e, true, true, false, .05f);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType(SnakemeleonConstants.hingeType)) {
            levelBeingLoaded.despawnEntity(e);
            Entity a = null, b = null;
            Vector2 pos = e.getPos().add(e.getDim().scale(.5f));
            for (Entity object : levelBeingLoaded.getNewEntities()) {
                if (!object.getType().equals(ReservedTypes.BACKGROUND) && ScriptUtils.isPointWithin(object, pos) && object != e) {
                    if (a == null)
                        a = object;
                    else if (a != null)
                        b = object;
                }
            }

            if (a != null && b != null)
                uni.addHinge(a, b, pos);
        }

        levelBeingLoaded.bakeBackground();
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

    @Override
    protected boolean render(Graphics rootCanvas, Level level) {
        try {
            ((Graphics2D) rootCanvas).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Entity player = level.getEntityWithId(SnakemeleonConstants.playerTypeId);
            if (player == null) {
                System.out.println("You need to make an entity with id set to player!");
                System.exit(1);
            }

            Vector2 offset = VIEWPORT.scale(.5f).sub(camera.getB());

            rootCanvas.setColor(Color.BLACK);
            rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);

            rootCanvas.drawImage(level.getBakedBackground(), (int) offset.x, (int) offset.y, (int) level.getDim().x,
                    (int) level.getDim().y, null);

            for (int i = level.getLayers().size() - 1; i >= 0; i--) {
                for (Entity e : level.getLayers().get(i)) {
                    if (e.isVisible() && e.isInView())
                        e.draw(rootCanvas, offset);
                }
            }

            /*
             * for (Entity wall : level.getEntitiesWithType(ReservedTypes.WALL)) {
             * rootCanvas.setColor(Color.RED); rootCanvas.fillRect((int)
             * (wall.getPos().x + offset.x), (int) (wall.getPos().y + offset.y),
             * wall .getDim().getWidth(), wall.getDim().getHeight());
             * 
             * }
             */

        } catch (final Exception e) {
            return false;
        }
        return true;
    }
}
