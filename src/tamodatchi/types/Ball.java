package tamodatchi.types;

import java.io.File;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.physics.PhysicsModule;
import toritools.entity.sprite.ImageSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.ScriptUtils;

public class Ball extends Entity implements EntityScript {

    private PhysicsModule physics;

    public Ball() {
        this.addScript(this);

        this.setType("ball");

        this.setDim(new Vector2(32, 32));
        this.setSprite(new ImageSprite(new File("tamodatchi/ball.png"), 1, 1));
    }

    @Override
    public void onSpawn(Entity self, Level level) {
        physics = new PhysicsModule(Vector2.ZERO, new Vector2(.5f, .5f), self);
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        self.setPos(self.getPos().add(physics.onUpdate(time)));

        for (Entity pet : level.getEntitiesWithType("pet")) {
            if (ScriptUtils.isColliding(pet, self)) {
                physics.addVelocity(self.getPos().sub(pet.getPos()));
            }
        }

        for (Entity ball : level.getEntitiesWithType("ball")) {
            if (ball != self && ScriptUtils.isColliding(ball, self)) {
                physics.addVelocity(self.getPos().sub(ball.getPos()));
            }
        }

        if (!ScriptUtils.isColliding(level, self)) {
            level.despawnEntity(self);
        }
    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {

    }

}
