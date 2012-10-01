package ttt.io;

import org.python.util.PythonInterpreter;

public class JythonFactory {

    @SuppressWarnings("rawtypes")
    public static Object getJythonObject(String interfaceName, String pathToJythonModule) throws ClassNotFoundException {

        Object javaInt = null;
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(pathToJythonModule);
        String tempName = pathToJythonModule.substring(pathToJythonModule.lastIndexOf("/") + 1);
        tempName = tempName.substring(0, tempName.indexOf("."));
        String instanceName = tempName.toLowerCase();
        String javaClassName = tempName.substring(0, 1).toUpperCase() + tempName.substring(1);
        String objectDef = "=" + javaClassName + "()";
        interpreter.exec(instanceName + objectDef);
        Class JavaInterface = Class.forName(interfaceName);
        javaInt = interpreter.get(instanceName).__tojava__(JavaInterface);

        return javaInt;
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /*
         * JythonFactory jf = JythonFactory.getInstance(); EntityScript eType =
         * (EntityScript) jf.getJythonObject( "filaengine.entity.EntityScript",
         * "Test.py"); eType.onUpdate(10, new Scene());
         */
    }
}