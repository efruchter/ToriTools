package tamodatchi.types;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class GUIPanel extends JPanel {

    private JProgressBar moodBar, energyBar;
    private JLabel nameLabel, messageLabel;

    public GUIPanel(final JLabel messageLabel) {

        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        this.messageLabel = messageLabel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("Chet the Named One");

        moodBar = new JProgressBar(0, 100);
        moodBar.setString("Mood");
        moodBar.setValue(90);
        moodBar.setStringPainted(true);

        energyBar = new JProgressBar(0, 100);
        energyBar.setString("Energy");
        energyBar.setValue(10);
        energyBar.setStringPainted(true);

        add(nameLabel);
        add(moodBar);
        add(energyBar);
    }

    public void updateInfo(final Creature creature) {
        moodBar.setValue((int) (creature.getMood() * 100));
        energyBar.setValue((int) (creature.getEnergy() * 100));
        nameLabel.setText(creature.getName());
    }
}
