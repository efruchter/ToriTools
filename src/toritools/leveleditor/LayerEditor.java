package toritools.leveleditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This frame lets teh user control what depths are visible and what the current
 * depth is.
 * 
 * @author toriscope
 * 
 */
public class LayerEditor extends JPanel {
	private JCheckBox[] layerBoxes;
	private int currentLayer = 0;

	private final int MAXLAYER = 10;

	public LayerEditor(final LevelEditor editor) {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editor.repaint();
			}
		};

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JComboBox<Integer> combo = new JComboBox<Integer>(new Integer[] { 0, 1,
				2, 3, 4, 5, 6, 7, 8, 9 });
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentLayer = (Integer) ((JComboBox) e.getSource())
						.getSelectedItem();
			}

		});
		
		combo.setToolTipText("Editing Layer");

		add(combo);

		add(new JLabel("Visible Layers"));

		layerBoxes = new JCheckBox[MAXLAYER];
		for (int i = 0; i < MAXLAYER; i++) {
			layerBoxes[i] = new JCheckBox("Layer " + i, i == 0);
			layerBoxes[i].addActionListener(action);
			add(layerBoxes[i]);
		}

	}

	public boolean isLayerVisible(final int layer) {
		if (layer < 0 || layer >= MAXLAYER)
			return false;
		else
			return layerBoxes[layer].isSelected();
	}

	public void setLayerVisibility(final int layer, final boolean visibility) {
		if (layer < 0 || layer >= MAXLAYER)
			return;
		else
			layerBoxes[layer].setSelected(visibility);
	}

	public int getCurrentLayer() {
		return currentLayer;
	}
}