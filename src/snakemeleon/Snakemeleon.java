package snakemeleon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import snakemeleon.types.ChameleonFootSensor;
import snakemeleon.types.ChameleonScript;
import snakemeleon.types.ChameleonStickyScript;
import snakemeleon.types.Collectable;
import snakemeleon.types.KeyTriggerEntityAction;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.entrypoint.Binary;
import toritools.io.FontLoader;
import toritools.io.Importer;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.physics.Universe;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Snakemeleon extends Binary implements ContactListener {

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
    private static String[] levels = new String[] { "snakemeleon/level1.xml", "snakemeleon/level2.xml",
            "snakemeleon/level3.xml", "snakemeleon/level4.xml"};

    private static Font uiFont;

    private static SnakemeleonHUD hud = new SnakemeleonHUD();

    private static File bgFile;

    public static void main(String[] args) {
        new Snakemeleon();
    }

    public Snakemeleon() {
        super(new Vector2(800, 600), 60, "Snakemeleon");
        super.getApplicationFrame().setIconImage(ScriptUtils.fetchImage(new File("snakemeleon/chameleon_head.png")));
    }

    @Override
    protected void initialize() {

        try {
            FontLoader.loadFont(new File("snakemeleon/eartm.ttf"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        uiFont = new Font("Earth's Mightiest", Font.TRUETYPE_FONT, 40);

        bgFile = new File("snakemeleon/forest1.png");

        // Player player = new Player();
        // player.setSourceLocation("snakemeleon/sounds/BGM/Wallpaper.mp3");
        // player.play();

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB), new Point(), "Red Circle Cursor");
        // Set the blank cursor to the JFrame.
        super.getApplicationFrame().getContentPane().setCursor(blankCursor);

        super.getApplicationFrame().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                Snakemeleon.isMouseDragging = true;

            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
                Snakemeleon.isMouseDragging = false;

            }
        });
    }

    @Override
    protected void globalLogic(Level level) {

        if (ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }

        if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_F12)) {
            nextLevel();
        }

        if (ScriptUtils.getKeyHolder().isPressedThenRelease(KeyEvent.VK_P)) {
            Debug.showDebugPrintouts = !Debug.showDebugPrintouts;
        }

        uni.step(60 / 1000f);

        // Camera step
        // if (isMouseDragging) {
        // camera.setA(mousePos);
        // camera.smoothTowardA();
        // } else {
        camera.setA(level.getEntityWithId(SnakemeleonConstants.playerTypeId).getPos());
        camera.smoothTowardA();
        // }

        Vector2 halfPort = VIEWPORT.scale(.5f);

        float x = Math.min(Math.max(camera.getB().x, halfPort.x), level.getDim().x - halfPort.x);
        float y = Math.min(Math.max(camera.getB().y, halfPort.y), level.getDim().y - halfPort.y);
        offset = halfPort.sub(new Vector2(x, y));

        PointerInfo e = MouseInfo.getPointerInfo();
        Point frameLoc = super.getApplicationFrame().getLocationOnScreen();
        mousePos = new Vector2(-offset.getWidth() + e.getLocation().x - frameLoc.x, -offset.getHeight()
                + e.getLocation().y - frameLoc.y);

        hud.update(1, level);
    }

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {

        touchQueue.clear();
        jumpTouchQueue = 0;

        uni = new Universe(SnakemeleonConstants.gravity);
        uni.setContactListener(this);

        Entity cham = levelBeingLoaded.getEntityWithId(SnakemeleonConstants.playerTypeId);
        cham.addScript(new ChameleonScript());
        Body chamBody = uni.addEntity(cham, BodyType.DYNAMIC, true, true, 1f, .3f);
        chamBody.setAngularDamping(5);
        // A script to enable the chameleon to stick to things
        cham.addScript(new ChameleonStickyScript());

        for (Entity e : levelBeingLoaded.getEntitiesWithType(ReservedTypes.WALL)) {
            uni.addEntity(e, BodyType.STATIC, false, false, 1f, .3f);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType("RIGHT_DIAG_WALL")) {
            uni.addEntity(e, BodyType.STATIC, false, false, 1f, .3f,
                    new Vector2[] { (e.getDim().scale(.5f, -.5f)), (e.getDim().scale(-.5f, .5f)) }, false);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType("LEFT_DIAG_WALL")) {
            uni.addEntity(e, BodyType.STATIC, false, false, 1f, .3f,
                    new Vector2[] { (e.getDim().scale(-.5f, -.5f)), (e.getDim().scale(.5f, .5f)) }, false);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType(SnakemeleonConstants.dynamicPropType)) {
            String isRound = e.getVariableCase().getVar("round");
            boolean round = false;
            if (isRound != null && isRound.equalsIgnoreCase("true")) {
                round = true;
            }
            uni.addEntity(e, BodyType.DYNAMIC, true, round, 1f, .3f);
            if (e.getVariableCase().getVar("key") != null) {
                e.addScript(new KeyTriggerEntityAction());
            }

            if (e.getVariableCase().getVar("id") != null && e.getVariableCase().getVar("id").equals("collectable")) {
                e.addScript(new Collectable());
            }
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

        EntityScript spikeScript = new EntityScript() {

            Entity player;

            @Override
            public void onSpawn(Entity self, Level level) {
                player = level.getEntityWithId("player");
            }

            @Override
            public void onUpdate(Entity self, float time, Level level) {
                if (player.isActive() && ScriptUtils.isColliding(self, player)) {
                    player.setActive(false);
                }
            }

            @Override
            public void onDeath(Entity self, Level level, boolean isRoomExit) {

            }

        };

        for (Entity e : levelBeingLoaded.getEntitiesWithType("spike")) {
            e.addScript(spikeScript);
        }

        for (Entity e : levelBeingLoaded.getEntitiesWithType("message")) {
            final String message = e.getVariableCase().getVar("message");
            e.setSprite(new AbstractSpriteAdapter() {
                @Override
                public void draw(Graphics2D g, Entity self) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawString(message, self.getPos().getWidth(), self.getPos().getHeight());
                }
            });
        }

        levelBeingLoaded.bakeBackground();

        camera = new MidpointChain(levelBeingLoaded.getEntityWithId(SnakemeleonConstants.playerTypeId).getPos(),
                SnakemeleonConstants.cameraLag);
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

    private final BasicStroke dottedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
            new float[] { 5, 5, 5, 5 }, 0);

    @Override
    protected boolean render(Graphics2D rootCanvas, Level level) {
        try {
            rootCanvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            rootCanvas.setFont(uiFont);

            Entity player = level.getEntityWithId(SnakemeleonConstants.playerTypeId);
            if (player == null) {
                System.out.println("You need to make an entity with id set to player!");
                System.exit(1);
            }

            rootCanvas.setColor(Color.LIGHT_GRAY);
            rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);

            rootCanvas.translate(offset.getWidth(), offset.getHeight());

            rootCanvas.drawImage(ScriptUtils.fetchImage(bgFile), (int) 0, (int) 0, level.getDim().getWidth(), level
                    .getDim().getHeight(), null);

            rootCanvas.drawImage(level.getBakedBackground(), (int) 0, (int) 0, (int) level.getDim().x,
                    (int) level.getDim().y, null);

            if (ChameleonStickyScript.isGrabbing) {
                rootCanvas.setColor(Color.GREEN);
                rootCanvas.setStroke(dottedStroke);
                rootCanvas.drawOval(player.getPos().getWidth(), player.getPos().getHeight(),
                        player.getDim().getWidth(), player.getDim().getHeight());
                // Entity other = ChameleonStickyScript.grabbingEntity;
                // rootCanvas.drawOval(other.getPos().getWidth(),
                // other.getPos().getHeight(),
                // other.getDim().getWidth(), other.getDim().getHeight());

            }

            for (int i = level.getLayers().size() - 1; i >= 0; i--) {
                for (Entity e : level.getLayers().get(i)) {
                    if (e.isVisible() && e.isInView())
                        e.draw(rootCanvas);
                }
            }

            if (Debug.showDebugPrintouts)
                for (Entity wall : level.getEntitiesWithType("WALL"))
                    wall.draw(rootCanvas);

            rootCanvas.translate(-offset.getWidth(), -offset.getHeight());

            hud.draw(rootCanvas, VIEWPORT);

        } catch (final Exception e) {
            return false;
        }
        return true;
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
     * Shape contact methods. You can't manipulate the universe from within
     * these. They are only here for temporary testing purposes.
     */

    public static List<Entity> touchQueue = new LinkedList<Entity>();
    public static int jumpTouchQueue = 0;

    @Override
    public void beginContact(Contact c) {

        Entity a = (Entity) c.m_fixtureA.m_userData, b = (Entity) c.m_fixtureB.m_userData;
        boolean playerisA = ((Entity) c.m_fixtureA.m_userData).getType().equals("player");
        boolean playerisB = ((Entity) c.m_fixtureB.m_userData).getType().equals("player");

        if (playerisA && b.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.add(b);
        } else if (playerisB && a.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.add(a);
        }

        if (c.m_fixtureA.m_userData instanceof ChameleonFootSensor && !playerisB) {
            jumpTouchQueue++;
        } else if (c.m_fixtureB.m_userData instanceof ChameleonFootSensor && !playerisA) {
            jumpTouchQueue++;
        }

    }

    @Override
    public void endContact(Contact c) {

        Entity a = (Entity) c.m_fixtureA.m_userData, b = (Entity) c.m_fixtureB.m_userData;
        boolean playerisA = ((Entity) c.m_fixtureA.m_userData).getType().equals("player");
        boolean playerisB = ((Entity) c.m_fixtureB.m_userData).getType().equals("player");

        if (playerisA && b.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.remove(b);
        } else if (playerisB && a.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.remove(a);
        }

        if (c.m_fixtureA.m_userData instanceof ChameleonFootSensor && !playerisB) {
            jumpTouchQueue--;
        } else if (c.m_fixtureB.m_userData instanceof ChameleonFootSensor && !playerisA) {
            jumpTouchQueue--;
        }
    }

    @Override
    public void postSolve(Contact arg0, ContactImpulse arg1) {

    }

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {

    }
}
