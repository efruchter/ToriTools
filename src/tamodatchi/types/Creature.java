package tamodatchi.types;

import java.awt.Color;
import java.awt.Graphics2D;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.AbstractSprite.AbstractSpriteAdapter;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class Creature extends Entity implements EntityScript {

    private float mood, energy;
    private long age;
    private String name;

    public Creature() {

        setDim(new Vector2(64, 64));
        setPos(new Vector2(100, 100));

        addScript(this);

        setSprite(new AbstractSpriteAdapter() {
            @Override
            public void draw(Graphics2D g, Entity self) {
                g.setColor(Color.BLACK);
                g.drawRect(self.getPos().getWidth(), self.getPos().getHeight(), self.getDim().getWidth(), self.getDim()
                        .getHeight());
            }
        });
    }

    @Override
    public void onSpawn(Entity self, Level level) {
        age = 0;
        mood = .5f;
        energy = 1;
        name = "Mozart";
    }

    @Override
    public void onUpdate(Entity self, float time, Level level) {
        if (energy > 0) {
            energy -= .0005;
        }
        //self.setPos(self.getPos().add(new Vector2(1, 3)));
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

    public static enum State {
        SLEEP, AWAKE;
    }
}
