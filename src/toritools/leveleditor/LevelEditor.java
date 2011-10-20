package toritools.leveleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import toritools.xml.ToriXMLParser;

public class LevelEditor {

	private final File BASE_DIR = new File("levels/level1");

	private HashMap<File, Document> objects = new HashMap<File, Document>();
	private HashMap<JButton, File> buttons = new HashMap<JButton, File>();
	private List<Entity> entities = new ArrayList<Entity>();

	private Entity current = null;

	private JPanel buttonPanel = new JPanel();
	private JFrame frame = new JFrame("ToriEditor");

	private Dimension gridSize = new Dimension(32, 32);

	private MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseClicked(MouseEvent arg0) {
			if (current != null) {
				Point p = (Point) arg0.getPoint().clone();
				p.setLocation((p.x / gridSize.width) * gridSize.width,
						(p.y / gridSize.height) * gridSize.height);

				Entity e = new Entity(current.getXml(), current.getImage(), p);
				entities.add(e);
			}
			frame.repaint();
		}
	};

	private JPanel panel = new JPanel() {
		{
			setPreferredSize(new Dimension(640, 480));
			this.addMouseListener(mouseAdapter);
		}

		public void paintComponent(Graphics g) {
			draw(g);
		}
	};

	public LevelEditor() throws IOException {
		setupXML();
		setupGUI();
		frame.repaint();
	}

	private void setupXML() throws IOException {
		// Create the essential level.xml file
		File f = new File(BASE_DIR + "level.xml");
		f.createNewFile();
	}

	private void setupGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(panel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.EAST);

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));

		/*
		 * Setup menu
		 */

		JMenuBar menuBar = new JMenuBar();

		/**
		 * FILE MENU
		 */

		JMenu fileMenu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveLevel();
				} catch (ParserConfigurationException | TransformerException e) {
					e.printStackTrace();
				}
			}
		});
		fileMenu.add(save);
		menuBar.add(fileMenu);

		// Entity Menu
		JMenu entityMenu = new JMenu("Entities");
		JMenuItem importXml = new JMenuItem("Import XML Entity");
		importXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				importNewObject();
			}
		});
		entityMenu.add(importXml);
		menuBar.add(entityMenu);

		/**
		 * SETTINGS MENU
		 */
		JMenu settingsMenu = new JMenu("Settings");
		JMenuItem gridMenu = new JMenuItem("Grid Size");
		gridMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int i = Integer.parseInt(JOptionPane
							.showInputDialog("Input an integer grid size:"));
					gridSize.setSize(new Dimension(i, i));
					frame.repaint();
				} catch (final Exception i) {
					return;
				}

			}
		});
		settingsMenu.add(gridMenu);
		menuBar.add(settingsMenu);

		/**
		 * HELP MENU
		 */
		JMenuItem item = new JMenuItem("Help");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null,
						"Everything is still in progress!\n ~tori");
			}
		});
		menuBar.add(item);

		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setVisible(true);
	}

	public void saveLevel() throws ParserConfigurationException,
			TransformerException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// Create level element
		Element levelElement = doc.createElement("level");
		doc.appendChild(levelElement);

		// Save the objects
		Element objectsElements = doc.createElement("objects");
		levelElement.appendChild(objectsElements);

		for (Entity e : entities) {
			Element object = doc.createElement("entity");

			object.setAttribute(
					"template",
					e.getXml()
							.getPath()
							.substring(
									e.getXml().getPath()
											.indexOf(BASE_DIR.getName())
											+ BASE_DIR.getName().length()));
			Element pos = doc.createElement("position");
			pos.setAttribute("x", e.getPos().getX() + "");
			pos.setAttribute("y", e.getPos().getY() + "");

			object.appendChild(pos);

			objectsElements.appendChild(object);

		}

		/*
		 * SAVE ALL THE THINGS
		 */
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		String xmlString = sw.toString();

		System.out.println(xmlString);

	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 1000);

		/*
		 * DRAW THE GRID
		 */
		if (gridSize.width > 1) {
			g.setColor(Color.BLACK);
			for (int x = 0; x < 1000; x += gridSize.width)
				g.drawLine(x, 0, x, 1000);

			for (int y = 0; y < 1000; y += gridSize.height)
				g.drawLine(0, y, 1000, y);
		}

		/**
		 * DRAW ENTITIES
		 */
		for (Entity e : entities)
			e.draw(g);
	}

	private void importNewObject() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"xml files", "xml"));
		fileChooser.setCurrentDirectory(BASE_DIR);
		int ret = fileChooser.showDialog(null, "Import an xml file");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			importXML(file);
		}
	}

	private void importXML(final File file) {
		Document doc = ToriXMLParser.parse(file);
		objects.put(file, doc);
		doc.getDocumentElement().normalize();

		String picture = doc.getElementsByTagName("editor").item(0)
				.getAttributes().getNamedItem("img").getNodeValue()
				+ "";

		final ImageIcon i = new ImageIcon(file.getPath().replace(
				file.getName(), "")
				+ picture);

		JButton b = new JButton(i);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setCurrent(new Entity(file, i.getImage(), new Point()));
			}
		});
		buttonPanel.add(b);
		frame.pack();
	}

	public void setCurrent(final Entity e) {
		current = e;
	}

	public static void main(String[] args) throws IOException {
		new LevelEditor();
	}

	private class Entity {
		private File xml;
		private Image image;
		private Point2D pos;

		public Entity(File xml, Image img, final Point2D pos) {
			this.xml = xml;
			this.image = img;
			this.pos = pos;
		}

		public File getXml() {
			return xml;
		}

		public void setXml(File xml) {
			this.xml = xml;
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public Point2D getPos() {
			return pos;
		}

		public void setPos(Point2D pos) {
			this.pos = pos;
		}

		public void draw(Graphics g) {
			g.drawImage(image, (int) pos.getX(), (int) pos.getY(), panel);
		}

	}

}
