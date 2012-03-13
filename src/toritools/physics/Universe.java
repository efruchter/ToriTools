package toritools.physics;

import java.util.HashMap;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

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

    public void step(final float dt) {

        /*
         * Step the world
         */
        world.step(dt, 10, 6);
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
        fd.restitution = .0f;
        Body body = world.createBody(bd);
        body.createFixture(fd);

        bodyMap.put(ent, body);

        if (bodyType == BodyType.DYNAMIC)
            ent.addScript(serviceScript);

        return body;
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

    // public void applyVelocity(final Entity e, final Vector2 force) {
    // Body b = bodyMap.get(e);
    // if (b != null)
    // b.setLinearVelocity(b.getLinearVelocityFromWorldPoint(worldPoint);
    // }

    public void applyForce(final Entity e, final Vector2 force) {
        Body b = bodyMap.get(e);
        if (b != null)
            b.applyForce(force.toVec(), b.getWorldCenter());
    }

    public void setVeclocity(Entity dragging, Vector2 scale) {
        Body b = bodyMap.get(dragging);
        if (b != null)
            b.setLinearVelocity(scale.toVec());
    }

    public void addVelocity(Entity dragging, Vector2 scale) {
        Body b = bodyMap.get(dragging);
        if (b != null) {
            b.m_linearVelocity.x += scale.x;
            b.m_linearVelocity.y += scale.y;
        }
    }

    public boolean isStanding(Entity e) {
        ContactEdge edge = bodyMap.get(e).getContactList();
        while (edge != null) {
            if (edge.contact.m_manifold.localNormal.y <= 0) {
                return true;
            }
            edge = edge.next;
        }
        return false;
    }
}
