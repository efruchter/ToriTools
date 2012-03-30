package toritools.map;

import java.util.HashMap;

/**
 * A simple wrapper for a String,String hashmap to hold variables.
 * 
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
     * Unlike the more specific getters, this wont crash the game on fail, it
     * will only return null.
     */
    public String getVar(final String key) {
        return variables.get(key);
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

    /*
     * The following methods will crash/error report on failure to fetch/parse.
     */

    public float getFloat(final String key) {
        try {
            return Float.parseFloat(variables.get(key));
        } catch (final NumberFormatException e) {
            throw new RuntimeException("The variable with key " + key + " is not a valid float.");
        } catch (final Exception e) {
            throw new RuntimeException("No variable with key " + key + " found!");
        }
    }

    public float getFloatOrDefault(final String key, final float defaultFloat) {
        String f = variables.get(key);
        if (f == null) {
            return defaultFloat;
        } else {
            try {
                return Float.parseFloat(f);
            } catch (final NumberFormatException e) {
                return defaultFloat;
            }
        }
    }

    public double getInteger(final String key) {
        try {
            return Integer.parseInt(variables.get(key));
        } catch (final NumberFormatException e) {
            System.err.println("The variable with key " + key + " is not a valid integer.");
        } catch (final Exception e) {
            System.err.println("No variable with key " + key + "found!");
        }
        System.exit(1);
        return 0;
    }

    public String getString(final String key) {
        String s = variables.get(key);
        if (s == null) {
            System.err.println("No variable with key " + key + "found!");
            System.exit(1);
        }
        return s;
    }
}
