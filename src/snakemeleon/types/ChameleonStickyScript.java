package snakemeleon.types;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;

import snakemeleon.Snakemeleon;
import snakemeleon.SnakemeleonConstants;
import toritools.debug.Debug;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.scripting.EntityScript.EntityScriptAdapter;
import toritools.scripting.ScriptUtils;

/**
 * This script allows the chameleon to stick to things! Make sure to add it
 * after the entity is added to the physics world, so it can apply the direction
 * negation. It also has the necessary stuff to handle contact detection.
 * 
 * @author toriscope
 * 
 */
public class ChameleonStickyScript extends EntityScriptAdapter implements ContactListener {

    boolean isGrabbing = false;

    Joint weld = null;

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        boolean grabKey = ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_SPACE);

        // Activate weld
        if (grabKey && !isGrabbing && !touchQueue.isEmpty()) {
            Snakemeleon.uni.setRotationDeg(self, 0);
            weld = Snakemeleon.uni.addWeld(self, touchQueue.get(0));
            isGrabbing = true;
            Debug.print("Joint created");
        }

        // Destroy weld
        if (!grabKey && isGrabbing && weld != null) {
            Snakemeleon.uni.destroyJoint(weld);
            isGrabbing = false;
            Debug.print("Joint Destroyed");
        }

        if (!isGrabbing)
            self.setDirection(0);
    }

    /*
     * Shape contact methods. You can't manipulate the universe from within
     * these. They are only here for temporary testing purposes.
     */

    public static List<Entity> touchQueue = new LinkedList<Entity>();

    @Override
    public void beginContact(Contact c) {
        Entity a = (Entity) c.m_fixtureA.m_userData, b = (Entity) c.m_fixtureB.m_userData;
        if (a.getType().equals("player") && b.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.add(b);
        } else if (b.getType().equals("player") && a.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.add(a);
        }
    }

    @Override
    public void endContact(Contact c) {
        Entity a = (Entity) c.m_fixtureA.m_userData, b = (Entity) c.m_fixtureB.m_userData;
        if (a.getType().equals("player") && b.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.remove(b);
        } else if (b.getType().equals("player") && a.getType().equals(SnakemeleonConstants.dynamicPropType)) {
            touchQueue.remove(a);
        }
    }

    @Override
    public void postSolve(Contact arg0, ContactImpulse arg1) {

    }

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {

    }
}
