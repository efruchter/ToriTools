package toritools.leveleditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BackgroundEditor extends JPanel {

	File imageFile;

	LevelEditor editor;

	public BackgroundEditor(final LevelEditor editor) {
		this.editor = editor;
	}

	public void paintComponent(Graphics g) {
		if (imageFile != null) {
			ImageIcon icon;
			g.drawImage((icon = new ImageIcon(imageFile.getPath())).getImage(),
					0, 0, null);
			setPreferredSize(new Dimension(icon.getIconWidth(),
					icon.getIconHeight()));
		}
	}

	public void setImageFile(final File file) {
		this.imageFile = file;
		repaint();
	}
}
