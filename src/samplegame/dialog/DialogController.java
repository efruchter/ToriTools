package samplegame.dialog;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import toritools.dialog.DialogNode;

public class DialogController {
	private List<DialogNode> dialogs = new ArrayList<DialogNode>();
	private DialogNode node;

	public void runDialog(final Graphics g) {
		if(!isDialogRunning()) {
			return;
		}
		node = node != null ? node : dialogs.remove(0);
		
	}

	public boolean isDialogRunning() {
		return !dialogs.isEmpty() || node != null;
	}

	public void queueDialog(final DialogNode dialogNode) {
		dialogs.add(dialogNode);
	}
}
