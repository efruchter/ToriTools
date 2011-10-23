package samplegame.entity;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;

import samplegame.render.Render2D;
import samplegame.scripting.EntityScript;

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
     * The control script for this entity.
     */
    private EntityScript script;

    /*
     * INSTANCE ENTITY VARIABLES
     */
    private Vector2f pos, dim;
    private Boolean solid;
    private File editorSprite;

    /**
     * This variable case will be passed in containing the additional data from
     * the xml level file, as well as entity data from the entity xml.
     * 
     * @param variables
     */
    public Entity(final VariableCase variables, final EntityScript script) {
        this.variables = variables;
        this.script = script;

        pos = new Vector2f(Float.parseFloat(variables.getVar("position.x")),
                Float.parseFloat(variables.getVar("position.y")));
        dim = new Vector2f(Float.parseFloat(variables.getVar("dimensions.x")),
                Float.parseFloat(variables.getVar("dimensions.y")));
        solid = Boolean.parseBoolean(variables.getVar("solid"));
        editorSprite = new File(variables.getVar("sprites.editor"));
    }

    public VariableCase getVariables() {
        return variables;
    }

    /*
     * CONTROL METHODS
     */

    public void onSpawn(final World world) {
        script.onSpawn();
    }

    public void onUpdate(final World world) {
        script.onUpdate();
    }

    public void onDeath(final World world) {
        script.onDeath();
    }

    public void draw() {
        Render2D.fillRect(pos, dim);
    }

    public Vector2f getPos() {
        return pos;
    }

    public void setPos(Vector2f pos) {
        this.pos = pos;
    }

    public Vector2f getDim() {
        return dim;
    }

    public Boolean getSolid() {
        return solid;
    }

    public File getEditorSprite() {
        return editorSprite;
    }
}