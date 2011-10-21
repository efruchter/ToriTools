package samplegame.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.python.core.PyObject;
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
	 * 
	 * @throws FileNotFoundException
	 */
	public ScriptFactory() throws FileNotFoundException {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.compile(new FileReader(new File("Test.py")));
		interpreter.exec("from Script import Script");
		scriptEntity = interpreter.get("Script");
	}

	/**
	 * The create method is responsible for performing the actual coercion of
	 * the referenced python module into Java bytecode
	 */
	public EntityScript create() {
		PyObject buildingObject = scriptEntity.__call__();
		return (EntityScript) buildingObject.__tojava__(EntityScript.class);
	}

}
