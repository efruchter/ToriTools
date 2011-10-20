package toritools.entities;

import java.util.HashMap;

public class VariableCase {
	protected HashMap<String, String> variables = new HashMap<String, String>();

	public void setVar(final String var, final String value) {
		variables.put(var, value);
	}

	public String getVar(final String var) {
		if (variables.containsKey(var))
			return variables.get(var);
		else
			return "";
	}
}
