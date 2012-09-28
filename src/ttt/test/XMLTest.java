package ttt.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ttt.TTT_Constants;
import ttt.organization.TTT_Entity;
import ttt.organization.TTT_Project;
import ttt.organization.TTT_Scene;

public class XMLTest {

	@Test
	public void test() {
		// Project
		TTT_Project project = new TTT_Project();
		project.variables.setString("name", "FUN PROJECT 1");

		// Scene 1
		TTT_Scene scene = new TTT_Scene();
		project.sceneManager.addScene(scene);
		scene.variables.setString(TTT_Constants.LEVEL_ID, "TESTO");

		// Scene 2
		TTT_Scene scene2 = new TTT_Scene();
		project.sceneManager.addScene(scene2);
		scene2.variables.setString(TTT_Constants.LEVEL_ID, "TESTODOS");

		// Entities
		TTT_Entity e = new TTT_Entity();
		e.types.addType("AAAA", "BBBB");
		e.variables.setString("inEntity", "EEE");
		scene.entities.addEntity(e);

		TTT_Project project2 = new TTT_Project();
		project2.assembleFromElement(project.writeToElement());

		System.out.println(project.writeToElement().toXML().toString());
		System.out.println(project2.writeToElement().toXML().toString());

		assertTrue(project.writeToElement().toXML().toString()
				.equals(project2.writeToElement().toXML().toString()));
	}

}
