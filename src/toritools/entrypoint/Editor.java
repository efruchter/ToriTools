package toritools.entrypoint;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import toritools.leveleditor.LevelEditor;

public class Editor {
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerException {
		new LevelEditor();
	}
}
