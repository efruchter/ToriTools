package audioProject.controller;

/**
 * No what these stats should/will do.
 * @author toriscope
 *
 */
public class WaveController {

	private boolean isBeat = false;
	private float feel = 0.0f;

	public void setTime(final long newTime) {
		double s = Math.sin(newTime * .1);
		isBeat = Math.abs(s) > .99;
		//System.out.println(s);
		feel = (float) Math.cos(newTime * .0000001);
	}

	public boolean isBeat() {
		return isBeat;
	}

	public float getFeel() {
		return feel;
	}
}