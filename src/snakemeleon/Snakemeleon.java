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

import org.jbox2d.dynamics.BodyType;

import snakemeleon.types.ChameleonScript;
import snakemeleon.types.ChameleonStickyScript;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.ReservedTypes;
import toritools.entrypoint.Binary;
import toritools.io.FontLoader;
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
    private static Vector2 offset = Vector2.ZERO;

    public static boolean isMouseDragging = false;
    public static Vector2 mousePos = Vector2.ZERO;

    private static int currentLevel = 0;
    private static String[] levels = new String[] { "snakemeleon/TestLevel.xml" };

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

        Debug.showDebugPrintouts = true;

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
    }

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {

        uni = new Universe(SnakemeleonConstants.gravity);

        camera = new MidpointChain(levelBeingLoaded.getEntityWithId(SnakemeleonConstants.playerTypeId).getPos(),
                SnakemeleonConstants.cameraLag);

        Entity cham = levelBeingLoaded.getEntityWithId(SnakemeleonConstants.playerTypeId);
        cham.addScript(new ChameleonScript());
        uni.addEntity(cham, BodyType.DYNAMIC, true, true, 1f, .3f).setAngularDamping(5);
        // A script to enable the chameleon to stick to things
        ChameleonStickyScript s = new ChameleonStickyScript();
        cham.addScript(s);
        uni.setContactListener(s);

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

            for (int i = level.getLayers().size() - 1; i >= 0; i--) {
                for (Entity e : level.getLayers().get(i)) {
                    if (e.isVisible() && e.isInView())
                        e.draw(rootCanvas);
                }
            }

            rootCanvas.setStroke(new BasicStroke(3));
            rootCanvas.setColor(Color.RED);
            rootCanvas.drawOval(mousePos.getWidth() - 10, mousePos.getHeight() - 10, 20, 20);

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
}
