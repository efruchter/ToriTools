package toritools.entrypoint;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import toritools.leveleditor.LevelEditor;

public class Editor {
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerException {
		if (System.getProperty("os.name").contains("Windows ")) {
			System.setProperty("sun.java2d.d3d", "True");
			System.setProperty("sun.java2d.accthreshold", "0");
		} else {
			System.setProperty("sun.java2d.opengl=true", "True");
		}
		
		new LevelEditor();
	}
}
