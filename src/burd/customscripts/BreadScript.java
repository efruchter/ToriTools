package burd.customscripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import toritools.entity.Entity;
import toritools.io.Importer;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

/**
 * When active, the bread stays in it's spot. Otherwise, the bread tails the
 * player.
 * 
 * @author toriscope
 * 
 */
public class BreadScript implements EntityScript {

	private static List<Entity> trailingQueue = new ArrayList<Entity>();

	MidpointChain chain;

	Vector2 origPos;

	Entity player, trailBread;

	@Override
	public void onSpawn(Entity self) {

		player = ScriptUtils.getCurrentLevel().getEntityWithId("player");

		// First spawn
		if (origPos == null) {
			origPos = self.getPos().clone();
		}

		chain = new MidpointChain(self.getPos().clone(), origPos, 15);
	}

	@Override
	public void onUpdate(Entity self) {
		boolean grabeable = Vector2.dist(chain.getA(), chain.getB()) < 10;
		if (self.isActive()) {
			chain.setB(origPos);
			if (grabeable && ScriptUtils.isColliding(self, player)) {
				self.setActive(false);
				trailBread = trailingQueue.isEmpty() ? player : trailingQueue
						.get(trailingQueue.size() - 1);
				trailingQueue.add(self);
			} else {
				trailingQueue.remove(self);
			}
		} else {
			if (trailBread == player || trailingQueue.contains(trailBread))
				chain.setB(trailBread.getPos().clone());
			else {
				int index = trailingQueue.indexOf(self);
				trailBread = index == 0 ? player : trailingQueue.get(index - 1);
			}
		}
		if (!grabeable)
			chain.smoothTowardB();
		self.setPos(chain.getA());

		for (Entity nest : ScriptUtils.getCurrentLevel().getEntitiesWithType("nest")) {
			if (ScriptUtils.isColliding(self, nest)) {
				ScriptUtils.getCurrentLevel().despawnEntity(self);
			}
		}
	}

	@Override
	public void onDeath(Entity self, boolean isRoomExit) {
		trailingQueue.remove(self);
		for (int i = 0; i < 5; i++) {
			Entity blood = VolcanoParticleScript.getSparkle();
			blood.setPos(self.getPos().clone());
			ScriptUtils.getCurrentLevel().spawnEntity(blood);
		}
		Entity e;
		try {
			e = Importer.importEntity(new File("burd/objects/player.entity"), null);
			e.getVariableCase().setVar("id", "");
			e.setType("baby");
			e.setScript(new ChickScript());
			e.setDim(e.getDim().scale(.5f));
			e.setActive(false);
			e.setPos(self.getPos().clone());
			ScriptUtils.getCurrentLevel().spawnEntity(e);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}

}
