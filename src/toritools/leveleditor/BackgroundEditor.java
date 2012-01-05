package toritools.leveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import toritools.math.Vector2;

@SuppressWarnings("serial")
public class BackgroundEditor extends JPanel {

	private File imageFile;

	private Dimension grid = new Dimension(32, 32);

	private Vector2 current = new Vector2();

	public BackgroundEditor(final LevelEditor editor) {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent m) {
				current = LevelEditor.getClosestGridPoint(getGrid(),
						new Vector2(m.getPoint()));
				repaint();
			}
		});
	}

	public void paintComponent(Graphics g) {
		if (imageFile != null) {
			ImageIcon icon;
			g.drawImage((icon = new ImageIcon(imageFile.getPath())).getImage(),
					0, 0, null);
			setPreferredSize(new Dimension(icon.getIconWidth(),
					icon.getIconHeight()));

			g.setColor(Color.BLACK);
			// Draw grid
			for (int x = 0; x <= icon.getIconWidth(); x += grid.width)
				g.drawLine(x, 0, x, icon.getIconHeight());
			for (int y = 0; y <= icon.getIconHeight(); y += grid.height)
				g.drawLine(0, y, icon.getIconWidth(), y);

			// draw Selected
			g.setColor(Color.RED);
			g.draw3DRect((int) current.x, (int) current.y, grid.width,
					grid.height, true);
		}
	}

	public void setImageFile(final File file) {
		this.imageFile = file;
		repaint();
	}

	public void setupBg() {
		try {
			String result = JOptionPane
					.showInputDialog("Input an integer tile width, height (ex. 32, 64):");
			String vals[] = result.split(",");
			grid.width = Integer.parseInt(vals[0].trim());
			grid.height = Integer.parseInt(vals[1].trim());
			repaint();
		} catch (final Exception i) {
			return;
		}
	}

	private Dimension getGrid() {
		return grid;
	}
}
