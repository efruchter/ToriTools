package toritools.leveleditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class VariableEditor extends JPanel {
	private JButton addVarButton = new JButton("Add Variable");
	private JPanel buttonPanel = new JPanel();
	private Entity entity;
	private HashMap<String, JTextField> keys = new HashMap<String, JTextField>();

	private LevelEditor editor;

	public VariableEditor(final LevelEditor editor) {
		add(addVarButton);
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveCurrent();
			}
		});
		add(saveButton);
		add((buttonPanel));
		this.editor = editor;
		addVarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = JOptionPane.showInputDialog("New variable name?");
				if (s != null && !s.isEmpty() && entity != null) {
					getEntity().getVariables().setVar(s, "");
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

	public void setEntity(final Entity... e) {
		clear();
		this.entity = e[0];
		loadVariables();
		editor.repaint();
	}

	private void loadVariables() {
		if (entity != null)
			for (Entry<String, String> s : entity.getVariables().getVariables()
					.entrySet()) {
				if (s.getKey().equals("layer")
						|| s.getKey().startsWith("position.")
						|| s.getKey().equals("template"))
					continue;
				buttonPanel.add(new JLabel(s.getKey() + ":"));
				String value = s.getValue();
				value = value != null ? value : "";
				JTextField field = new JTextField(value, 10);
				buttonPanel.add(field);
				keys.put(s.getKey(), field);
			}
	}

	public void saveCurrent() {
		if (entity != null)
			for (Entry<String, JTextField> s : keys.entrySet())
				entity.getVariables().getVariables()
						.put(s.getKey(), s.getValue().getText());
	}

	public void clear() {
		entity = null;
		buttonPanel.removeAll();
		keys.clear();
	}
}
