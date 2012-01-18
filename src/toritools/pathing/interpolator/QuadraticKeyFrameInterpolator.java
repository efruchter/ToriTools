package toritools.pathing.interpolator;

import java.util.List;

import toritools.math.Vector2;

/**
 * Interpolate a path based on keyframes.
 * 
 * @author toriscope
 * 
 */
public class QuadraticKeyFrameInterpolator {

	private final List<HermiteKeyFrame> keyFrames;

	public QuadraticKeyFrameInterpolator(final List<HermiteKeyFrame> keyFrames) {
		this.keyFrames = keyFrames;
	}

	/**
	 * Get the relative position for a given time.
	 * 
	 * @param time
	 *            the time to fetch the interpolated position for.
	 * @return the relative position at time.
	 */
	public Vector2 getPositionDeltaAtTime(final float time) {
		return keyFrames.isEmpty() ? new Vector2() : keyFrames.get(0).pos;
	}

	/**
	 * A simple keyframe with a position, direction at position, and a time.
	 * Comparable by time.
	 * 
	 * @author toriscope
	 * 
	 */
	public static class HermiteKeyFrame implements Comparable<HermiteKeyFrame> {
		final public Vector2 pos;
		final public Vector2 dir;
		final public float time;

		public HermiteKeyFrame(final Vector2 pos, final Vector2 dir, final float time) {
			this.pos = pos;
			this.dir = dir;
			this.time = time;
		}

		@Override
		public int compareTo(HermiteKeyFrame other) {
			return Math.round(this.time - other.time);
		}

	}
}
