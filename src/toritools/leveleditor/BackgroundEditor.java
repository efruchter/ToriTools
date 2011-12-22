package toritools.leveleditor;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import toritools.entity.sprite.Sprite;

@SuppressWarnings("serial")
public class BackgroundEditor extends JPanel {
	Sprite currentImage = new Sprite(null, 1, 1);

	public BackgroundEditor() {

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				selectTile(arg0.getPoint());
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void selectTile(final Point p) {

	}

	public void paintComponent(Graphics g) {

	}
}
