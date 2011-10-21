package samplegame.entrypoint;

import java.io.FileNotFoundException;

import samplegame.scripting.ScriptFactory;

/**
 * This will be the main class for a simple game that uses toritools.
 * 
 * @author toriscope
 * 
 */
public class SampleGame {

	public static void main(final String[] args) throws FileNotFoundException {
		ScriptFactory s = new ScriptFactory();
		s.create().onSpawn();
	}
}
