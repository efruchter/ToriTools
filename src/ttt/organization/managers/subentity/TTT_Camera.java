package ttt.organization.managers.subentity;

import nu.xom.Element;

import org.lwjgl.opengl.Display;

import toritools.math.Vector2;
import ttt.gl.RenderGL;
import ttt.io.XMLSerializeable;

public class TTT_Camera implements XMLSerializeable {
	public Vector2 pos = new Vector2();

	public void translateGL() {
		RenderGL.translate(-pos.x + Display.getWidth() / 2,
				-pos.y + Display.getHeight() / 2);
	}

	@Override
	public Element writeToElement() {
		Element ele = new Element(getElementName());
		ele.appendChild(Vector2.writeToElement(pos, "pos"));
		return ele;
	}

	@Override
	public void assembleFromElement(Element entity) {
		pos = Vector2
				.assembleFromElement(entity.getChildElements("pos").get(0));
	}

	@Override
	public String getElementName() {
		return "camera";
	}
}
