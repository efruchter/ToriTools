package ttt.io;

import nu.xom.Element;

public interface XMLSerializeable {
	/**
	 * Write to an xml element
	 * 
	 * @return the element
	 */
	Element writeToElement();

	/**
	 * Clear the fields and assemble them from an entity
	 * 
	 * @param entity
	 */
	void assembleFromElement(Element entity);

	/**
	 * Get the name of the root element of this class.
	 * 
	 * @return
	 */
	String getElementName();
}
