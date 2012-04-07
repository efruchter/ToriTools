package toritools.entrypoint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.VolatileImage;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import toritools.debug.Debug;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

/**
 * This class is a good way to get started with ToriTools. Just extend it, and
 * fill in what you need. Instantiate the subclass, and you're done!
 * 
 * @author toriscope
 * 
 */
public abstract class Binary {

    // CORE VARS
    protected final float FRAMERATE;
    public static Vector2 VIEWPORT = Vector2.ZERO;

    private static JFrame frame;
    public static final GraphicsConfiguration gc;
    private ScheduledExecutorService timer;

    private boolean gameRunning = false;
    
    private final JPanel panel;

    private final File splash = new File("resources/toritools_splash.png");

    static {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hardware accel.
        if (System.getProperty("os.name").contains("Windows")) {
            System.setProperty("sun.java2d.d3d", "True");
        } else {
            System.setProperty("sun.java2d.opengl", "True");
            System.setProperty("sun.java2d.ddscale", "true");
            System.setProperty("sun.java2d.translaccel", "true");
            System.setProperty("sun.java2d.ddforcevram", "true");
        }

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gc = frame.getGraphicsConfiguration();
    }

    /*
     * SUBCLASS
     */

    /**
     * Load anything you need (besides entities), be it large background images
     * or fonts. This is your time to prepare for the update logic which will
     * begin ticking after this method is run.
     */
    protected abstract void initialize();

    /**
     * The global logic loop. Poll controls here if you want, check for win
     * condition, etc. Entity updating should not be done here. It is a good
     * idea to package control polling for most entities with their script,
     * rather than here. This is the place for global menus, state changing,
     * etc. The level will update after this method is run, followed by a
     * graphical repaint. Keys queued for release are also released after this.
     */
    protected abstract void globalLogic(final Level level);

    /**
     * Configure the current level to be loaded. Set up your special entity
     * types and scripts here. Spawning will be done elsewhere.
     * 
     * @param levelBeingLoaded
     */
    protected abstract void setupCurrentLevel(Level levelBeingLoaded);

    /**
     * Get the starting level. Feel free to spawn a blank one if you don't want
     * to do this.
     * 
     * @return a level.
     */
    protected abstract Level getStartingLevel();

    /**
     * Render your game.
     * 
     * @param rootCanvas
     *            the panel's drawing surface.
     * @return true if drawing was successful, false otherwise.
     */
    protected abstract boolean render(final Graphics2D rootCanvas, final Level level);

    /**
     * Some basic settings.
     * 
     * @param VIEWPORT_SIZE
     *            the dimensions of the viewport/window.
     * @param frameRate
     *            the frame-rate as a ratio. 60FPS would be 60, for example.
     */
    @SuppressWarnings("serial")
    public Binary(final Vector2 VIEWPORT_SIZE, final int frameRate, final String windowTitle) {

        frame.setTitle(windowTitle);

        this.FRAMERATE = 1000 / frameRate;
        VIEWPORT = VIEWPORT_SIZE;

        panel = new JPanel() {
            public void paintComponent(final Graphics g) {
                // super.paintComponent(g);
                renderAll(g);
            }
        };
        frame.add(panel, BorderLayout.CENTER);
        frame.addKeyListener(ScriptUtils.getKeyHolder());
        frame.setFocusable(true);
        panel.setPreferredSize(new Dimension((int) VIEWPORT.x, (int) VIEWPORT.y));

        initialize();

        frame.pack();
        frame.setVisible(true);

        ScriptUtils.queueLevelSwitch(getStartingLevel());

        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(new Thread() {
            public void run() {
                try {
                    coreLogic();
                    panel.repaint();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    System.exit(1);
                }
            }
        }, 0, (int) FRAMERATE, TimeUnit.MILLISECONDS);
    }

    private void rebuildBuffers() {
        b1 = gc.createCompatibleVolatileImage((int) VIEWPORT.x, (int) VIEWPORT.y);
        b2 = gc.createCompatibleVolatileImage((int) VIEWPORT.x, (int) VIEWPORT.y);
    }

    private void coreLogic() {
        if (ScriptUtils.isLevelQueued()) {
            if (ScriptUtils.getCurrentLevel() != null) {
                Debug.print("Closing level.");
                ScriptUtils.getCurrentLevel().onDeath(true);
            }
            ScriptUtils.moveToQueuedLevel();
            setupCurrentLevel(ScriptUtils.getCurrentLevel());
            Debug.print("Spawning entities.");
            ScriptUtils.getCurrentLevel().onSpawn(null);
        } else {
            globalLogic(ScriptUtils.getCurrentLevel());
            ScriptUtils.getCurrentLevel().onUpdate((float) FRAMERATE);
            ScriptUtils.getKeyHolder().freeQueuedKeys();
        }
    }

    VolatileImage b1, b2;
    boolean buffer1 = true;

    private void renderAll(final Graphics finalCanvas) {

        if (b1 == null || b2 == null || b1.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE
                || b2.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
            rebuildBuffers();
        }

        Image drawSurface = (buffer1) ? b1 : b2;
        Image renderSurface = (buffer1) ? b2 : b1;

        finalCanvas.drawImage(renderSurface, 0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y, null);

        if (render((Graphics2D) drawSurface.getGraphics(), ScriptUtils.getCurrentLevel())) {
            buffer1 = !buffer1;
            gameRunning = true;
        } else if (!gameRunning) {
            finalCanvas
                    .drawImage(ScriptUtils.fetchImage(splash), 0, 0, VIEWPORT.getWidth(), VIEWPORT.getHeight(), null);
        }
    }

    /**
     * Get the core application frame.
     * 
     * @return the JFrame the whole thing is running in.
     */
    protected JFrame getApplicationFrame() {
        return frame;
    }
    
    /**
     * Get the core application panel.
     * 
     * @return the JFrame the whole thing is running in.
     */
    protected JPanel getApplicationPanel() {
        return panel;
    }
}
