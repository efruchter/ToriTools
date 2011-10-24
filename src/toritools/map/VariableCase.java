package toritools.map;

import java.util.HashMap;

public class VariableCase {
	protected HashMap<String, String> variables = new HashMap<String, String>();

	public VariableCase() {

	}

	public VariableCase(final HashMap<String, String> variables) {
		this.variables = variables;
	}

	public void setVar(final String var, final String value) {
		variables.put(var, value);
	}

	/**
	 * Return the variable if it exists, or null.
	 * 
	 * @param var
	 *            the key to search.
	 * @return the var or null.
	 */
	public String getVar(final String var) {
		return variables.get(var);
	}

	public HashMap<String, String> getVariables() {
		return variables;
	}

	public void setVariables(HashMap<String, String> variables) {
		this.variables = variables;
	}
}
