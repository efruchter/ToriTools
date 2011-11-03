package toritools.leveleditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import toritools.entity.Entity;

@SuppressWarnings("serial")
public class VariableEditor extends JPanel {
	private JButton addVarButton = new JButton(" + ");
	private JPanel buttonPanel = new JPanel();
	private Entity entity;
	private HashMap<String, JTextField> keys = new HashMap<String, JTextField>();
	private JLabel statusLabel = new JLabel("Variables");
	private LevelEditor editor;

	public VariableEditor(final LevelEditor editor) {
		setAlignmentY(Component.TOP_ALIGNMENT);
		setBackground(Color.cyan);
		buttonPanel.setBackground(Color.cyan);
		setBorder(BorderFactory.createRaisedBevelBorder());
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		add(statusLabel);
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (getEntity() != null) {
					saveCurrent();
					JOptionPane.showMessageDialog(null,
							"Instance variables saved to entity!");
				}
			}
		});
		JPanel p = new JPanel();
		p.setBackground(Color.cyan);
		p.add(addVarButton);
		p.add(saveButton);
		add(p);
		add(new JScrollPane(buttonPanel));
		this.editor = editor;
		addVarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (getEntity() == null)
					return;
				String s = JOptionPane.showInputDialog("New variable name?");
				if (s != null && !s.isEmpty()) {
					getEntity().variables.setVar(s, "DEFAULT");
					Entity e = getEntity();
					clear();
					setEntity(e);
				}
			}
		});
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(final Entity e) {
		clear();
		this.entity = e;
		loadVariables();
		setEnabled(entity != null);
		editor.repaint();
	}

	private void loadVariables() {
		if (entity != null)
			for (Entry<String, String> s : entity.variables.getVariables()
					.entrySet()) {
				if (s.getKey().equals("layer")
						|| s.getKey().startsWith("position.")
						|| s.getKey().equals("template"))
					continue;
				JPanel micro = new JPanel();
				micro.setBackground(Color.CYAN);
				String value = s.getValue();
				value = value != null ? value : "";
				JTextField field = new JTextField(value, 10);
				keys.put(s.getKey(), field);
				micro.add(new JLabel(s.getKey() + ":"));
				micro.add(field);
				buttonPanel.add(micro);
			}
	}

	public void saveCurrent() {
		if (entity != null)
			for (Entry<String, JTextField> s : keys.entrySet()) {
				String data = s.getValue().getText();
				if (!data.isEmpty()) {
					entity.variables.getVariables()
							.put(s.getKey(), s.getValue().getText());
				} else {
					entity.variables.getVariables().remove(s.getKey());
				}

			}
	}

	public void clear() {
		entity = null;
		buttonPanel.removeAll();
		keys.clear();
	}
}
