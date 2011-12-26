package samplegame.customScripts;

import java.awt.event.KeyEvent;
import java.util.Random;

import samplegame.SampleGame;
import toritools.dialog.DialogNode;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;
import toritools.scripting.ScriptUtils.Direction;

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

    private Entity player;

    public void onSpawn(Level level, Entity self) {
        player = level.getEntityWithId("player");
        newDirection();
    }

    public void onUpdate(Level level, Entity self) {
        if (rand.nextDouble() > .99)
            newDirection();
        if (rand.nextDouble() > .8) {
            ScriptUtils.safeMove(self, Vector2.buildVector(direction).scale(speed),
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

        if (!SampleGame.inDialog && ScriptUtils.isColliding(self, player)) {
            
            SampleGame.setDisplayPrompt("Talk <SPACE>");

            if (SampleGame.keys.isPressedThenRelease(KeyEvent.VK_SPACE)) {
                level.spawnEntity(new DialogEntity(
                        new DialogNode(
                                "The 1689 Boston revolt was a popular uprising against the rule of Sir Edmund Andros (pictured), governor of the Dominion of New England that followed the Glorious Revolution deposing James II of England, who had appointed Andros. During the revolt, on April 18, 1689, a well-organized body of Puritan citizens and militiamen entered the dominion capital of Boston and arrested officials of the dominion, a colonial entity composed of present-day Maine, New Hampshire, Vermont, Massachusetts, Rhode Island, Connecticut, New York, and New Jersey. The rebellion was inspired by actions taken by Andros and dominion administrators, including promoting the Church of England, invalidating land titles, and famously attempting to seize the colonial charter of Connecticut.")));
            }
        }
    }

    public void onDeath(Level level, Entity self, boolean isRoomExit) {
    }

    private void newDirection() {
        direction = rand.nextFloat() * 6.28f;
    }
}