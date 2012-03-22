package toritools.physics;

import java.util.HashMap;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;

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

    final public float PTM_RATIO = 32;

    public Universe(final Vector2 gravity) {
        world = new World(new Vec2(gravity.x, gravity.y), true);
        bodyMap = new HashMap<Entity, Body>();
    }

    public void setContactListener(final ContactListener listener) {
        world.setContactListener(listener);
    }

    public void step(final float dt) {

        /*
         * Step the world
         */
        world.step(dt, 6, 8);
    }

    /**
     * Add an entity to the simulation. The entity will be given an additional
     * script that keeps it synced with its simulation representation..
     * 
     * @param ent
     *            the entity to add.
     * @param bodyType
     *            the Body type
     * @param allowRotation
     *            true if rotation should be allowed.
     * @param density
     *            default is 1, adjust accordingly.
     */
    public Body addEntity(final Entity ent, final BodyType bodyType, final boolean allowRotation,
            final boolean spherical, final float density, final float friction) {
        BodyDef bd = new BodyDef();
        bd.fixedRotation = !allowRotation;
        bd.type = bodyType;
        bd.position.set(ent.getPos().x / PTM_RATIO + ent.getDim().x / 2 / PTM_RATIO,
                ent.getPos().y / PTM_RATIO + ent.getDim().y / 2 / PTM_RATIO);

        Shape p = null;
        if (!spherical) {
            p = new PolygonShape();
            ((PolygonShape) p).setAsBox(ent.getDim().x / PTM_RATIO / 2, ent.getDim().y / PTM_RATIO / 2);
        } else {
            p = new CircleShape();
            p.m_radius = ent.getDim().x / PTM_RATIO / 2;
        }

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = p;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = .3f;
        // fd.filter.categoryBits = cat;
        fd.userData = ent;

        Body body = world.createBody(bd);
        body.createFixture(fd);
        body.m_userData = ent;

        world.setContactFilter(new ContactFilter() {
        });

        bodyMap.put(ent, body);

        if (bodyType == BodyType.DYNAMIC)
            ent.addScript(serviceScript);

        return body;
    }

    public Fixture createFloorSensor(final Entity e) {
        Body body = bodyMap.get(e);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(e.getDim().x / 2 / PTM_RATIO, e.getDim().y / PTM_RATIO);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = true;
        fd.userData = e;

        Fixture f = body.createFixture(fd);

        return f;
    }

    /**
     * Construct a hinge. Be sure the shapes are overlapping at the given point.
     * 
     * @param a
     * @param b
     * @param hingePosition
     */
    public void addHinge(Entity a, Entity b, final Vector2 hingePosition) {
        RevoluteJointDef def = new RevoluteJointDef();
        def.initialize(bodyMap.get(a), bodyMap.get(b), hingePosition.scale(1f / PTM_RATIO).toVec());
        world.createJoint(def);
    }

    public void addSpring(Entity a, Entity b) {
        DistanceJointDef def = new DistanceJointDef();
        def.initialize(bodyMap.get(a), bodyMap.get(b), bodyMap.get(a).getWorldCenter(), bodyMap.get(b).getWorldCenter());
        def.collideConnected = true;
        world.createJoint(def);
    }

    public void addWeld(Entity a, Entity b) {
        WeldJointDef def = new WeldJointDef();
        def.initialize(bodyMap.get(a), bodyMap.get(b), bodyMap.get(a).getWorldCenter());
        world.createJoint(def);
    }

    /**
     * This script keeps the entity model synced with the physics model.
     */
    private final EntityScript serviceScript = new EntityScriptAdapter() {

        @Override
        public void onDeath(Entity self, Level level, boolean isRoomExit) {
            if (!isRoomExit) {
                removeEntity(self);
            }
        }

        @Override
        public void onUpdate(Entity self, float time, Level level) {
            Transform body = bodyMap.get(self).getTransform();
            Vector2 newPos = new Vector2((body.position.x * PTM_RATIO) - self.getDim().x / 2, body.position.y
                    * PTM_RATIO - self.getDim().y / 2);
            // System.out.println(newPos + " | " + self.getPos());
            self.setPos(newPos);
            self.setDirection((int) (body.getAngle() * 57.3));
        }
    };

    /**
     * Remove an entity, and corresponding body from the map and the simulation.
     * 
     * @param ent
     */
    public void removeEntity(Entity ent) {
        if (bodyMap.containsKey(ent)) {
            world.destroyBody(bodyMap.get(ent));
            bodyMap.remove(ent);
        }
    }

    public void applyForce(final Entity e, final Vector2 force) {
        Body b = bodyMap.get(e);
        if (b != null)
            b.applyForce(force.scale(1f / b.m_mass).toVec(), b.getWorldCenter());
    }

    public void applyLinearImpulse(final Entity e, final Vector2 force) {
        Body b = bodyMap.get(e);
        if (b != null)
            b.applyLinearImpulse(force.scale(1f / b.m_mass).toVec(), b.getWorldCenter());
    }

    public void setVelocity(Entity dragging, Vector2 scale) {
        Body b = bodyMap.get(dragging);
        if (b != null)
            b.setLinearVelocity(scale.toVec());
    }

    public boolean isCollidingWithType(final Entity e, final String type) {
        ContactEdge edge = bodyMap.get(e).getContactList();
        while (edge != null) {
            if (((Entity) edge.other.m_userData).getType().equals(type))
                return true;
            edge = edge.next;
        }
        return false;
    }
}
