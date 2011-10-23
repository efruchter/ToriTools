package samplegame.render;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.ReadableColor;

/**
 * Draw some stuff to the screen with the <i>power of OpenGL</i>.
 * 
 * @author toriscope
 * 
 */
public class Render {

    /**
     * Set the GL Color and alpha
     */
    public static void setColor(final Color c, float alpha) {
        GL11.glColor4f(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    /**
     * Set the GL Color. Alpha defaults to 1.
     */
    public static void setColor(final Color c) {
        GL11.glColor4f(c.getRed(), c.getGreen(), c.getBlue(), 1f);
    }

    public static void setColor(final ReadableColor c) {
        GL11.glColor4f(c.getRed(), c.getGreen(), c.getBlue(), 1f);
    }

    public static void setColor(final ReadableColor c, final float alpha) {
        GL11.glColor4f(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public static void setFill(final boolean fill) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, fill ? GL11.GL_FILL
                : GL11.GL_LINE);
    }
}
