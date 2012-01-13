package burd.customscripts;

import java.io.File;
import java.io.FileNotFoundException;

import toritools.entity.Entity;
import toritools.entity.physics.PhysicsModule;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class VolcanoParticleScript implements EntityScript {

	int timer = 200;

	PhysicsModule physicsModule;

	boolean up;

	public VolcanoParticleScript(final boolean up) {
		this.up = up;
	}

	public void onSpawn(Entity self) {

		physicsModule = new PhysicsModule(new Vector2(0, 0.2f), 1f, self);

		float y = (float) (up ? Math.random() * -20 : 0);
		float x = (float) (-10 + Math.random() * 20);
		physicsModule.addVelocity(new Vector2(x, y));
	}

	public void onUpdate(Entity self) {
		self.setPos(self.getPos().add(physicsModule.onUpdate()));
		ScriptUtils.moveOut(self, false, ScriptUtils.getCurrentLevel().solids);

		if (--timer == 0) {
			ScriptUtils.getCurrentLevel().killEntity(self);
		}

	}

	public void onDeath(Entity self, boolean isRoomExit) {

	}

	public static Entity getBlood() {
		try {
			Entity e = Importer.importEntity(new File(
					"burd/objects/blood.entity"), null);
			e.setScript(new VolcanoParticleScript(true));
			return e;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Entity getSparkle() {
		try {
			Entity e = Importer.importEntity(new File(
					"burd/objects/sparkle.entity"), null);
			e.setScript(new VolcanoParticleScript(true));
			return e;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Entity getBreadCrumb() {
		try {
			Entity e = Importer.importEntity(new File(
					"burd/objects/crumb.entity"), null);
			e.setScript(new VolcanoParticleScript(false));
			e.getDim().set((float) (6 + Math.random() * 5),
					(float) (6 + Math.random() * 5));
			return e;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
