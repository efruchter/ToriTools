package samplegame.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class Render2D {
    /**
     * Push a matrix Triangle fan in the shape of a circle. Does not set color
     * or fill style.
     */
    public static void fillCircle(final Vector2f center, final float radius) {

        final float FULL_CIRCLE = (float) Math.PI * 2, SEGMENTS = 20;
        GL11.glPushMatrix();
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        for (float angle = 0; angle < FULL_CIRCLE; angle += FULL_CIRCLE
                / SEGMENTS) {
            Vector2f newPoint = Vector2f.add(center,
                    new Vector2f(radius * (float) Math.cos(angle), radius
                            * (float) Math.sin(angle)), null);
            GL11.glVertex2f(newPoint.getX(), newPoint.getY());
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    /**
     * Push a matrix polygon.
     */
    public static void drawPoly(final Vector2f... points) {
        GL11.glPushMatrix();
        GL11.glBegin(GL11.GL_LINES);
        for (Vector2f newPoint : points)
            GL11.glVertex2f(newPoint.getX(), newPoint.getY());
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    /**
     * Push a matrix polygon.
     */
    public static void fillPoly(final Vector2f... points) {
        GL11.glPushMatrix();
        GL11.glBegin(GL11.GL_QUADS);
        for (Vector2f newPoint : points)
            GL11.glVertex2f(newPoint.getX(), newPoint.getY());
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void fillRect(final Vector2f pos, final Vector2f dim) {

        fillPoly(pos, new Vector2f(pos.x + dim.x, pos.y),
                Vector2f.add(pos, dim, null),
                new Vector2f(pos.x, pos.y + dim.y), pos);
    }
}
