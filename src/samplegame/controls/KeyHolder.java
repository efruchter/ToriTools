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
	private HashMap<Integer, Boolean> toggleableKeys = new HashMap<Integer, Boolean>();
	
	/**
	 * All toggleable keys must be initially set to true.  Otherwise, the inceptive press does not register.
	 */
	public KeyHolder()
	{
		toggleableKeys.put(KeyEvent.VK_0, true);
		toggleableKeys.put(KeyEvent.VK_L , true);
	}

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

	/**
	 * Triggered immediately when key is simply pressed upon. 
	 */
	@Override
	public void keyPressed(KeyEvent keyEvent) {
		if (!toggleableKeys.containsKey(keyEvent.getKeyCode()))
		{
			keyBox.put(keyEvent.getKeyCode(), true);
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		if (!toggleableKeys.containsKey(keyEvent.getKeyCode()))
		{
			keyBox.put(keyEvent.getKeyCode(), false);
		}
		else
		{
			keyBox.put(keyEvent.getKeyCode(), toggleableKeys.get(keyEvent.getKeyCode()));
			toggleableKeys.put(keyEvent.getKeyCode(), !toggleableKeys.get(keyEvent.getKeyCode()));
		}
	}
	
	
	@Override
	public void keyTyped(KeyEvent keyEvent)
	{
		//TODO maybe
	}
}