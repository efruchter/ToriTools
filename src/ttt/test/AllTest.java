package ttt.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ttt.TTT_Constants;
import ttt.organization.TTT_Entity;
import ttt.organization.TTT_Project;
import ttt.organization.TTT_Scene;

public class AllTest {

	@Test
	public void test() {
		// Project
		TTT_Project project = new TTT_Project();
		project.variables.store("name", "FUN PROJECT 1");

		// Scene 1
		TTT_Scene scene = new TTT_Scene();
		scene.variables.store(TTT_Constants.ID_KEY, "TESTO");
		project.sceneManager.addScene(scene);

		// Scene 2
		TTT_Scene scene2 = new TTT_Scene();
		scene2.variables.store(TTT_Constants.ID_KEY, "TESTODOS");
		project.sceneManager.addScene(scene2);

		// Entities
		TTT_Entity e = new TTT_Entity();
		e.types.addType("AAAA", "BBBB");
		e.variables.store("inEntity", "EEE");
		scene.entities.addEntity(e);

		TTT_Project project2 = new TTT_Project();
		project2.assembleFromElement(project.writeToElement());

		System.out.println(project.writeToElement().toXML().toString());
		System.out.println(project2.writeToElement().toXML().toString());

		assertTrue(
				project.writeToElement().toXML().toString() + "\n"
						+ project2.writeToElement().toXML().toString(),
				project.writeToElement().toXML().toString()
						.equals(project2.writeToElement().toXML().toString()));

		// Test entity sorting, etc
		e = new TTT_Entity();
		e.variables.store(TTT_Constants.ID_KEY, "blooper");
		e.types.addType("cat", "green");
		scene.entities.addEntity(e);
		assertTrue("ID retrieval broken",
				e == scene.entities.getEntityById("blooper"));
		List<TTT_Entity> l = scene.entities.getEntitiesByType("cat", "green");
		assertTrue("Type list retrieval broken",
				l.size() == 2 && l.get(0) == l.get(1) && e == l.get(0));

		e = new TTT_Entity();
		e.variables.store(TTT_Constants.ID_KEY, "waka");
		e.types.addType("root", "cat");
		scene.entities.addEntity(e);
		assertTrue("ID retrieval broken",
				e == scene.entities.getEntityById("waka"));
		l = scene.entities.getEntitiesByType("cat", "root", "green");
		assertTrue("Type list concat broken2", l.size() == 4);

		// Jython Test
		e.scripts.addScript("ttt/test/TestScript.py");
		e.scripts.onSpawn(e, scene);
		e.scripts.onUpdate(e, scene, 16);
		e.scripts.onDeath(e, scene, false);

		project.moveToOpeningScene();

		// moveupdate
		TTT_Project.switchToLevel("TESTO");
		project.update(16);

	}
}
