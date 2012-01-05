package toritools.leveleditor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.io.Importer;
import toritools.map.ToriMapIO;
import toritools.math.Vector2;
import toritools.xml.ToriXML;

/**
 * The core level editor. What a mess! :D
 * 
 * @author toriscope
 * 
 */
public class LevelEditor {

	private final String configFile = "editor.conf";

	/**
	 * The current level file being edited.
	 */
	private File levelFile;

	File workingDirectory;

	/**
	 * Map of int layers to list of existing entities.
	 */
	private List<Entity> entities = new ArrayList<Entity>();

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
	 * Size of the editing world.
	 */
	private Dimension levelSize = new Dimension(1000, 1000);

	/**
	 * This object handles layering information.
	 */
	private LayerEditor layerEditor = new LayerEditor(this);

	/**
	 * This object handles instance variables.
	 */
	private VariableEditor varEditor = new VariableEditor(this);

	/**
	 * This object handles instance variables.
	 */
	private BackgroundEditor bgEditor = new BackgroundEditor(this);

	/**
	 * The text of this is where you can set some neat data to display to the
	 * user.
	 */
	private JLabel fileLabel = new JLabel("FILE LABEL");
	private JLabel gridLabel = new JLabel("GRID LABEL");
	private JLabel levelSizeLabel = new JLabel("WORLD SIZE LABEL");
	private JLabel editModeLabel = new JLabel("EDITMODE");

	private Vector2 wallStart, wallEnd;
	private Entity moving;

	private enum Mode {
		WALL_QUEUE, WALL_MAKING, MOVING, PLACE, BG
	}

	private Mode mode = Mode.PLACE;

	/**
	 * Mouse controller.
	 */
	private MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent m) {
			if (mode == Mode.WALL_QUEUE) {
				wallStart = wallEnd = getClosestGridPoint(new Vector2(
						m.getPoint()));
				mode = Mode.WALL_MAKING;
			} else if (mode == Mode.BG) {
				if (m.getButton() == MouseEvent.BUTTON3) {
					for (Entity e : getOverlapping(new Vector2(m.getPoint()))) {
						if ("BACKGROUND".equals(e.type)) {
							entities.remove(e);
						}
						repaint();
					}

				} else {
					Entity bg = bgEditor
							.makeEntity(getClosestGridPoint(new Vector2(m
									.getPoint())));
					if (bg != null) {
						bg.layer = layerEditor.getCurrentLayer();
						addEntity(bg);
					}
				}
			} else {
				if (m.getButton() == MouseEvent.BUTTON1)
					for (Entity ent : entities) {
						if (new Rectangle.Float(ent.pos.x, ent.pos.y,
								ent.dim.x, ent.dim.y).contains(m.getPoint())) {
							moving = ent;
							break;
						}
					}
			}
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent m) {
			if (mode == Mode.WALL_MAKING) {
				wallEnd = getClosestGridPoint(new Vector2(m.getPoint()));
				if (wallEnd.x < wallStart.x) {
					float temp = wallStart.x;
					wallStart.x = wallEnd.x;
					wallEnd.x = temp;
				}
				if (wallEnd.y < wallStart.y) {
					float temp = wallStart.y;
					wallStart.y = wallEnd.y;
					wallEnd.y = temp;
				}
			} else if (moving != null) {
				moving.pos = getClosestGridPoint(new Vector2(m.getPoint()));
			}
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent m) {
			if (mode == Mode.WALL_MAKING) {
				mouseDragged(m);
				Vector2 wallDim = wallEnd.sub(wallStart);
				if (wallDim.x != 0 && wallDim.y != 0)
					addEntity(Importer.makeWall(wallStart,
							wallEnd.sub(wallStart)));
				mode = Mode.PLACE;
			} else if (moving != null) {
				mouseDragged(m);
				moving = null;
			}
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			frame.requestFocus();
			if (mode != Mode.BG) {
				mode = Mode.PLACE;
				if (arg0.getButton() == MouseEvent.BUTTON3) {
					selectOverlapping(new Vector2(arg0.getPoint()));
					varEditor.setEntity(selected);
					repaint();
				} else if (arg0.getButton() == MouseEvent.BUTTON1) {
					System.err.println("Click");
					if (current != null) {
						Vector2 p = new Vector2(arg0.getPoint());
						deleteOverlapping(p);
						p.set(getClosestGridPoint(p));
						Entity e = new Entity();
						e.setFile(current.getFile());
						e.pos = p.clone();
						e.dim = current.dim.clone();
						e.sprite = current.sprite;
						e.layer = layerEditor.getCurrentLayer();
						addEntity(e);
					}
				}
			}
			repaint();
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
			this.addMouseMotionListener(mouseAdapter);

			this.setFocusable(true);
		}

		public void paintComponent(Graphics g) {
			draw(g);
		}
	};

	public LevelEditor() throws IOException, ParserConfigurationException,
			TransformerException {
		/*
		 * LOAD THE CONFIG FILE.
		 */try {
			Node configNode = ToriXML.parse(new File(configFile))
					.getElementsByTagName("config").item(0);
			Node recentNode = configNode.getAttributes().getNamedItem("recent");
			if (recentNode != null) {
				File f;
				if ((f = new File(recentNode.getNodeValue())).exists())
					setLevelFile(f);
			}
		} catch (final Exception NullPointer) {
			// Do nothing
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

		repaint();
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

		if (levelFile == null)
			return;

		if (levelFile.exists()) {
			Level level = Importer.importLevel(levelFile);
			levelSize.setSize(level.dim.x, level.dim.y);
			entities.clear();
			layerEditor.clear();
			for (Entity e : level.newEntities) {
				if (e.file != null && e.file.canRead()) {
					importEntity(e.file);
				}
				entities.add(e);
				layerEditor.setLayerVisibility(e.layer, true);
			}
		} else {
			saveLevel();
		}
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

		ToriXML.saveXMLDoc(new File(configFile), doc);
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
		dummyPanel.setLayout(new BoxLayout(dummyPanel, BoxLayout.Y_AXIS));
		dummyPanel.add(layerEditor);
		dummyPanel.add(varEditor);
		frame.add(dummyPanel, BorderLayout.WEST);

		/*
		 * Form the status bar
		 */
		dummyPanel = new JPanel();
		dummyPanel.setBackground(Color.WHITE);
		dummyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		dummyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		dummyPanel.add(fileLabel);
		dummyPanel.add(new JLabel("|"));
		dummyPanel.add(gridLabel);
		dummyPanel.add(new JLabel("|"));
		dummyPanel.add(levelSizeLabel);
		dummyPanel.add(new JLabel("|"));
		dummyPanel.add(editModeLabel);

		frame.add(dummyPanel, BorderLayout.NORTH);

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

		/*
		 * Setup menu
		 */

		JMenuBar menuBar = new JMenuBar();

		/**
		 * FILE MENU
		 */

		JMenu fileMenu = new JMenu("File");
		JMenuItem newLevel = new JMenuItem("New");
		newLevel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				Event.CTRL_MASK));
		newLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String levelName;
				if (levelFile != null)
					levelName = levelFile.getName().substring(0,
							levelFile.getName().lastIndexOf("."));
				else
					levelName = "new level";
				int ret = JOptionPane.showConfirmDialog(null,
						"Do you want to save changes you made to " + levelName
								+ "?");
				if (ret == JOptionPane.YES_OPTION) {
					try {
						if (!saveLevel())
							return;
						else
							saveConfig();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (ret == JOptionPane.CANCEL_OPTION) {
					return;
				}
				clear();
				levelFile = null;
				repaint();
			}
		});
		fileMenu.add(newLevel);

		JMenuItem open = new JMenuItem("Open");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				Event.CTRL_MASK));
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File[] files = importNewFileDialog("Open Level File",
						"XML file (*.xml)", "xml");
				if (files.length != 0) {
					setLevelFile(files[0]);
					try {
						reloadLevel();
						repaint();
					} catch (Exception e) {
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
					if (saveLevel())
						JOptionPane.showMessageDialog(null, "Level Saved!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Event.CTRL_MASK));
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

		JMenuItem makeWallEntry = new JMenuItem("Make Wall");
		makeWallEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mode = Mode.WALL_QUEUE;
				selected = null;
				repaint();
			}
		});
		entityMenu.add(makeWallEntry);
		makeWallEntry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				Event.CTRL_MASK));

		JMenuItem importXml = new JMenuItem("Import Entity Template");
		importXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (File f : importNewFileDialog("Load New Entity Template",
						"Entity files (*.entity)", "entity")) {
					if (f != null) {
						try {
							importEntity(f);
							repaint();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		entityMenu.add(importXml);
		importXml.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				Event.CTRL_MASK));

		JMenuItem deleteAll = new JMenuItem("Delete All");
		deleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				entities.clear();
				repaint();
			}
		});
		entityMenu.add(deleteAll);

		JMenuItem deleteType = new JMenuItem("Delete All By Type");
		deleteType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String type = JOptionPane.showInputDialog("Type to Delete:");
				if (type == null || type.isEmpty()) {
					return;
				}
				List<Entity> trash = new ArrayList<Entity>();
				for (Entity e : entities) {
					if (type.equals(e.type)) {
						trash.add(e);
					}
				}
				entities.removeAll(trash);
				repaint();
			}
		});
		entityMenu.add(deleteType);

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
					repaint();
				} catch (final Exception i) {
					return;
				}
			}
		});
		settingsMenu.add(gridMenu);
		JMenuItem levelSizeItem = new JMenuItem("Level Size");
		levelSizeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String result = JOptionPane
							.showInputDialog("Input an integer world width, height (ex. 1000, 5000):");
					String vals[] = result.split(",");
					levelSize.width = Integer.parseInt(vals[0].trim());
					levelSize.height = Integer.parseInt(vals[1].trim());
					repaint();
				} catch (final Exception i) {
					return;
				}
			}
		});
		settingsMenu.add(levelSizeItem);
		menuBar.add(settingsMenu);

		JMenu layerMenu = new JMenu("Layer");
		JMenuItem moveLayer = new JMenuItem("Move Selected to Layer");
		moveLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (selected != null) {
						int i = Integer.parseInt(JOptionPane
								.showInputDialog("Move to which layer?"));
						transferEntity(selected, i);
						repaint();
					}

					varEditor.clear();

					repaint();
				} catch (final Exception i) {
					return;
				}
			}
		});
		layerMenu.add(moveLayer);

		JMenuItem moveAllLayer = new JMenuItem("Move Type to Layer");
		moveAllLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String type = JOptionPane
							.showInputDialog("Type to Change Layer of?");
					if (type == null || type.isEmpty()) {
						return;
					}
					int layer = Integer.parseInt(JOptionPane
							.showInputDialog("Move to which layer?"));
					List<Entity> switchs = new ArrayList<Entity>();
					for (Entity e : entities) {
						if (type.equals(e.type)) {
							switchs.add(e);
						}
					}
					for (Entity e : switchs) {
						transferEntity(e, layer);
					}

					varEditor.clear();

					repaint();
				} catch (final Exception i) {
					return;
				}
			}
		});
		layerMenu.add(moveAllLayer);

		menuBar.add(layerMenu);

		JMenu bgMenu = new JMenu("Background");
		JMenuItem selectBg = new JMenuItem("Select New BG");
		selectBg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(workingDirectory);
				int ret = fileChooser.showDialog(null, "Select Image File");
				if (ret == JFileChooser.APPROVE_OPTION) {
					bgEditor.setImageFile(fileChooser.getSelectedFile());
					bgEditor.setupBg();
					System.out.println("Found image "
							+ fileChooser.getSelectedFile().getPath());
				}
			}
		});
		bgMenu.add(selectBg);

		JMenuItem setupBg = new JMenuItem("Edit BG Settings");
		setupBg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				bgEditor.setupBg();
			}
		});
		bgMenu.add(setupBg);

		JMenuItem placeBg = new JMenuItem("Toggle Place BG Mode");
		placeBg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mode = mode == Mode.BG ? Mode.PLACE : Mode.BG;
				selected = null;
				repaint();
			}
		});
		bgMenu.add(placeBg);
		placeBg.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				Event.CTRL_MASK));

		menuBar.add(bgMenu);

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
	public void selectOverlapping(final Vector2 p) {
		Entity selected = null;
		for (Entity e : entities) {
			if (new Rectangle((int) e.pos.getX(), (int) e.pos.getY(),
					(int) e.dim.getX(), (int) e.dim.getY()).contains(new Point(
					(int) p.x, (int) p.y))
					&& this.selected != e) {
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
			selected = null;
		}
	}

	/**
	 * Delete the first object detected overlapping.
	 * 
	 * @param p
	 *            the mouse point.
	 */
	public void deleteOverlapping(final Vector2 p) {
		Entity selected = null;
		for (Entity e : entities) {
			if (e.layer == layerEditor.getCurrentLayer()
					&& new Rectangle((int) e.pos.getX(), (int) e.pos.getY(),
							(int) e.dim.getX(), (int) e.dim.getY())
							.contains(new Point((int) p.x, (int) p.y))
					&& this.selected != e) {
				selected = e;
				break;
			}
		}
		removeEntity(selected);
	}

	/**
	 * Return all overlapping entities
	 * 
	 * @param p
	 *            the mouse point.
	 */
	public List<Entity> getOverlapping(final Vector2 p) {
		List<Entity> ents = new ArrayList<Entity>();
		for (Entity e : entities) {
			if (e.layer == layerEditor.getCurrentLayer()
					&& new Rectangle((int) e.pos.getX(), (int) e.pos.getY(),
							(int) e.dim.getX(), (int) e.dim.getY())
							.contains(new Point((int) p.x, (int) p.y))) {
				ents.add(e);
			}
		}
		return ents;
	}

	/**
	 * Add an entity, and give it to a layer.
	 * 
	 * @param e
	 *            the entity
	 */
	private void addEntity(final Entity e) {
		if (e != null) {
			entities.add(e);
			repaint();
		}
	}

	/**
	 * Remove an entity from all layers.
	 * 
	 * @param e
	 *            entity to remove.
	 */
	public void removeEntity(final Entity e) {
		entities.remove(e);
		repaint();
	}

	/**
	 * Remove entity from one layer and add to another.
	 * 
	 * @param e
	 *            entity to move.
	 * @param layer
	 *            layer to add to.
	 */
	private void transferEntity(final Entity e, final int layer) {
		removeEntity(e);
		e.layer = layer;
		e.variables.setVar("layer", layer + "");
		addEntity(e);
		repaint();
	}

	/**
	 * Save level.xml.
	 * 
	 * @return true if the save was successful; false otherwise
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 * @throws DOMException
	 */
	public boolean saveLevel() throws ParserConfigurationException,
			TransformerException, DOMException, IOException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// Create level element
		Element levelElement = doc.createElement("level");
		doc.appendChild(levelElement);

		// Add the attributes of the level
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("width", levelSize.width + "");
		props.put("height", levelSize.height + "");
		levelElement.setAttribute("map", ToriMapIO.writeMap(null, props));

		// Save the objects
		Element objectsElements = doc.createElement("objects");
		levelElement.appendChild(objectsElements);
		for (Entity e : entities) {
			HashMap<String, String> map = new HashMap<String, String>();
			if (e.getFile() != null)
				map.put("template",
						e.getFile()
								.getPath()
								.substring(
										e.getFile()
												.getPath()
												.indexOf(
														workingDirectory
																.getName())
												+ workingDirectory.getName()
														.length()));
			map.put("layer", e.layer + "");
			map.putAll(e.variables.getVariables());
			map.put("position.x", e.pos.getX() + "");
			map.put("position.y", e.pos.getY() + "");
			Element object = doc.createElement("entity");
			object.setAttribute("map", ToriMapIO.writeMap(null, map));
			objectsElements.appendChild(object);
		}

		if (levelFile != null) {
			ToriXML.saveXMLDoc(levelFile, doc);
			return true;
		} else {
			File[] files = importNewFileDialog("Save New Level File",
					"XML file (*.xml)", "xml");
			if (files.length != 0) {
				setLevelFile(files[0]);
				ToriXML.saveXMLDoc(levelFile, doc);
				repaint();
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Brings up a file chooser
	 * 
	 * @param title
	 *            The title of the window.
	 * @param description
	 *            the file type description.
	 * @param extension
	 *            the file extension to filter for.
	 * @return a list of selected files.
	 */
	public File[] importNewFileDialog(final String title,
			final String description, final String extension) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(workingDirectory);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				description, extension);
		fileChooser.setFileFilter(filter);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setAcceptAllFileFilterUsed(false);
		int ret = fileChooser.showDialog(null, title);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File[] files = fileChooser.getSelectedFiles();
			for (int i = 0; i < files.length; i++) {
				String path = files[i].getAbsolutePath();
				String ext = "."
						+ ((FileNameExtensionFilter) fileChooser
								.getFileFilter()).getExtensions()[0];
				if (!path.endsWith(ext)) {
					files[i] = new File(path + ext);
				}
			}

			return files;
		}
		return new File[0];
	}

	/**
	 * Fully import and add an xml entity. if the entity is a new type, it will
	 * be given a new button and added to button panel.
	 * 
	 * @param file
	 *            the file of the xml.
	 * @return the generated entity.
	 * @throws FileNotFoundException
	 */
	public Entity importEntity(final File file) throws FileNotFoundException {
		HashMap<String, String> data = ToriMapIO.readMap(file);

		final Entity e = Importer.importEntity(file, null);
		try {
			String[] s = data.get("sprite.editor").split(",");
			e.sprite.set(Integer.parseInt(s[0].trim()),
					Integer.parseInt(s[1].trim()));
		} catch (final Exception exc) {
			// The sprite remains at 1,1;
		}

		if (!entityExists(e)) {
			ImageIcon bI = new ImageIcon();
			bI.setImage(e.sprite.getImage().getScaledInstance(32, 32, 0));
			JButton b = new JButton(bI);
			b.setToolTipText(data.get("description"));
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setCurrent(e);
				}
			});
			buttonPanel.add(b);
			repaint();
		}
		return e;
	}

	private boolean entityExists(final Entity e) {
		for (Entity existingEntity : entities) {
			if (e.file.equals(existingEntity.file))
				return true;
		}
		return false;
	}

	/**
	 * Draw the state of the level.
	 * 
	 * @param g
	 *            graphics!
	 */
	public void draw(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, levelSize.width, levelSize.height);

		/*
		 * DRAW THE GRID
		 */
		if (gridSize.width > 1) {
			((Graphics2D) g).setStroke(new BasicStroke(1));
			g.setColor(Color.BLACK);
			for (int x = 0; x < levelSize.width; x += gridSize.width)
				g.drawLine(x, 0, x, levelSize.height);

			for (int y = 0; y < drawPanel.getHeight(); y += gridSize.height)
				g.drawLine(0, y, drawPanel.getWidth(), y);
		}

		/**
		 * DRAW ENTITIES in layer order.
		 */
		Collections.sort(entities, new Comparator<Entity>() {
			@Override
			public int compare(Entity a, Entity b) {
				int ret = new Integer(b.layer).compareTo(new Integer(a.layer));
				if (ret == 0) {
					if ("WALL".equals(a.type))
						return 1;
					else if ("WALL".equals(b.type))
						return -1;
					else
						return 0;
				} else {
					return ret;
				}
			}
		});
		for (Entity e : entities) {
			if (layerEditor.isLayerVisible(e.layer))
				e.draw(g, new Vector2());
		}

		if (selected != null) {
			g.setColor(Color.GREEN);
			g.drawRect((int) selected.pos.getX(), (int) selected.pos.getY(),
					(int) selected.dim.getX(), (int) selected.dim.getY());
		}

		g.setColor(Color.RED);
		g.draw3DRect(0, 0, levelSize.width, levelSize.height, true);

		if (mode == Mode.WALL_MAKING) {
			((Graphics2D) g).setStroke(new BasicStroke(2));
			g.drawRect((int) wallStart.x, (int) wallStart.y, (int) wallEnd.x
					- (int) wallStart.x, (int) wallEnd.y - (int) wallStart.y);
			((Graphics2D) g).setStroke(new BasicStroke(1));
		}
	}

	/**
	 * Clear the GUI state.
	 */
	private void clear() {
		entities.clear();
		current = null;
		selected = null;
		buttonPanel.removeAll();
		layerEditor.clear();
	}

	public void setCurrent(final Entity e) {
		current = e;
	}

	/**
	 * Forces repaint on frame and updates status bar.
	 */
	public void repaint() {
		if (levelFile != null)
			fileLabel.setText(levelFile.getName().substring(0,
					levelFile.getName().lastIndexOf(".")));
		else
			fileLabel.setText("new level");
		gridLabel.setText("Grid: " + (int) gridSize.getWidth() + " x "
				+ (int) gridSize.getHeight());
		levelSizeLabel.setText("Level Size: " + (int) levelSize.getWidth()
				+ " x " + (int) levelSize.getHeight());
		if (selected != null) {
			editModeLabel.setText("Editing Single Entity: " + selected.type);
		} else if (mode == Mode.WALL_QUEUE) {
			editModeLabel.setText("Click and Drag to Draw Wall");
		} else if (mode == Mode.BG) {
			editModeLabel
					.setText("Click To Place a BG Tile in Current Layer (Ctrl+B to exit mode)");
		} else {
			editModeLabel.setText("Click to Place Entity");
		}
		drawPanel.setPreferredSize(levelSize);
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

	public Vector2 getClosestGridPoint(final Vector2 p) {
		return getClosestGridPoint(gridSize, p);
	}

	public static Vector2 getClosestGridPoint(final Dimension gridSize,
			final Vector2 p) {
		return new Vector2(((int) p.x / (int) gridSize.width) * gridSize.width,
				((int) p.y / (int) gridSize.height) * gridSize.height);
	}
}
