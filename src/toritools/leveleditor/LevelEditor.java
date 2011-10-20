package toritools.leveleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import toritools.xml.ToriXML;

/**
 * The core level editor. What a mess! :D
 * 
 * @author toriscope
 * 
 */
public class LevelEditor {

	/**
	 * The current level file being edited.
	 */
	private File levelFile, workingDirectory;

	/**
	 * Maps for getting the docs out of files. Files are the basic way to
	 * edentify unit type.
	 */
	private HashMap<File, Document> objects = new HashMap<File, Document>();

	/**
	 * Map of int layers to list of existing entities.
	 */
	private HashMap<Integer, ArrayList<Entity>> entities = new HashMap<Integer, ArrayList<Entity>>();

	/**
	 * The current placeable entity.
	 */
	private Entity current = null;

	/**
	 * The currently selected entity.
	 */
	private Entity selected = null;

	/**
	 * The panel where the entity selection buttons are added and removed.
	 */
	private JPanel buttonPanel = new JPanel();

	/**
	 * The root frame.
	 */
	private JFrame frame = new JFrame("ToriEditor");

	/**************
	 * EDITOR VARIABLES
	 **************/

	/**
	 * Size of the grid. Usually square for now.
	 */
	private Dimension gridSize = new Dimension(32, 32);

	/**
	 * This object handles layering information.
	 */
	private LayerEditor layerEditor = new LayerEditor(this);

	/**
	 * The text of this is where you can set some neat data to display to the
	 * user.
	 */
	private JMenuItem statusBar = new JMenuItem("Hello!");

	/**
	 * Mouse controller.
	 */
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseClicked(MouseEvent arg0) {
			frame.requestFocus();
			if (arg0.getButton() == MouseEvent.BUTTON3) {
				selectOverlapping(arg0.getPoint());
			} else if (arg0.getButton() == MouseEvent.BUTTON1) {
				if (current != null) {
					Point p = (Point) arg0.getPoint().clone();
					deleteOverlapping(p);
					p.setLocation((p.x / gridSize.width) * gridSize.width,
							(p.y / gridSize.height) * gridSize.height);
					Entity e = new Entity(current.getXml(), current.getImage(),
							p, current.getDim());
					addEntity(e, layerEditor.getCurrentLayer());
				}
			}
			frame.repaint();
		}
	};

	/**
	 * This JPanel is where the actual drawing take splace.
	 */
	@SuppressWarnings("serial")
	private JPanel drawPanel = new JPanel() {
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
		/*
		 * LOAD THE CONFIG FILE.
		 */
		Node configNode = ToriXML.parse(new File("config.xml"))
				.getElementsByTagName("config").item(0);
		Node recentNode = configNode.getAttributes().getNamedItem("recent");
		if (recentNode != null) {
			File f = new File(recentNode.getNodeValue());
			try {
				if (!f.exists())
					throw new NullPointerException();
				setLevelFile(f);
			} catch (final Exception NullPointer) {
				JOptionPane.showMessageDialog(null,
						"There was an issue loading the recent level file!");
				setLevelFile(importNewFileDialog());
			}
		}

		setupGUI();
		reloadLevel();

		/*
		 * Add the keyboard handler.
		 */
		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteSelected();
				}
			}
		});

		frame.repaint();
	}

	/**
	 * Sets the working level file and the working directory.
	 * 
	 * @param file
	 *            the level xml file.
	 */
	private void setLevelFile(final File file) {
		this.levelFile = file;
		this.workingDirectory = file.getParentFile();
	}

	/**
	 * If a level.xml file exists, load the data from it. Uses levelFile.
	 * 
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private void reloadLevel() throws IOException,
			ParserConfigurationException, TransformerException {
		// Create the essential level.xml file
		clear();
		if (levelFile.exists()) {
			Document doc = ToriXML.parse(levelFile);
			loadLevel(doc);
		} else {
			saveLevel();
		}
		statusBar.setText(levelFile.getName());
		saveConfig();
	}

	/**
	 * Save the config file.
	 * 
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	private void saveConfig() throws ParserConfigurationException,
			TransformerException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// Create level element
		Element editorElement = doc.createElement("editor");
		doc.appendChild(editorElement);

		Element configElement = doc.createElement("config");
		editorElement.appendChild(configElement);

		configElement.setAttribute("recent", levelFile.getPath());

		ToriXML.saveXMLDoc(new File("config.xml"), doc);
	}

	/**
	 * Clear the current state.
	 */
	private void clear() {
		objects.clear();
		entities.clear();
		current = null;
		selected = null;
		buttonPanel.removeAll();
		layerEditor.clear();
	}

	/**
	 * Create the GUI components and menu.
	 */
	private void setupGUI() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setFocusable(true);

		JPanel dummyPanel = new JPanel();
		frame.add(new JScrollPane(drawPanel), BorderLayout.CENTER);
		frame.add(new JScrollPane(buttonPanel), BorderLayout.EAST);
		dummyPanel.add(layerEditor);
		frame.add(new JScrollPane(dummyPanel), BorderLayout.WEST);

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

		/*
		 * Setup menu
		 */

		JMenuBar menuBar = new JMenuBar();

		/**
		 * FILE MENU
		 */

		JMenu fileMenu = new JMenu("File");
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File f = importNewFileDialog();
				if (f != null) {
					setLevelFile(f);
					try {
						reloadLevel();
					} catch (IOException | ParserConfigurationException
							| TransformerException e) {
						e.printStackTrace();
					}
				}
			}
		});
		fileMenu.add(open);
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveLevel();
					JOptionPane.showMessageDialog(null, "Level Saved!");
				} catch (ParserConfigurationException | TransformerException e) {
					e.printStackTrace();
				}
			}
		});
		fileMenu.add(save);
		JMenuItem close = new JMenuItem("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(close);
		menuBar.add(fileMenu);

		// Entity Menu
		JMenu entityMenu = new JMenu("Entities");
		JMenuItem importXml = new JMenuItem("Import XML Entity");
		importXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File f = importNewFileDialog();
				if (f != null)
					importXML(f);
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
				JOptionPane
						.showMessageDialog(
								null,
								"Everything is still in progress!\n ~tori"
										+ "\n\nLeft click to place the latest selected object!\nRight click to select!\nDELETE to delete selected object!");
			}
		});
		menuBar.add(item);
		menuBar.add(statusBar);

		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Make the first detected overlapping, visible object, selected.
	 * 
	 * @param p
	 *            the mouse location.
	 */
	public void selectOverlapping(final Point p) {
		Entity selected = null;
		for (Entry<Integer, ArrayList<Entity>> entry : entities.entrySet())
			if (layerEditor.isLayerVisible(entry.getKey()))
				for (Entity e : entry.getValue()) {
					if (new Rectangle((int) e.getPos().getX(), (int) e.getPos()
							.getY(), (int) e.getDim().getX(), (int) e.getDim()
							.getY()).contains(p)) {
						selected = e;
						break;
					}
				}
		this.selected = selected;
	}

	/**
	 * Delete the entity that is currently selected.
	 */
	public void deleteSelected() {
		if (selected != null) {
			removeEntity(selected);
		}
	}

	/**
	 * Delete the first object detected overlapping.
	 * 
	 * @param p
	 *            the mouse point.
	 */
	public void deleteOverlapping(final Point p) {
		Entity deleteMe = null;
		for (Entry<Integer, ArrayList<Entity>> entry : entities.entrySet())
			if (layerEditor.getCurrentLayer() == entry.getKey())
				for (Entity e : entry.getValue()) {
					if (new Rectangle((int) e.getPos().getX(), (int) e.getPos()
							.getY(), (int) e.getDim().getX(), (int) e.getDim()
							.getY()).contains(p)) {
						deleteMe = e;
						break;
					}
				}
		removeEntity(deleteMe);
	}

	/**
	 * Add an entity, and give it to a layer.
	 * 
	 * @param e
	 *            the entity
	 * @param layer
	 *            the layer/depth.
	 */
	private void addEntity(final Entity e, final int layer) {
		if (!entities.containsKey(layer)) {
			entities.put(layer, new ArrayList<Entity>());
		}
		entities.get(layer).add(e);
		frame.repaint();
	}

	/**
	 * Remove an entity from all layers.
	 * 
	 * @param e
	 *            entity to remove.
	 */
	public void removeEntity(final Entity e) {
		for (Entry<Integer, ArrayList<Entity>> entry : entities.entrySet())
			if (layerEditor.isLayerVisible(entry.getKey()))
				entry.getValue().remove(e);
		frame.repaint();
	}

	/**
	 * Remove entity from one layer and add to another.
	 * 
	 * @param e
	 *            entity to move.
	 * @param layer
	 *            layer to add to.
	 */
	@SuppressWarnings("unused")
	private void transferEntity(final Entity e, final int layer) {
		removeEntity(e);
		addEntity(e, layer);
		frame.repaint();
	}

	/**
	 * Save level.xml.
	 * 
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
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
		for (Entry<Integer, ArrayList<Entity>> entry : entities.entrySet())
			for (Entity e : entry.getValue()) {
				Element object = doc.createElement("entity");
				object.setAttribute(
						"template",
						e.getXml()
								.getPath()
								.substring(
										e.getXml()
												.getPath()
												.indexOf(
														workingDirectory
																.getName())
												+ workingDirectory.getName()
														.length()));
				object.setAttribute("layer", entry.getKey() + "");

				Element pos = doc.createElement("position");
				pos.setAttribute("x", e.getPos().getX() + "");
				pos.setAttribute("y", e.getPos().getY() + "");

				object.appendChild(pos);

				objectsElements.appendChild(object);

			}

		ToriXML.saveXMLDoc(levelFile, doc);
	}

	/**
	 * Load the entity data from level.xml.
	 * 
	 * @param doc
	 *            the doc of level.xml.
	 */
	private void loadLevel(final Document doc) {
		NodeList objects = doc.getElementsByTagName("entity");
		for (int i = 0; i < objects.getLength(); i++) {
			Node node = objects.item(i);
			String s = node.getAttributes().getNamedItem("template")
					.getNodeValue();
			Entity e = importXML(new File(workingDirectory + s));

			/**
			 * Special param data.
			 */
			int depth = 0;
			for (int ii = 0; ii < node.getChildNodes().getLength(); ii++) {
				Node param = node.getChildNodes().item(ii);
				if (param.getNodeName().equals("position")) {
					Double x = Double.parseDouble(param.getAttributes()
							.getNamedItem("x").getNodeValue());
					Double y = Double.parseDouble(param.getAttributes()
							.getNamedItem("y").getNodeValue());
					e.setPos(new Point.Double(x, y));
				}
			}
			if (node.getAttributes().getNamedItem("layer") != null) {
				depth = Integer.parseInt(node.getAttributes()
						.getNamedItem("layer").getNodeValue());
				layerEditor.setLayerVisibility(depth, true);
			}
			addEntity(e, depth);
		}
	}

	/**
	 * Bring up a file picker to import a new item.
	 */
	private File importNewFileDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
				"xml files", "xml"));
		fileChooser.setCurrentDirectory(workingDirectory);
		int ret = fileChooser.showDialog(null, "Import an xml file");
		if (ret == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * Fully import and add an xml entity. if the entity is a new type, it will
	 * be given a new button and added to button panel.
	 * 
	 * @param file
	 *            the file of the xml.
	 * @return the generated entity.
	 */
	private Entity importXML(final File file) {
		Document doc = ToriXML.parse(file);
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
		final Entity e = new Entity(file, i.getImage(), new Point.Double(),
				new Point.Double(x, y));
		if (!objects.containsKey(file)) {
			JButton b = new JButton(i);
			b.setToolTipText(doc.getElementsByTagName("description").item(0)
					.getChildNodes().item(0).getNodeValue());
			b.setSize(new Dimension((int) x, (int) y));
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setCurrent(e);
				}
			});
			buttonPanel.add(b);
			frame.pack();
			objects.put(file, doc);
		}
		return e;
	}

	/**
	 * Draw the state of the level.
	 * 
	 * @param g
	 *            graphics!
	 */
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000, 1000);

		/*
		 * DRAW THE GRID
		 */
		if (gridSize.width > 1) {
			g.setColor(Color.BLACK);
			for (int x = 0; x < drawPanel.getWidth(); x += gridSize.width)
				g.drawLine(x, 0, x, drawPanel.getHeight());

			for (int y = 0; y < drawPanel.getHeight(); y += gridSize.height)
				g.drawLine(0, y, drawPanel.getWidth(), y);
		}

		/**
		 * DRAW ENTITIES in layer order.
		 */
		List<Entry<Integer, ArrayList<Entity>>> list = new ArrayList<Entry<Integer, ArrayList<Entity>>>(
				entities.entrySet());
		Collections.sort(list,
				new Comparator<Entry<Integer, ArrayList<Entity>>>() {
					@Override
					public int compare(Entry<Integer, ArrayList<Entity>> arg0,
							Entry<Integer, ArrayList<Entity>> arg1) {
						return arg0.getKey().compareTo(arg1.getKey()) * -1;
					}
				});
		for (Entry<Integer, ArrayList<Entity>> entry : list)
			if (layerEditor.isLayerVisible(entry.getKey()))
				for (Entity e : entry.getValue()) {
					e.draw(g);
					if (selected == e) {
						g.setColor(Color.RED);
						g.drawRect((int) e.getPos().getX(), (int) e.getPos()
								.getY(), (int) e.getDim().getX(), (int) e
								.getDim().getY());
					}
				}
	}

	public void setCurrent(final Entity e) {
		current = e;
	}

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerException {
		new LevelEditor();
	}

	public void repaint() {
		frame.repaint();
	}

}
