package toritools.io.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * This class simplifies and abstracts dynamic class loading/compiling.
 * compileStringToInstance() is the recommended method to use. The rest simply
 * exist to give you more control over the compiling/loading process, if
 * desired. This class does not clean up after itself by default, and will leave
 * the .java and .class files lying around in whatever directory they were
 * required. To initiate a cleanup, a method has been provided, cleanUp().
 * 
 * @author efruchter
 */
@SuppressWarnings("rawtypes")
public final class ClassIO {

	/**
	 * List of files generated, to be deleted by cleanUp(boolean).
	 */
	private static volatile Set<File> trashList = new HashSet<File>();

	private ClassIO() {
		;
	}

	/**
	 * Returns a class, built and loaded from a java class file. To convert the
	 * class, you can use:
	 * "Interface instance = (Interface) theClass.newInstance();"
	 * 
	 * @param classFile
	 *            the class file, compiled with the same java version as
	 *            current.
	 * @param className
	 *            name of the class
	 * @return the constructed class file
	 * @throws IOException
	 *             io issue
	 * @throws ClassNotFoundException
	 *             The given class is not available.
	 */
	public static Class getClassFromClassFile(final File classFile,
			final String className) throws IOException, ClassNotFoundException {
		// Create a new class loader with the directory
		ClassLoader cl = new URLClassLoader(new URL[] { new File("TEMP/")
				.toURI().toURL() });
		return cl.loadClass(className);
	}

	/**
	 * Compile a java class file.
	 * 
	 * @param file
	 *            java class file to compile.
	 * @throws IOException
	 *             io exception has occurred.
	 * @throws CompilationException
	 *             used to relay important error information when compilation
	 *             fails.
	 */
	@SuppressWarnings("unchecked")
	public static void compileClass(final File file) throws IOException,
			CompilationException {
		JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null,
				null);
		Iterable fileObjects = sjfm.getJavaFileObjects(file);
		final CompilationException exc = new CompilationException(null);
		jc.getTask(null, sjfm, new DiagnosticListener() {
			@Override
			public void report(final Diagnostic diagnostic) {
				exc.setDiagnostic(diagnostic);
			}
		}, null, null, fileObjects).call();
		if (exc.getDiagnostic() != null) {
			throw exc;
		}
		sjfm.close();
	}

	/**
	 * This convenience method will save a string as a java class, compile it,
	 * and then load it as a Class, and finally return it as an instantiation of
	 * one of I's subclasses. The class name is inserted automatically into the
	 * class string, so please replace 'ClassName' with 'ANYCLASS' in your class
	 * string, or use the setter method if you are sure your name is unique.
	 * 
	 * @param <I>
	 *            This interface should be the superclass of all the instances
	 *            you plan to load with this method.
	 * @param classString
	 *            the string that makes up the class.
	 * @param className
	 *            the name of the class.
	 * @param directoryPath
	 *            the legal path to the directory from the current.
	 * @return an instance of one of I's implementation.
	 * @throws IOException
	 *             standard io exception
	 * @throws InstantiationException
	 *             the compiled class does not have a nullary constructor, or is
	 *             not a class that can be instantiated.
	 * @throws IllegalAccessException
	 *             access issue forming a new instance of the class.
	 * @throws CompilationException
	 *             Used to relay important error information when compilation
	 *             fails.
	 * @throws ClassNotFoundException
	 *             The given class is not available.
	 */
	@SuppressWarnings("unchecked")
	public static <I> I compileStringToInstance(final String classString,
			final String className, final String directoryPath)
			throws IOException, InstantiationException, IllegalAccessException,
			CompilationException, ClassNotFoundException {
		File sourceFile = new File(directoryPath + className + ".java");
		File classFile = new File(directoryPath + className + ".class");
		FileWriter fW = new FileWriter(sourceFile);
		fW.write(classString);
		fW.close();
		trashList.add(sourceFile);
		trashList.add(classFile);
		compileClass(sourceFile);
		Class classd = getClassFromClassFile(classFile, className);
		return (I) classd.newInstance();
	}

	/**
	 * Delete the class and source files spawned since last cleanup or session
	 * start.
	 * 
	 * @param now
	 *            if true, delete the files immediately. if false, delete on JVM
	 *            exit.
	 */
	public static void cleanUp(final boolean now) {
		for (File f : trashList) {
			if (now) {
				f.delete();
			} else {
				f.deleteOnExit();
			}
		}
		trashList.clear();
	}

	/**
	 * A vessel for a compilation error diagnostic.
	 * 
	 * @author efruchter3
	 * 
	 */
	@SuppressWarnings("serial")
	public static class CompilationException extends Exception {
		private Diagnostic diagnostic;

		/**
		 * Takes in a diagnostic object to store.
		 * 
		 * @param diagnostic
		 *            represents the error information.
		 */
		public CompilationException(final Diagnostic diagnostic) {
			super("Compilation error has occured. See enclosed Diagnostic.");
			this.diagnostic = diagnostic;
		}

		public Diagnostic getDiagnostic() {
			return diagnostic;
		}

		public void setDiagnostic(final Diagnostic d) {
			this.diagnostic = d;
		}
	}
}