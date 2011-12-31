package burd.customscripts;

import java.io.File;
import java.io.FileNotFoundException;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class BloodScript implements EntityScript {
	
	private static File entityFile = new File("burd/objects/blood.entity");
	
	int timer = 200;

	PhysicsModule physicsModule;

	public void onSpawn(Level level, Entity self) {

		physicsModule = new PhysicsModule(new Vector2(0, 0.2f), 1f, self);

		physicsModule.onStart();
		float y = (float) ( Math.random() * -20);
		float x = (float) (-10 + Math.random() * 20);
		physicsModule.addVelocity(new Vector2(x, y));
	}

	public void onUpdate(Level level, Entity self) {
		ScriptUtils.safeMove(self, physicsModule.onUpdate(),
				level.solids.toArray(new Entity[0]));
		
		if(--timer == 0) {
			level.killEntity(self);
		}

	}

	public void onDeath(Level level, Entity self, boolean isRoomExit) {

	}
	
	public static Entity getBlood() {
		try {
			Entity e = Importer.importEntity(entityFile, null);
			e.script = new BloodScript();
			return e;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
