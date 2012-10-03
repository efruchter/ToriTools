package ttt.editor;

import ttt.organization.TTT_Entity;
import ttt.organization.TTT_Project;
import ttt.organization.TTT_Scene;

public interface TTT_EditorComponent {

    void initialize();

    void entitySelected(TTT_Entity entity);

    void sceneSelected(TTT_Scene scene);

    void projectSelected(TTT_Project project);
}
