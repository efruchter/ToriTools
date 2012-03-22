package snakemeleon;

import java.awt.Color;
import java.awt.Graphics2D;

import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

public class SnakemeleonHUD {
    
    private int w = 270, h = 132;

    public void draw(Graphics2D g, final Vector2 viewport) {
        g.setColor(Color.CYAN);
        g.drawImage(ScriptUtils.fetchImage(SnakemeleonConstants.hudImageFile), viewport.getWidth() - w, viewport.getHeight() - h, w, h, null);
        g.drawString("Apples Remaining", viewport.getWidth() - w, viewport.getHeight() - h);
    }
}
