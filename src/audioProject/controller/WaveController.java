package audioProject.controller;

/**
 * No what these stats should/will do.
 * @author toriscope
 *
 */
public class WaveController {

	private boolean isBeat = false;
	private double feel = 0.0;

	public void setTime(final long newTime) {
		double s = Math.sin(newTime * .01);
		isBeat = Math.abs(s) > .99;
		//System.out.println(s);
		feel = Math.cos(newTime * .0000001);
	}

	public boolean isBeat() {
		return isBeat;
	}

	public double getFeel() {
		return feel;
	}
}
