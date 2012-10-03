package ttt.editor.components.treeview;

import javax.swing.JFrame;
import javax.swing.JTree;

import ttt.editor.TTT_EditorComponent;
import ttt.organization.TTT_Entity;
import ttt.organization.TTT_Project;
import ttt.organization.TTT_Scene;

@SuppressWarnings("serial")
public class TTT_EditorHierarchyView extends JTree implements TTT_EditorComponent {

    public TTT_EditorHierarchyView() {
        this.setExpandsSelectedPaths(true);
        this.setEditable(false);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void entitySelected(TTT_Entity entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sceneSelected(TTT_Scene scene) {
        // TODO Auto-generated method stub

    }

    @Override
    public void projectSelected(final TTT_Project project) {
        setModel(new TTT_EditorProjectTreeModel(project));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TTT_EditorComponent d;
        frame.add((TTT_EditorHierarchyView) (d = new TTT_EditorHierarchyView()));

        TTT_Project p = new TTT_Project() {
            {
                sceneManager.addScene(new TTT_Scene() {
                    {
                        entities.addEntity(new TTT_Entity());
                    }
                });
            }
        };

        d.projectSelected(p);

        frame.pack();
        frame.setVisible(true);
    }
}
