package tamodatchi.types;

import java.io.File;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.ImageSprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class Food extends Entity implements EntityScript {

    public Food() {
        this.addScript(this);

        this.setType("food");

        this.setDim(new Vector2(32, 32));
        this.setSprite(new ImageSprite(new File("tamodatchi/food.png"), 12, 4));
        this.getSprite().setCycle((int) (Math.random() * 4));
        this.getSprite().setFrame((int) (Math.random() * 12));
    }

    @Override
    public void onSpawn(Entity self, Level level) {

    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {

    }

    @Override
    public void onDeath(Entity self, Level level, boolean isRoomExit) {

    }

}
