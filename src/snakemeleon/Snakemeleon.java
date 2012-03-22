package snakemeleon;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

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

public class Snakemeleon extends Binary implements MouseListener, MouseMotionListener, ContactListener {

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
    private static Vector2 offset = Vector2.ZERO;

    public static boolean isMouseDragging = false;
    public static Vector2 mousePos = Vector2.ZERO;

    private static int currentLevel = 0;
    private static String[] levels = new String[] { "snakemeleon/TestLevel.xml" };

    public static void main(String[] args) {
        new Snakemeleon();
    }

    public Snakemeleon() {
        super(new Vector2(800, 600), 60, "Snakemeleon");
        super.getApplicationFrame().setIconImage(new ImageIcon("snakemeleon/chameleon_head.png").getImage());

        /**
         * Set up a mouse tracker.
         */

    }

    @Override
    protected void initialize() {
        // Player player = new Player();
        // player.setSourceLocation("snakemeleon/sounds/BGM/Wallpaper.mp3");
        // player.play();

        // Set up cursor
        try {
            BufferedImage image = ImageIO.read(new File("snakemeleon/cursor.png"));
            // Create a new blank cursor.
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(image,
                    new Point(image.getWidth() / 3, 0), "Green Circle Cursor");

            // Set the blank cursor to the JFrame.
            super.getApplicationFrame().getContentPane().setCursor(blankCursor);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        super.getApplicationFrame().addMouseListener(this);
        super.getApplicationFrame().addMouseMotionListener(this);
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

        offset = VIEWPORT.scale(.5f).sub(camera.getB());

        // System.out.println(isMouseDragging + " " + mousePos);

        if (k == 1) {
            k++;
            if (ab.getType().equals("player"))
                uni.addWeld(ab, bb);
            else
                uni.addWeld(bb, ab);
        }
    }

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {

        uni = new Universe(SnakemeleonConstants.gravity);

        uni.setContactListener(this);

        camera = new MidpointChain(levelBeingLoaded.getEntityWithId(SnakemeleonConstants.playerTypeId).getPos(),
                SnakemeleonConstants.cameraLag);

        for (Entity en : levelBeingLoaded.getEntitiesWithType(SnakemeleonConstants.playerTypeId)) {
            en.addScript(new ChameleonScript());
            uni.addEntity(en, BodyType.DYNAMIC, true, true, 1f, .3f);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType(ReservedTypes.WALL)) {
            uni.addEntity(e, BodyType.STATIC, false, false, 1f, .3f);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType(SnakemeleonConstants.dynamicPropType)) {
            String isRound = e.getVariableCase().getVar("round");
            boolean round = false;
            if (isRound != null && isRound.equalsIgnoreCase("true")) {
                round = true;
            }
            uni.addEntity(e, BodyType.DYNAMIC, true, round, 1f, .3f);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType(SnakemeleonConstants.hingeType)) {
            levelBeingLoaded.despawnEntity(e);
            Entity a = null, b = null;
            Vector2 pos = e.getPos().add(e.getDim().scale(.5f));
            for (Entity object : levelBeingLoaded.getNewEntities()) {
                if (!object.getType().equals(ReservedTypes.BACKGROUND) && ScriptUtils.isPointWithin(object, pos)
                        && object != e) {
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
            return Importer.importLevel(new File(levels[0]));
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

            rootCanvas.setColor(Color.LIGHT_GRAY);
            rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);

            ((Graphics2D) rootCanvas).translate(offset.getWidth(), offset.getHeight());

            rootCanvas.drawImage(level.getBakedBackground(), (int) 0, (int) 0, (int) level.getDim().x,
                    (int) level.getDim().y, null);

            for (int i = level.getLayers().size() - 1; i >= 0; i--) {
                for (Entity e : level.getLayers().get(i)) {
                    if (e.isVisible() && e.isInView())
                        e.draw(rootCanvas);
                }
            }

            rootCanvas.setColor(Color.RED);
            rootCanvas.drawRect(player.getPos().getWidth(), player.getPos().getHeight(), player.getDim().getWidth(),
                    player.getDim().getHeight());

        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    /*
     * MOUSE CONTROL METHODS
     */

    @Override
    public void mouseDragged(MouseEvent e) {
        Snakemeleon.isMouseDragging = true;
        mousePos = new Vector2(-offset.getWidth() + e.getX(), -offset.getHeight() + e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDragged(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Snakemeleon.isMouseDragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public static void restartLevel() {
        try {
            ScriptUtils.queueLevelSwitch(Importer.importLevel(new File(levels[currentLevel])));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void nextLevel() {
        try {
            ScriptUtils.queueLevelSwitch(Importer.importLevel(new File(levels[++currentLevel])));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException winner) {
            System.out.println("You won!");
            System.exit(1);
        }
    }

    /*
     * Shape contact methods. You cant manipulate the universe from within
     * these. They are only here for temporary testing purposes.
     */

    @Override
    public void beginContact(Contact c) {

        Entity a = (Entity) c.m_fixtureA.m_userData, b = (Entity) c.m_fixtureB.m_userData;

        if (a.getType().equals("player") || b.getType().equals("player")) {
            if (k++ == 0) {
                System.out.println("WELDED");
                ab = a;
                bb = b;
            }
        }
    }

    int k = 0;

    @Override
    public void endContact(Contact c) {

    }

    @Override
    public void postSolve(Contact arg0, ContactImpulse arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {
        // TODO Auto-generated method stub

    }

    private Entity ab, bb;

}
