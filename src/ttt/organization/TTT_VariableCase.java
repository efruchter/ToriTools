package ttt.organization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import toritools.math.Vector2;
import ttt.TTT_Constants;
import ttt.io.XMLSerializeable;

/**
 * Can hold and save/load: String, float, XMLSerializeable
 * 
 * @author toriscope
 * 
 */
public class TTT_VariableCase implements XMLSerializeable {
	private final HashMap<String, Object> variables;

	public TTT_VariableCase() {
		variables = new HashMap<String, Object>();
		store(TTT_Constants.ID_KEY, "DEFAULT");
	}

	public void store(final String key, final String value) {
		variables.put(key, value);
	}

	public void store(final String key, final float value) {
		variables.put(key, value);
	}

	public void store(final String key, final Vector2 value) {
		variables.put(key, value);
	}

	public Object load(final String key) {
		return variables.get(key);
	}

	public String loadString(final String key) {
		return (String) variables.get(key);
	}

	public float loadFloat(final String key) {
		return (Float) variables.get(key);
	}

	public Vector2 loadVector(final String key) {
		return (Vector2) variables.get(key);
	}

	public boolean has(final String s) {
		return variables.containsKey(s);
	}

	@Override
	public String toString() {
		StringBuffer bu = new StringBuffer();
		bu.append("VARIABLES [\n");
		for (Entry<String, Object> s : this.variables.entrySet()) {
			bu.append("\t").append(s.getKey()).append(": ")
					.append(s.getValue().toString()).append("\n");
		}
		return bu.append("]").toString();
	}

	@Override
	public Element writeToElement() {
		Element variables = new Element(getElementName());
		ArrayList<Entry<String, Object>> li = new ArrayList<Entry<String, Object>>(
				this.variables.entrySet());
		Collections.sort(li, new Comparator<Entry<String, Object>>() {
			@Override
			public int compare(Entry<String, Object> arg0,
					Entry<String, Object> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		});
		for (Entry<String, Object> s : li) {
			if (s.getValue() instanceof String || s.getValue() instanceof Float) {
				Element var = new Element(s.getKey());
				var.addAttribute(new Attribute("type",
						(s.getValue() instanceof Float ? "Float" : "String")));
				var.appendChild(s.getValue().toString());
				variables.appendChild(var);
			} else if (s.getValue() instanceof Vector2) {
				Element v = Vector2.writeToElement((Vector2) s.getValue());
				v.setLocalName(s.getKey());
				v.addAttribute(new Attribute("type", "Vector2"));
				variables.appendChild(v);
			}
		}
		return variables;
	}

	@Override
	public void assembleFromElement(Element entity) {
		variables.clear();
		int childCount = entity.getChildCount();
		Elements children = entity.getChildElements();
		for (int i = 0; i < childCount; i++) {
			Element variable = children.get(i);
			String type = variable.getAttribute("type").getValue();
			Object obj = null;
			if (type.equals("Vector2")) {
				obj = Vector2.assembleFromElement(variable);
			} else if (type.equals("Float")) {
				obj = Float.parseFloat(variable.getValue());
			} else {
				obj = variable.getValue();
			}
			variables.put(variable.getLocalName(), obj);
		}
	}

	@Override
	public String getElementName() {
		return "variables";
	}
}
