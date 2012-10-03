package ttt.editor.components.treeview;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ttt.organization.TTT_Project;
import ttt.organization.TTT_Scene;
import ttt.organization.managers.TTT_EntityManager;
import ttt.organization.managers.TTT_SceneManager;

public class TTT_EditorProjectTreeModel implements TreeModel {

    // Project
    // -scene
    // --scene1
    // --scene2
    // -entities
    // --ent1
    // --ent2

    final private TTT_Project project;

    public TTT_EditorProjectTreeModel(final TTT_Project project) {
        this.project = project;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public Object getChild(Object o, int index) {
        if (o instanceof TTT_Project) {
            TTT_Project p = (TTT_Project) o;
            return index == 0 ? p.sceneManager : p.entities;
        } else if (o instanceof TTT_SceneManager) {
            return ((TTT_SceneManager) o).getSceneList().get(index);
        } else if (o instanceof TTT_EntityManager) {
            return ((TTT_EntityManager) o).getAllEntitiesFast().get(0);
        } else if (o instanceof TTT_Scene) {
            return ((TTT_Scene) o).entities.getAllEntitiesFast().get(0);
        }

        return 0;
    }

    @Override
    public int getChildCount(Object o) {
        if (o instanceof TTT_Project) {
            return 2;
        } else if (o instanceof TTT_SceneManager) {
            return ((TTT_SceneManager) o).getSceneList().size();
        } else if (o instanceof TTT_EntityManager) {
            return ((TTT_EntityManager) o).getAllEntitiesFast().size();
        } else if (o instanceof TTT_Scene) {
            return ((TTT_Scene) o).entities.getAllEntitiesFast().size();
        }

        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof TTT_Project) {
            return child instanceof TTT_SceneManager ? 0 : 1;
        } else if (parent instanceof TTT_SceneManager) {
            return ((TTT_SceneManager) parent).getSceneList().indexOf(child);
        } else if (parent instanceof TTT_EntityManager) {
            return ((TTT_EntityManager) parent).getAllEntitiesFast().indexOf(child);
        } else if (parent instanceof TTT_Scene) {
            return ((TTT_Scene) parent).entities.getAllEntitiesFast().indexOf(child);
        }
        return -1;
    }

    @Override
    public Object getRoot() {

        return project;
    }

    @Override
    public boolean isLeaf(Object node) {
        return !(node instanceof TTT_Project) && !(node instanceof TTT_SceneManager) && !(node instanceof TTT_Scene)
                && !(node instanceof TTT_EntityManager);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO Auto-generated method stub

    }
}
