package ttt.organization.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import nu.xom.Element;
import nu.xom.Elements;
import ttt.TTT_Constants;
import ttt.io.XMLSerializeable;

public class TTT_TypeManager implements XMLSerializeable {

	private final HashMap<String, Void> types;

	public TTT_TypeManager() {
		types = new HashMap<String, Void>();
	}

	public void addType(final String... newTypes) {
		for (String t : newTypes) {
			types.put(t, null);
		}
	}

	public void removeType(final String type) {
		types.remove(type);
	}

	@Override
	public Element writeToElement() {
		Element variables = new Element(getElementName());
		for (Entry<String, Void> type : types.entrySet()) {
			Element tt = new Element(TTT_Constants.TYPE_KEY);
			tt.appendChild(type.getKey());
			variables.appendChild(tt);
		}
		return variables;
	}

	@Override
	public void assembleFromElement(Element entity) {
		types.clear();
		Elements ele = entity.getChildElements();
		for (int i = 0; i < ele.size(); i++) {
			types.put(ele.get(i).getValue(), null);
		}
	}

	@Override
	public String getElementName() {
		return "types";
	}

	public boolean isType(final String type) {
		return types.containsKey(type);
	}

	public List<String> getTypeList() {
		List<String> t = new LinkedList<String>();
		for (Entry<String, Void> s : types.entrySet()) {
			t.add(s.getKey());
		}
		return t;
	}
}
