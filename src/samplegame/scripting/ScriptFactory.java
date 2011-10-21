package samplegame.scripting;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

/**
 * The Jython importer for EntityScripts
 * 
 * @author toriscope
 * 
 */
public class ScriptFactory {
	private PyObject scriptEntity;

	/**
	 * Create a new PythonInterpreter object, then use it to execute some python
	 * code. In this case, we want to import the python module that we will
	 * coerce.
	 * 
	 * Once the module is imported than we obtain a reference to it and assign
	 * the reference to a Java variable
	 */
	public ScriptFactory() {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("from EntityScript import EntityScript");
		scriptEntity = interpreter.get("Script");
	}

	/**
	 * The create method is responsible for performing the actual coercion of
	 * the referenced python module into Java bytecode
	 */
	public EntityScript create(String name, String location, String id) {
		PyObject buildingObject = scriptEntity.__call__(new PyString(name),
				new PyString(location), new PyString(id));
		return (EntityScript) buildingObject.__tojava__(EntityScript.class);
	}

}
