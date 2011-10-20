package toritools.leveleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import toritools.xml.ToriXMLParser;

/**
 * OH GOD IT IS SUCH A MESS.... It was hacked together quickly and with great
 * urgency.... feel free to REFACTOR!
 * 
 * @author toriscope
 * 
 */
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
			if (arg0.getButton() == MouseEvent.BUTTON3) {
				deleteOverlapping(arg0.getPoint());
			} else if(arg0.getButton() == MouseEvent.BUTTON1) {
				if (current != null) {
					Point p = (Point) arg0.getPoint().clone();
					deleteOverlapping(p);
					p.setLocation((p.x / gridSize.width) * gridSize.width,
							(p.y / gridSize.height) * gridSize.height);
					Entity e = new Entity(current.getXml(), current.getImage(),
							p, current.getDim());
					entities.add(e);
				}
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

	public LevelEditor() throws IOException, ParserConfigurationException,
			TransformerException {
		setupXML();
		setupGUI();
		frame.repaint();
	}

	private void setupXML() throws IOException, ParserConfigurationException,
			TransformerException {
		// Create the essential level.xml file
		File f = new File(BASE_DIR + "/level.xml");
		if (f.exists()) {
			Document doc = ToriXMLParser.parse(f);
			loadLevel(doc);
		} else {
			saveLevel();
		}

	}

	private void loadLevel(final Document doc) {
		NodeList objects = doc.getElementsByTagName("entity");
		for (int i = 0; i < objects.getLength(); i++) {
			Node node = objects.item(i);
			String s = node.getAttributes().getNamedItem("template")
					.getNodeValue();
			importXML(new File(BASE_DIR + s));

			/**
			 * Special param data.
			 */
			for (int ii = 0; ii < node.getChildNodes().getLength(); ii++) {
				Node param = node.getChildNodes().item(ii);
				if (param.getNodeName().equals("position")) {
					Double x = Double.parseDouble(param.getAttributes()
							.getNamedItem("x").getNodeValue());
					Double y = Double.parseDouble(param.getAttributes()
							.getNamedItem("y").getNodeValue());
					entities.add(new Entity(current.getXml(), current
							.getImage(), new Point.Double(x, y), current
							.getDim()));
				}
			}
		}
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
		JMenuItem deleteAll = new JMenuItem("Delete All");
		deleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				entities.clear();
				frame.repaint();
			}
		});
		entityMenu.add(deleteAll);
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
						"Everything is still in progress!\n ~tori" +
						"\n\nLeft click to place\nRight click to delete!");
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

		try {
			FileWriter f = new FileWriter(BASE_DIR + "/level.xml");
			f.write(xmlString);
			f.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 1000);

		/*
		 * DRAW THE GRID
		 */
		if (gridSize.width > 1) {
			g.setColor(Color.BLACK);
			for (int x = 0; x < panel.getWidth(); x += gridSize.width)
				g.drawLine(x, 0, x, panel.getHeight());

			for (int y = 0; y < panel.getHeight(); y += gridSize.height)
				g.drawLine(0, y, panel.getWidth(), y);
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
		if (objects.containsKey(file)) {
			return;
		}
		Document doc = ToriXMLParser.parse(file);
		objects.put(file, doc);
		doc.getDocumentElement().normalize();

		// get the editor image
		String picture = doc.getElementsByTagName("editor").item(0)
				.getAttributes().getNamedItem("img").getNodeValue()
				+ "";
		// Get the dimensions
		NamedNodeMap pos = doc.getElementsByTagName("dimensions").item(0)
				.getAttributes();

		double x = Double.parseDouble(pos.getNamedItem("x").getNodeValue());
		double y = Double.parseDouble(pos.getNamedItem("y").getNodeValue());

		// Form the image
		final ImageIcon i = new ImageIcon(file.getPath().replace(
				file.getName(), "")
				+ picture);
		i.setImage(i.getImage().getScaledInstance((int) x, (int) y, 0));

		JButton b = new JButton(i);
		b.setSize(new Dimension((int) x, (int) y));
		final Entity e = new Entity(file, i.getImage(), new Point.Double(),
				new Point.Double(x, y));
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setCurrent(e);
			}
		});
		setCurrent(e);
		buttonPanel.add(b);
		frame.pack();

		objects.put(file, doc);
		buttons.put(b, file);
	}

	public void setCurrent(final Entity e) {
		current = e;
	}

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerException {
		new LevelEditor();
	}

	public void deleteOverlapping(final Point p) {
		Entity deleteMe = null;
		for (Entity e : entities) {
			if (new Rectangle((int) e.getPos().getX(), (int) e.getPos().getY(),
					(int) e.getDim().getX(), (int) e.getDim().getY())
					.contains(p)) {
				deleteMe = e;
				break;
			}
		}
		entities.remove(deleteMe);
	}

	private class Entity {
		private File xml;
		private Image image;
		private Point2D pos;
		private Point2D dim;

		public Entity(File xml, Image img, final Point2D pos, final Point2D dim) {
			this.xml = xml;
			this.image = img;
			this.pos = pos;
			this.dim = dim;
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
			g.drawImage(image, (int) pos.getX(), (int) pos.getY(),
					(int) dim.getX(), (int) dim.getY(), panel);
		}

		public Point2D getDim() {
			return dim;
		}

		public void setDim(Point2D dim) {
			this.dim = dim;
		}

	}

}
