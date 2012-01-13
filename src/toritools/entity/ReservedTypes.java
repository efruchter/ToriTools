package toritools.entity;

/**
 * The reserved types for encoding specific types of entities.
 * @author toriscope
 *
 */
public enum ReservedTypes {
	/**
	 * The type of the background tiles.
	 */
	BACKGROUND("BACKGROUND"),

	/**
	 * The invisible walls.
	 */
	WALL("WALL");

	final String encodedName;

	private ReservedTypes(final String s) {
		this.encodedName = s;
	}
	
	/**
	 * Get the name as it is encoded in level files.
	 */
	@Override
	public String toString() {
		return encodedName;
	}
}
