package audioProject.controller;

import java.util.Scanner;

/**
 * Wave analysis engine. Maps are of size milliseconds.
 * @author toriscope
 *
 */
public class WaveController {

	private boolean isBeat = false;
	private float feel = 0.0f;
	
	final Scanner feelScanner;
	final Scanner beatScanner;
			
	public WaveController(final long maxMilliTime) {
		StringBuffer feel = new StringBuffer(), beat = new StringBuffer();
		long beatTimer = -1;
		for(long i = 0; i <= maxMilliTime; i+= (16)) {		
			feel.append((float) Math.sin(i * .00005)).append("\n");
			beat.append(beatTimer < 0 ? 1 : 0).append("\n");
			if (beatTimer-- < 0) {
				beatTimer = 100;
			}
		}
		feelScanner = new Scanner(feel.toString());
		beatScanner = new Scanner(beat.toString());
	}
	
	long time = 0;
	
	public void setTime44100(final long newTime) {
		System.out.println("tDist: " + (newTime - time));
		feel = feelScanner.nextFloat();
		isBeat = beatScanner.nextFloat() == 1;
		System.out.print(isBeat?isBeat:"");
		time += 1000 / 60;
	}

	public boolean isBeat() {
		return isBeat;
	}

	public float getFeel() {
		return feel;
	}
}
