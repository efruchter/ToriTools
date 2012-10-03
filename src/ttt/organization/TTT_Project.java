package ttt.organization;

import nu.xom.Attribute;
import nu.xom.Element;
import ttt.io.XMLSerializeable;
import ttt.organization.managers.TTT_EntityManager;
import ttt.organization.managers.TTT_SceneManager;

public class TTT_Project implements XMLSerializeable {

    public final TTT_SceneManager sceneManager;
    public final TTT_VariableCase variables;
    public final TTT_EntityManager entities;
    private String openingScene;

    // Updating code
    private static boolean levelSwitchRequested = false;
    private static String nextLevel;

    public TTT_Project() {
        sceneManager = new TTT_SceneManager();
        variables = new TTT_VariableCase();
        entities = new TTT_EntityManager();
        openingScene = "";

    }

    public static void switchToLevel(final String levelId) {
        nextLevel = levelId;
        levelSwitchRequested = true;
    }

    @Override
    public Element writeToElement() {
        Element e = new Element(getElementName());
        e.appendChild(sceneManager.writeToElement());
        e.appendChild(variables.writeToElement());
        e.appendChild(entities.writeToElement());
        e.addAttribute(new Attribute("startingScene", openingScene));
        return e;
    }

    @Override
    public void assembleFromElement(Element entity) {
        sceneManager.assembleFromElement(entity.getChildElements(sceneManager.getElementName()).get(0));
        variables.assembleFromElement(entity.getChildElements(variables.getElementName()).get(0));
        entities.assembleFromElement(entity.getChildElements(entities.getElementName()).get(0));
        openingScene = entity.getAttribute("startingScene").getValue();
    }

    public void update(final long milliDelta) {
        if (levelSwitchRequested) {
            sceneManager.getCurrentScene().onDeath();
            sceneManager.moveToScene(nextLevel);
            sceneManager.getCurrentScene().onSpawn();
            levelSwitchRequested = false;
        }
    }

    @Override
    public String getElementName() {
        return "project";
    }

    public void setOpeningScene(String openingScene) {
        this.openingScene = openingScene;
    }

    public void moveToOpeningScene() {
        sceneManager.moveToScene(openingScene);
    }

    public String toString() {
        return getElementName();
    }
}
