package audioProject.controller;

import static java.lang.Math.min;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import toritools.additionaltypes.HistoryQueue;

/**
 * Wave analysis engine. Maps are of size milliseconds.
 * @author toriscope
 *
 */
public class WaveController {

	private boolean beat = false;
	private float feel = 0.0f;
	
	final List<Float> feelArray = new ArrayList<Float>(), beatArray = new ArrayList<Float>();
	
	final HistoryQueue<Float> history;
			
	public WaveController(final String songName, final int historyLength) throws FileNotFoundException {
		Scanner feelScan = new Scanner(new File("audioProject/" + songName + "_feels.kres"));
		while(feelScan.hasNextFloat()) {
			float entry = feelScan.nextFloat(); 
			feelArray.add(entry);
		}		
		Scanner beatScan = new Scanner(new File("audioProject/" + songName + "_beats.kres"));
		while(beatScan.hasNextFloat()) {
			beatArray.add(beatScan.nextFloat());
		}
		history = new HistoryQueue<Float>(historyLength + 1);
		for(int i = 0; i < historyLength; i++) {
			history.push(0f);
		}
	}

	long lastTime = 0;
	long timeDifference = 0;
	
	public void setTime44100(final long newTime) {
		
		beat = false;
		for(int i = (int) lastTime; i < newTime; i++) {
			if (beat = beatArray.get(i) > 0)
				break;
		}
		lastTime = newTime;
		
		feel = min(1, feelArray.get((int) newTime));	
		
		history.push(feel);
	}
	
	public List<Float> pastN () {
		return history;
	}

	public boolean isBeat() {
		return beat;
	}

	public float getFeel() {
		return feel;
	}
	
	public float getAverageFeel() {
		float a = 0;
		for (float b: feelArray) {
			a += b;
		}
		return a / feelArray.size();
	}
}
