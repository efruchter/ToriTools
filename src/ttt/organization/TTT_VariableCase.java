package ttt.organization;

import java.util.HashMap;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import ttt.io.XMLSerializeable;

public class TTT_VariableCase implements XMLSerializeable {
	private final HashMap<String, Object> variables;

	public TTT_VariableCase() {
		variables = new HashMap<String, Object>();
	}

	public void clear() {
		variables.clear();
	}

	public void clear(final String key) {
		variables.remove(key);
	}

	public void set(final String key, final Object value) {
		variables.put(key, value);
	}

	public Object get(final String key) {
		return variables.get(key);
	}

	public void setString(final String key, final String value) {
		variables.put(key, value);
	}

	public void setFloat(final String key, final Float value) {
		variables.put(key, value);
	}

	public String getString(final String key) throws ClassCastException {
		return (String) variables.get(key);
	}

	public Float getFloat(final String key) throws ClassCastException {
		return (Float) variables.get(key);
	}

	@Override
	public Element writeToElement() {
		Element variables = new Element(getElementName());
		for (Entry<String, Object> s : this.variables.entrySet()) {
			if (!(s.getValue() instanceof String)
					&& !(s.getValue() instanceof Float)) {
				continue;
			}
			Element var = new Element(s.getKey());
			var.addAttribute(new Attribute("type",
					(s.getValue() instanceof Float ? "Float" : "String")));
			var.appendChild(s.getValue().toString());
			variables.appendChild(var);
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
			variables
					.put(variable.getLocalName(),
							type.equals("Float") ? Float.parseFloat(variable
									.getValue()) : variable.getValue());
		}
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
	public String getElementName() {
		return "variables";
	}
}
