package tamodatchi.types;

import java.io.File;

import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.ImageSprite;
import toritools.math.MidpointChain;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Creature extends Entity implements EntityScript {

    /**
     * Important state stuff.
     */
    private float mood, energy;
    private long age;
    private String name;
    private State state;

    // Movement internals
    private Vector2 moveTarget;
    private float moveTargetSpeed = .6f;
    private MidpointChain moveChain;

    public Creature() {

        setDim(new Vector2(64, 64));
        setPos(new Vector2(100, 100));

        addScript(this);

        setSprite(new ImageSprite(new File("tamodatchi/rabbit.gif"), 1, 1));
    }

    @Override
    public void onSpawn(Entity self, Level level) {
        age = 0;
        mood = .5f;
        energy = .9f;
        name = "Mozart";
        moveTarget = self.getPos();
        moveChain = new MidpointChain(self.getPos(), 20);
        state = State.ROAM;
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        // Maintenance
        if (energy > 0) {
            energy -= .0000005;
        }

        // Transitions
        if (state == State.ROAM) {
            if (!level.getEntitiesWithType("food").isEmpty()) {
                state = State.HUNTING;
            }
        }

        // Actions
        if (state == State.ROAM && Math.random() < .06 * energy * energy) {
            moveTarget = new Vector2((level.getDim().getWidth() - self.getDim().getWidth() / 2) * Math.random(), (level
                    .getDim().getHeight() - self.getDim().getHeight() / 2) * Math.random());
            Debug.print("Moving to: " + moveTarget);
        }

        if (state == State.HUNTING) {
            Vector2 closest = null;
            float bestDist = Float.MAX_VALUE;
            for (Entity e : level.getEntitiesWithType("food")) {
                if (ScriptUtils.isColliding(self, e)) {
                    eatFood((Food) e, level);
                    break;
                }
                if (closest == null || self.getPos().dist(e.getPos()) < bestDist) {
                    closest = e.getPos().add(e.getDim().scale(.5f));
                    bestDist = self.getPos().dist(e.getPos());
                }
            }
            moveTarget = closest;
            Debug.print("Moving towards food");
        }
        
        if (moveTarget != null) {
            Vector2 move = moveTarget.sub(self.getPos().add(self.getDim().scale(.5f)));
            if (move.mag() > moveTargetSpeed) {
                move = move.unit().scale(moveTargetSpeed * energy);
                moveChain.setA(moveChain.getA().add(move));
                moveChain.smoothTowardA();
                self.setPos(moveChain.getB().sub(self.getDim().scale(.5f)));
            } else {
                moveTarget = null;
            }
        }
    }

    private void eatFood(final Food e, final Level level) {
        level.despawnEntity(e);
        energy = Math.min(1, .3f + energy);
        state = State.ROAM;
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {

    }

    public float getMood() {
        return mood;
    }

    public float getEnergy() {
        return energy;
    }

    public long getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return state;
    }

    public static enum State {
        SLEEP, ROAM, HUNTING;
    }
}
