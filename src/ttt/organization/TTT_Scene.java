package ttt.organization;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import nu.xom.Element;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import ttt.io.XMLSerializeable;
import ttt.organization.managers.TTT_EntityManager;
import ttt.organization.managers.subentity.TTT_Camera;

public class TTT_Scene implements XMLSerializeable {

	public final TTT_VariableCase variables;
	public final TTT_EntityManager entities;
	public final TTT_Camera camera;

	public TTT_Scene() {
		variables = new TTT_VariableCase();
		entities = new TTT_EntityManager();
		camera = new TTT_Camera();
	}

	@Override
	public Element writeToElement() {
		Element e = new Element(getElementName());
		e.appendChild(variables.writeToElement());
		e.appendChild(entities.writeToElement());
		e.appendChild(camera.writeToElement());
		return e;
	}

	@Override
	public void assembleFromElement(Element entity) {
		variables.assembleFromElement(entity.getChildElements(
				variables.getElementName()).get(0));
		entities.assembleFromElement(entity.getChildElements(
				entities.getElementName()).get(0));
		camera.assembleFromElement(entity.getChildElements(
				camera.getElementName()).get(0));
	}

	@Override
	public String getElementName() {
		return "scene";
	}

	public void onSpawn() {
		for (TTT_Entity e : entities.getAllEntitiesFast()) {
			e.scripts.onSpawn(e, this);
		}
	}

	public void onUpdate(long milliDelay) {
		for (TTT_Entity e : entities.getAllEntitiesFast()) {
			e.scripts.onUpdate(e, this, milliDelay);
		}
	}

	public void onDeath() {
		for (TTT_Entity e : entities.getAllEntitiesFast()) {
			e.scripts.onDeath(e, this, true);
		}
	}

	public String toString() {
		return getElementName();
	}

	public void draw() {
		glMatrixMode(GL_PROJECTION);

		glLoadIdentity();

		GLU.gluOrtho2D(0, Display.getWidth(), 0, Display.getHeight());

		glMatrixMode(GL_MODELVIEW);

		// clear the screen
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glPushMatrix();
		{

			camera.translateGL();

			for (TTT_Entity e : entities.getAllEntitiesFast()) {
				e.view.draw(e);
			}

		}
		glPopMatrix();

	}
}
