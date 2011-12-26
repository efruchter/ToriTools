package samplegame.customScripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

import samplegame.SampleGame;
import toritools.dialog.DialogNode;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entity.sprite.Sprite;
import toritools.math.Vector2;
import toritools.scripting.EntityScript;

public class DialogEntity extends Entity {

    private List<String> currentDisplay;

    public List<String> getCurrentDisplay() {
        return currentDisplay;
    }

    public void setCurrentDisplay(List<String> currentDisplay) {
        this.currentDisplay = currentDisplay;
    }

    public DialogEntity(final DialogNode dialogNode) {

        script = new EntityScript() {

            @Override
            public void onSpawn(Level level, Entity self) {

            }

            @Override
            public void onUpdate(Level level, Entity self) {
                if (getCurrentDisplay() == null
                        || SampleGame.keys.isPressedThenRelease(KeyEvent.VK_SPACE)) {
                    setCurrentDisplay(dialogNode.getNextLines(3));
                    System.err.println("SentencesLeft: " + getCurrentDisplay().size());
                    if (getCurrentDisplay().isEmpty()) {
                        level.killEntity(self);
                    }
                }

            }

            @Override
            public void onDeath(Level level, Entity self, boolean isRoomExit) {
                dialogNode.doAction(level);
            }

        };

        layer = 0;

        sprite = new Sprite() {
            @Override
            public void draw(final Graphics g, final Entity self, final Vector2 pos,
                    final Vector2 dim) {
                List<String> displayString = getCurrentDisplay();
                if (displayString == null) {
                    return;
                }
                g.setColor(Color.WHITE);
                for (int i = 0; i < displayString.size(); i++) {
                    g.drawString(displayString.get(i), 200, 200 + i * 20);
                }
            }
        };
    }
}
