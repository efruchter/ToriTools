package samplegame.entrypoint;

import java.io.FileNotFoundException;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

import samplegame.render.Render;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 * 
 */
public class SampleGame {

    /** Game title */
    public static final String GAME_TITLE = "SampleGame";

    /** Exit the game */
    private static boolean finished;

    /** Desired frame time */
    private static final int FRAMERATE = 60;

    public static final Vector2f BOUNDS = new Vector2f(800, 600);

    /**
     * Application init
     * 
     * @param args
     *            Commandline args
     */
    public static void main(String[] args) {
        try {
            init();
            run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
        } finally {
            cleanup();
        }
        System.exit(0);
    }

    /**
     * Initialize the game
     * 
     * @throws Exception
     *             if init fails
     */
    private static void init() throws Exception {
        /**
         * Display windows stuff.
         */
        Display.setTitle(GAME_TITLE);
        Display.setVSyncEnabled(true);
        Display.setDisplayMode(new DisplayMode((int) BOUNDS.getX(),
                (int) BOUNDS.getY()));
        Display.create();
    }

    /**
     * Runs the game (the "main loop")
     */
    private static void run() {
        while (!finished) {
            // Always call Window.update(), all the time - it does some behind
            // the
            // scenes work, and also displays the rendered output
            Display.update();
            // Check for close requests
            if (Display.isCloseRequested()) {
                finished = true;
            }
            // The window is in the foreground, so we should play the game
            else if (Display.isActive()) {
                logic();
                render();
                Display.sync(FRAMERATE);
            }
            // The window is not in the foreground, so we can allow other stuff
            // to run and
            // infrequently update
            else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                logic();
                // Only bother rendering if the window is visible or dirty
                if (Display.isVisible() || Display.isDirty()) {
                    render();
                }
            }
        }
    }

    /**
     * Do any game-specific cleanup
     */
    private static void cleanup() {
        // Close the window
        Display.destroy();
    }

    /**
     * Do all calculations, handle input, etc.
     */
    private static void logic() {
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            finished = true;
        }
    }

    /**
     * Render the current frame
     */
    private static void render() {

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(45, BOUNDS.x / BOUNDS.y, 0, 10000);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // clear the screen and set the camera
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glPushMatrix();
        GL11.glTranslatef(-BOUNDS.x / 2, -BOUNDS.y / 2, -725);

        Render.setColor(Color.BLACK, .6f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(0, 0, 0);
        GL11.glVertex3f(BOUNDS.getX(), 0, 0);
        GL11.glVertex3f(BOUNDS.getX(), BOUNDS.getY(), 0);
        GL11.glVertex3f(0, BOUNDS.getY(), 0);
        GL11.glEnd();

        /**
         * DRAW ALL THE THINGS
         */
        Render.setColor(Color.GREEN);
        Render.fillRect(new Vector2f(20, 20), new Vector2f(20, 20));

        GL11.glPopMatrix();
    }

}
