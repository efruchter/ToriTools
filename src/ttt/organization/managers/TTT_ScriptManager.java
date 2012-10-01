package ttt.organization.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Elements;
import ttt.io.JythonFactory;
import ttt.io.XMLSerializeable;
import ttt.organization.TTT_Entity;
import ttt.organization.TTT_EntityScript;
import ttt.organization.TTT_Scene;

public class TTT_ScriptManager implements XMLSerializeable, TTT_EntityScript {

    final private static HashMap<String, TTT_EntityScript> library;

    static {
        library = new HashMap<String, TTT_EntityScript>();
    }

    final private List<String> scripts;

    public TTT_ScriptManager() {
        scripts = new LinkedList<String>();
    }

    @Override
    public Element writeToElement() {
        Element scriptss = new Element(getElementName());
        for (String file : scripts) {
            Element tt = new Element("script");
            tt.appendChild(file);
            scriptss.appendChild(tt);
        }
        return scriptss;
    }

    @Override
    public void assembleFromElement(Element entity) {
        scripts.clear();
        Elements ele = entity.getChildElements();
        for (int i = 0; i < ele.size(); i++) {
            scripts.add(ele.get(i).getValue());
        }
    }

    @Override
    public String getElementName() {
        return "scripts";
    }

    @Override
    public String getName() {
        return "scriptgroup";
    }

    @Override
    public void onSpawn(TTT_Entity self, TTT_Scene scene) {
        for (String key : scripts) {
            library.get(key).onSpawn(self, scene);
        }
    }

    @Override
    public void onUpdate(TTT_Entity self, TTT_Scene scene, long timeDelta) {
        for (String key : scripts) {
            library.get(key).onUpdate(self, scene, timeDelta);
        }
    }

    @Override
    public void onDeath(TTT_Entity self, TTT_Scene scene, boolean isRoomExit) {
        for (String key : scripts) {
            library.get(key).onDeath(self, scene, isRoomExit);
        }
    }

    public static TTT_EntityScript getScriptFromLibrary(String key) {
        return library.get(key);
    }

    public static void addScriptToLibrary(String fileString) {
        library.put(fileString, TTT_EntityScript.BLANK);
        reloadLibrary();
    }

    public void addScript(String fileKey) {
        if (!library.containsKey(fileKey)) {
            addScriptToLibrary(fileKey);
        }
        scripts.add(fileKey);
    }

    public static void reloadLibrary() {
        Set<Entry<String, TTT_EntityScript>> s = library.entrySet();
        s.clear();
        for (Entry<String, TTT_EntityScript> scr : s) {
            String loc = scr.getKey();
            try {
                TTT_EntityScript eType = (TTT_EntityScript) JythonFactory.getJythonObject(
                        "ttt.organization.TTT_EntityScript", loc);
                library.put(loc, eType);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
