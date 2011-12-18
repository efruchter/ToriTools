package samplegame.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;

import samplegame.load.Importer;
import samplegame.scripting.EntityScript;
import samplegame.scripting.ScriptUtils;
import samplegame.scripting.ScriptUtils.Direction;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;

/**
 * The script for the wolf. This shows how to spawn instances on the fly, as
 * well as how to use the script to store state.
 * 
 * @author toriscope
 * 
 */
public class WolfScript implements EntityScript {
	private Random rand = new Random();
	private float speed = 4;
	private float direction = 0;

	private Boolean canShoot = true;

	public void onSpawn(Level level, Entity self) {
		newDirection();
	}

	public void onUpdate(Level level, Entity self) {
		if (rand.nextDouble() > .99)
			newDirection();
		if (rand.nextDouble() > .8) {
			ScriptUtils.safeMove(self,
					Vector2.buildVector(direction).scale(speed),
					level.solids.toArray(new Entity[0]));
			self.sprite.nextFrame();
		}

		switch (Direction.findEnum(direction)) {
		case DOWN:
		case DOWN_RIGHT:
		case DOWN_LEFT:
			self.sprite.setCylcle(3);
			break;
		case UP:
		case UP_RIGHT:
		case UP_LEFT:
			self.sprite.setCylcle(0);
			break;
		case RIGHT:
			self.sprite.setCylcle(2);
			break;
		case LEFT:
			self.sprite.setCylcle(1);
			break;
		}

		if (canShoot
				&& ScriptUtils.isColliding(self,
						level.getIntityWithId("player"))) {
			System.out.println("BOOM!");
			try {
				Entity cross = Importer.importEntity(new File(
						"levels/objects/wall/cross.entity"),
						new HashMap<String, String>());
				cross.pos = self.pos.clone();
				cross.solid = false;
				cross.variables.setVar("id", "cross");

				cross.script = new EntityScript() {

					int timer = 200;
					Entity player;

					@Override
					public void onSpawn(Level level, Entity self) {
						player = level.getIntityWithId("player");
						canShoot = false;
					}

					@Override
					public void onUpdate(Level level, Entity self) {
						self.pos = self.pos.add(Vector2.toward(self.pos,
								player.pos).scale(1));
						if (--timer == 0) {
							level.killEntity(self);
							canShoot = true;
						}
					}

					@Override
					public void onDeath(Level level, Entity self,
							boolean isRoomExit) {

					}
				};

				level.spawnEntity(cross);

			} catch (FileNotFoundException e) {
				System.out.println("Tried to spawn a cross, but the entity file couldn't be found!");
			}
		}
	}

	public void onDeath(Level level, Entity self, boolean isRoomExit) {
	}

	private void newDirection() {
		direction = rand.nextFloat() * 6.28f;
	}
}
