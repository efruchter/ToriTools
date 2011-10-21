package samplegame.entity;

/**
 * A scripted entity that exists in the game world.
 * 
 * @author toriscope
 * 
 */
public class Entity {

	/**
	 * Holds all variables for the entity.
	 */
	private VariableCase variables = new VariableCase();

	/**
	 * This variable case will be passed in containing the additional data from
	 * the xml level file, as well as entity data from the entity xml.
	 * 
	 * @param variables
	 */
	public Entity(final VariableCase variables) {
		this.variables = variables;
	}

	public VariableCase getVariables() {
		return variables;
	}
}

/*
 * TODO: Add scripting support.
 */