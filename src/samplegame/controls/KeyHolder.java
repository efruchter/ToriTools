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

	public HashMap<Integer, Boolean> getKeyBox() {
		return keyBox;
	}

	public void setKeyBox(HashMap<Integer, Boolean> keyBox) {
		this.keyBox = keyBox;
	}

	public boolean isPressed(int key) {
		if (!keyBox.containsKey(key))
			return false;
		return keyBox.get(key);
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		keyBox.put(keyEvent.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		keyBox.put(keyEvent.getKeyCode(), false);

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}