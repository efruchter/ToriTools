package toritools.pathing;

import java.util.List;

import toritools.math.Vector2;

public class SimpleLinearPathfinder {

	public static Vector2 findStraightPosition(List<TimePosition> timeLine,
			float time) {
		if (time >= timeLine.get(timeLine.size() - 1).time) {
			return timeLine.get(timeLine.size() - 1).position;
		}
		if (time <= timeLine.get(0).time) {
			return timeLine.get(0).position;
		}
		int atTime = -1;
		for (int i = 1; i < timeLine.size() && atTime == -1; i++) {
			if (timeLine.get(i).time >= time) {
				atTime = i;
			}
		}
		Vector2 direction = timeLine.get(atTime).position.sub(
				timeLine.get(atTime - 1).position).unit();
		float velocity = Vector2.dist(timeLine.get(atTime - 1).position,
				timeLine.get(atTime).position)
				/ (timeLine.get(atTime).time - timeLine.get(atTime - 1).time);
		return timeLine.get(atTime - 1).position.add(direction.scale(velocity
				* (time - timeLine.get(atTime - 1).time)));
	}

	public static class TimePosition {
		public Vector2 position;
		public long time;
	}

}