package audioProject.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Wave analysis engine. Maps are of size milliseconds.
 * @author toriscope
 *
 */
public class WaveController {

	private boolean beat = false;
	private float feel = 0.0f;
	
	final List<Float> feelArray = new ArrayList<Float>(), beatArray = new ArrayList<Float>();
			
	public WaveController() throws FileNotFoundException {
		Scanner feelScan = new Scanner(new File("audioProject/feel.kres"));
		while(feelScan.hasNextFloat()) {
			feelArray.add(feelScan.nextFloat());
		}		
		Scanner beatScan = new Scanner(new File("audioProject/beats.kres"));
		while(beatScan.hasNextFloat()) {
			beatArray.add(beatScan.nextFloat());
		}
	}

	long lastTime = 0;
	
	public void setTime44100(final long newTime) {
		
		beat = false;
		for(int i = (int) lastTime; i < newTime; i++) {
			beat = beat || beatArray.get(i) == 1;
		}
		lastTime = newTime;
		
		feel = feelArray.get((int) newTime);
		
		if(beat)
			System.out.println("BEAT");
		
	}

	public boolean isBeat() {
		return beat;
	}

	public float getFeel() {
		return feel;
	}
}
