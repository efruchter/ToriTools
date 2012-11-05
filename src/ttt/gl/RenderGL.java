package ttt.gl;

import org.lwjgl.opengl.GL11;

public class RenderGL {

	public static void push() {
		GL11.glPushMatrix();
	}

	public static void pop() {
		GL11.glPopMatrix();
	}

	public static void drawRect(int x, int y, int w, int h) {
		GL11.glBegin(GL11.GL_POLYGON);
		{
			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x + w, y);
			GL11.glVertex2f(x + w, y + h);
			GL11.glVertex2f(x, y + h);
		}
		GL11.glEnd();
	}

	public static void translate(float x, float y) {
		GL11.glTranslatef(x, y, 0);
	}
}
