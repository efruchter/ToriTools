package toritools.physics;

import java.util.HashMap;
import java.util.Map.Entry;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;
import toritools.scripting.EntityScript.EntityScriptAdapter;

/**
 * A wrapper for Box2d's world.
 * 
 * @author toriscope
 * 
 */
public class Universe {

    final private World world;
    final private HashMap<Entity, Body> bodyMap;

    public Universe(final Vector2 gravity) {
        world = new World(new Vec2(gravity.x, gravity.y), true);
        bodyMap = new HashMap<Entity, Body>();
    }

    public void step(final float dt) {

        /*
         * Step the world
         */
        world.step(dt, 8, 3);
    }

    public void addEntity(final Entity ent, final boolean dynamic, final boolean circular, final boolean allowRotation) {
        removeEntity(ent);
        BodyDef bd = new BodyDef();
        bd.fixedRotation = !allowRotation;
        bd.allowSleep = true;
        bd.type = dynamic ? BodyType.DYNAMIC : BodyType.STATIC;
        bd.position.set(ent.getPos().x, ent.getPos().y);

        PolygonShape p = new PolygonShape();
        p.setAsBox(ent.getDim().x, ent.getDim().y);

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = p;
        fd.density = 0.9f;
        fd.friction = 0.3f;
        fd.restitution = 0.6f;
        Body body = world.createBody(bd);
        body.createFixture(fd);

        bodyMap.put(ent, body);

        ent.addScript(serviceScript);
    }

    /**
     * This script keeps the entity model synced with the physics model.
     */
    private final EntityScript serviceScript = new EntityScript() {

        @Override
        public void onDeath(Entity self, Level level, boolean isRoomExit) {
            if (!isRoomExit) {
                removeEntity(self);
            }
        }

        @Override
        public void onSpawn(Entity self, Level level) {

        }

        @Override
        public void onUpdate(Entity self, float time, Level level) {

            Body body = bodyMap.get(self);

            self.setPos(new Vector2(body.getPosition().x, body.getPosition().y));
            self.setDirection((int) (body.getAngle() * 57.3));
        }
    };

    private void removeEntity(Entity ent) {
        if (bodyMap.containsKey(ent)) {
            world.destroyBody(bodyMap.get(ent));
            bodyMap.remove(ent);
        }
    }
}
