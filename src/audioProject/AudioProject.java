package audioProject;

import java.awt.Graphics;
import java.io.File;

import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.math.Vector2;

/**
 * Template for our possible audio project.
 * @author toriscope
 *
 */
public class AudioProject extends Binary{

	public AudioProject(Vector2 VIEWPORT_SIZE, int frameRate) {
		super(VIEWPORT_SIZE, frameRate);
	}

	@Override
	protected boolean render(Graphics rootCanvas) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void globalLogic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setupCurrentLevel(Level levelBeingLoaded) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected File getStartingLevel() {
		// TODO Auto-generated method stub
		return null;
	}

}
