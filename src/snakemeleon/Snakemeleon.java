package snakemeleon;

import java.awt.Color;
import java.awt.Graphics;

import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;

public class Snakemeleon extends Binary {

    public static void main(String[] args) {
        new Snakemeleon();
    }

    public Snakemeleon() {
        super(new Vector2(800, 600), 60, "Snakemeleon");
    }

    @Override
    protected void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void globalLogic(Level level) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean render(Graphics rootCanvas, Level level) {
        Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        rootCanvas.setColor(c);
        rootCanvas.fillRect(0, 0, VIEWPORT.getWidth(), VIEWPORT.getHeight());
        return true;
    }

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {

    }

    @Override
    protected Level getStartingLevel() {
        Level l = new Level();
        l.setDim(VIEWPORT);
        return l;
    }

}
