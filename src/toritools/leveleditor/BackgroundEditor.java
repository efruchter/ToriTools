package toritools.leveleditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import toritools.entity.Entity;
import toritools.io.Importer;
import toritools.math.Vector2;

@SuppressWarnings("serial")
public class BackgroundEditor extends JPanel {

	private File imageFile;

	private Dimension grid = new Dimension(32, 32);

	private Vector2 current = new Vector2(), imageDim = new Vector2();

	private LevelEditor editor;

	private JFrame frame;

	public BackgroundEditor(final LevelEditor editor) {
		this.editor = editor;
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent m) {
				current = LevelEditor.getClosestGridPoint(getGrid(),
						new Vector2(m.getPoint()));
				repaint();
			}
		});

		frame = new JFrame("Background Tile Selector");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JScrollPane(this));
	}

	public void paintComponent(Graphics g) {
		if (imageFile != null) {
			ImageIcon icon;
			g.drawImage((icon = new ImageIcon(imageFile.getPath())).getImage(),
					0, 0, null);
			setPreferredSize(new Dimension(icon.getIconWidth(),
					icon.getIconHeight()));

			imageDim = new Vector2(icon.getIconWidth(), icon.getIconHeight());

			g.setColor(Color.BLACK);
			// Draw grid
			for (int x = 0; x <= imageDim.x; x += grid.width)
				g.drawLine(x, 0, x, icon.getIconHeight());
			for (int y = 0; y <= imageDim.y; y += grid.height)
				g.drawLine(0, y, icon.getIconWidth(), y);

			// draw Selected
			g.setColor(Color.RED);
			g.draw3DRect((int) current.x, (int) current.y, grid.width,
					grid.height, true);
		}
	}

	public void setImageFile(final File file) {
		this.imageFile = file;
		ImageIcon icon = new ImageIcon(imageFile.getPath());
		setPreferredSize(new Dimension(icon.getIconWidth(),
				icon.getIconHeight()));
		frame.pack();
		frame.setVisible(true);
		repaint();
	}

	public void setupBg() {
		try {
			String result = JOptionPane
					.showInputDialog("Input an integer tile width, height (ex. 32, 64):");
			String vals[] = result.split(",");
			int width = Integer.parseInt(vals[0].trim());
			int height = vals.length == 2 ? Integer.parseInt(vals[1].trim())
					: width;

			if (imageDim.x % width != 0 || imageDim.y % height != 0) {
				JOptionPane.showMessageDialog(null,
						"The tile size must divide the image cleanly.");
			} else {
				grid.setSize(width, height);
			}
			repaint();
		} catch (final Exception i) {
			return;
		}
	}

	private Dimension getGrid() {
		return grid;
	}

	public Entity makeEntity(final Vector2 pos) {
		if (imageFile == null)
			return null;
		String relativeLink = imageFile.getPath().split(
				editor.workingDirectory.getPath())[1];

		Entity bg = Importer.makeBackground(pos, new Vector2(grid.width,
				grid.height), new ImageIcon(imageFile.getPath()).getImage(),
				relativeLink, (int) (current.x / grid.width),
				(int) (current.y / grid.height),
				(int) (imageDim.x / grid.width),
				(int) (imageDim.y / grid.height));
		return bg;
	}
}
