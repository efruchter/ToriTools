package samplegame.controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 * Mechanism for detecting if a key is pressed.
 * 
 * @author toriscope
 * 
 */
public class KeyHolder implements KeyListener {
	private HashMap<Integer, Boolean> keyBox = new HashMap<Integer, Boolean>();

	public KeyHolder() {
	}

	public HashMap<Integer, Boolean> getKeyBox() {
		return keyBox;
	}

	public void setKeyBox(HashMap<Integer, Boolean> keyBox) {
		this.keyBox = keyBox;
	}

	/**
	 * Checks to see if a key is pressed.
	 * 
	 * @param key
	 *            the key to poll for.
	 * @return whether or not the key is being pressed.
	 */
	public boolean isPressed(int key) {
		if (!keyBox.containsKey(key))
			return false;
		return keyBox.get(key);
	}

	/**
	 * Imitates a normal keyPressed event, by returning the result of isPressed,
	 * and if the key is currently being held down, releases it.
	 * 
	 * @param key
	 *            the key to poll for.
	 * @return whether or not the key is being pressed.
	 */
	public boolean isPressedThenRelease(int key) {
		if (!keyBox.containsKey(key))
			return false;
		boolean k;
		if (k = keyBox.get(key))
			keyBox.put(key, false);
		return k;
	}

	/**
	 * Triggered immediately when key is simply pressed upon.
	 */
	@Override
	public void keyPressed(KeyEvent keyEvent) {
		keyBox.put(keyEvent.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		keyBox.put(keyEvent.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		// TODO maybe
	}
}