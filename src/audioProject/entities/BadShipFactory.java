package audioProject.entities;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.pathing.interpolator.HermiteKeyFrameInterpolator;
import toritools.scripting.EntityScript.EntityScriptAdapter;

public class BadShipFactory {

	public static Entity makePathedEnemy(final HermiteKeyFrameInterpolator path, final float aggressionRatio) {

		final Entity entity = new Entity();

		entity.addScript(new EntityScriptAdapter() {

			float allTime;
			Vector2 origVect;

			@Override
			public void onSpawn(Entity self, Level level) {
				allTime = 0;
				self.setPos(origVect = path.getPositionDeltaAtTime(0));
			}

			@Override
			public void onUpdate(Entity self, float time, Level level) {
				allTime += time;
				if (allTime > path.getEndTime()) {
					level.despawnEntity(self);
				} else {
					self.setPos(origVect.add(path
							.getPositionDeltaAtTime(allTime)));
				}
			}
		});

		return entity;
	}

}