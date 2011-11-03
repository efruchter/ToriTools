package toritools.map;

import java.util.HashMap;

/**
 * A simple wrapper for a String,String hashmap to hold variables.
 * @author toriscope
 *
 */
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
	
	public void clear() {
		variables.clear();
	}
	
	public double getDouble(final String key){
		return Double.parseDouble(variables.get(key));
	}
	
	public double getInteger(final String key){
		return Integer.parseInt(variables.get(key));
	}
}
