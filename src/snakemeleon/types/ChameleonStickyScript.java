package snakemeleon.types;

import java.awt.event.KeyEvent;

import org.jbox2d.dynamics.joints.Joint;

import snakemeleon.Snakemeleon;
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
public class ChameleonStickyScript extends EntityScriptAdapter {

    public static boolean isGrabbing = false;

    public static Entity grabbingEntity = null;

    @Override
    public void onSpawn(Entity self, Level level) {
        isGrabbing = false;
        grabbingEntity = null;
    }

    Joint weld = null;

    @Override
    public void onUpdate(Entity self, float time, Level level) {

        boolean grabKey = ScriptUtils.getKeyHolder().isPressed(KeyEvent.VK_SPACE);

        // Activate weld
        if (grabKey && !isGrabbing && !Snakemeleon.touchQueue.isEmpty()) {
            Snakemeleon.uni.setRotationDeg(self, 0);
            weld = Snakemeleon.uni.addWeld(self, grabbingEntity = Snakemeleon.touchQueue.get(0));
            isGrabbing = true;
            Debug.print("Joint created");
        }

        // Destroy weld
        if (!grabKey && isGrabbing && weld != null) {
            Snakemeleon.uni.destroyJoint(weld);
            isGrabbing = false;
            Debug.print("Joint Destroyed");
            grabbingEntity = null;
        }

        if (!isGrabbing)
            self.setDirection(0);
    }
}
