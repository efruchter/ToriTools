package tamodatchi.types;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import tamodatchi.types.Creature.State;

public class GUIController {

    private JProgressBar moodBar, energyBar;
    private JLabel nameLabel, messageLabel;

    public GUIController(final JLabel messageLabel, final JPanel corePanel) {

        this.messageLabel = messageLabel;

        //corePanel.setLayout(new BoxLayout(corePanel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("Chet the Named One");
        nameLabel.setForeground(Color.WHITE);

        moodBar = new JProgressBar(0, 100);
        moodBar.setString("Mood");
        moodBar.setValue(90);
        moodBar.setStringPainted(true);

        energyBar = new JProgressBar(0, 100);
        energyBar.setString("Energy");
        energyBar.setValue(10);
        energyBar.setStringPainted(true);

        corePanel.add(nameLabel);
        corePanel.add(moodBar);
        corePanel.add(energyBar);
    }

    public void updateInfo(final Creature creature) {
        moodBar.setValue((int) (creature.getMood() * 100));
        energyBar.setValue((int) (creature.getEnergy() * 100));
        nameLabel.setText(creature.getName());

        // State messages
        if (creature.getState() == State.ROAM) {
            messageLabel.setText(creature.getName() + " is roaming around.");
        }
        if (creature.getState() == State.SLEEP) {
            messageLabel.setText(creature.getName() + " is fast asleep.");
        }
        if (creature.getState() == State.HUNTING) {
            messageLabel.setText(creature.getName() + " is looking for food.");
        }
    }
}
