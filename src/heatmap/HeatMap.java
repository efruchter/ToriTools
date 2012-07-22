package heatmap;

import heatmap.entities.Player;

import java.awt.Color;
import java.awt.Graphics2D;

import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;

public class HeatMap extends Binary {

	public HeatMap() {
		super(new Vector2(800, 600), 60, "HeatMap Demo");
	}

	@Override
	protected void initialize() {
		
	}

	@Override
	protected void globalLogic(Level level, long milliDelay) {

	}

	@Override
	protected void setupCurrentLevel(Level levelBeingLoaded) {
		levelBeingLoaded.spawnEntity(new Player());
	}

	@Override
	protected Level getStartingLevel() {
		return new Level();
	}

	@Override
	protected boolean render(Graphics2D rootCanvas, Level level) {
		try {
			rootCanvas.setColor(Color.BLACK);
			rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);

			for (int i = level.getLayers().size() - 1; i >= 0; i--)
				for (Entity e : level.getLayers().get(i)) {
					e.draw(rootCanvas);
				}
		} catch (final NullPointerException stillLoading) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		new HeatMap();
	}
}
