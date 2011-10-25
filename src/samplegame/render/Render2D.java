package samplegame.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class Render2D {
	public static void setup2D(final Vector2f BOUNDS) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, BOUNDS.x, BOUNDS.y, 0, 0, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void clearScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	public static void drawPoint(final Vector2f pos) {
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex2f(pos.x, pos.y);
		GL11.glEnd();
	}

	public static void drawLine(final Vector2f a, final Vector2f b) {
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(a.x, a.y);
		GL11.glVertex2f(b.x, b.y);
		GL11.glEnd();
	}

	public static void setColor(int r, int g, int b) {
		GL11.glColor3f(r, g, b);
	}

	public static void setBackgroundColor(int r, int g, int b) {
		GL11.glClearColor(r, g, b, 0);
	}

	public static void drawRect(final Vector2f a, final Vector2f b) {
		float x1 = a.x, x2 = b.x, y1 = a.y, y2 = b.y;
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y1);
		GL11.glVertex2f(x2, y2);
		GL11.glVertex2f(x1, y2);
		GL11.glEnd();
	}

}
