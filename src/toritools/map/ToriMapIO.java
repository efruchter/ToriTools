package toritools.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * HashMap IO utility.
 * 
 * @author toriscope
 * 
 */
public class ToriMapIO {
	public static <K, V> void writeMap(final File file, final HashMap<K, V> map)
			throws IOException {
		FileWriter f = new FileWriter(file);
		for (Entry<K, V> s : map.entrySet())
			f.write(s.getKey().toString() + " = " + s.getValue().toString()
					+ ";");
		f.close();
	}

	public static HashMap<String, String> readMap(final File file)
			throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		StringBuilder doc = new StringBuilder();
		while (scan.hasNextLine()) {
			doc.append(scan.nextLine()).append("\n");
		}
		HashMap<String, String> map = new HashMap<String, String>();
		for (String token : doc.toString().split(";")) {
			if (!token.contains("="))
				throw new RuntimeException("The token " + token
						+ "is not a valid keymap entry. Needs a ';' and '='.");
			else {
				String[] entry = token.split("=");
				map.put(entry[0].trim(), entry[1].trim());
			}

		}
		return map;
	}

	public static void writeVariables(final VariableCase vars, final File file)
			throws IOException {
		writeMap(file, vars.getVariables());
	}

	public static VariableCase readVariables(final File file)
			throws FileNotFoundException {
		return new VariableCase(readMap(file));
	}
}
