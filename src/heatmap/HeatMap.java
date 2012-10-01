package heatmap;

import java.awt.Graphics2D;

import toritools.entity.Level;
import toritools.math.Vector2;
import ttt.TTT_Binary;
import ttt.organization.TTT_Project;
import ttt.organization.TTT_Scene;

public class HeatMap extends TTT_Binary {

    public HeatMap(TTT_Project p) {

        super(p, new Vector2(800, 600), 60, "HeatMap Demo");
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void globalLogic(TTT_Scene level, long milliDelay) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean render(Graphics2D rootCanvas, Level level) {
        // TODO Auto-generated method stub
        return false;
    }

    public static void main(String[] args) {
        TTT_Project proj = new TTT_Project();
        TTT_Scene s = new TTT_Scene();
        new HeatMap(proj);
    }
}
