package snakemeleon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import snakemeleon.types.ChameleonScript;
import toritools.entity.Entity;
import toritools.entity.Level;
import toritools.entrypoint.Binary;
import toritools.io.Importer;
import toritools.math.Vector2;
import toritools.scripting.ScriptUtils;

public class Snakemeleon extends Binary {

    public static void main(String[] args) {
        new Snakemeleon();
    }

    public Snakemeleon() {
        super(new Vector2(800, 600), 60, "Snakemeleon");
        super.getApplicationFrame().setIconImage(new ImageIcon("snakemeleon/chameleon_head.png").getImage());
    }

    @Override
    protected void initialize() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void globalLogic(Level level) {
        world.step(60 / 1000f, 1, 1);
        System.out.println(body.getPosition());
    }

    @Override
    protected boolean render(Graphics rootCanvas, Level level) {
        try {
            ((Graphics2D) rootCanvas).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Entity player = level.getEntityWithId("player");
            if (player == null) {
                System.out.println("You need to make an entity with id set to player!");
                System.exit(1);
            }
            // Vector2 playerPos = player.getPos();
            Vector2 offset = Vector2.ZERO; // VIEWPORT.scale(.5f).sub(playerPos);

            rootCanvas.setColor(Color.BLACK);
            rootCanvas.fillRect(0, 0, (int) VIEWPORT.x, (int) VIEWPORT.y);
            for (int i = level.getLayers().size() - 1; i >= 0; i--) {
                for (Entity e : level.getLayers().get(i)) {
                    if (e.isVisible() && e.isInView())
                        e.draw(rootCanvas, offset);
                    if (!"BACKGROUND".equals(e.getType()) && ScriptUtils.isDebugMode()) {
                        rootCanvas.setColor(Color.RED);
                        rootCanvas.drawRect((int) (e.getPos().x + offset.x), (int) (e.getPos().y + offset.y),
                                (int) e.getDim().x, (int) e.getDim().y);
                    }

                }
            }

            /*
             * Circle test.
             */
            rootCanvas.setColor(Color.RED);
            Vector2 pos = new Vector2(body.getPosition().x, body.getPosition().y);
            rootCanvas.fillOval(pos.getWidth() - 20, pos.getHeight() - 20, 40, 40);

            pos = new Vector2(body2.getPosition().x, body2.getPosition().y);
            rootCanvas.fillOval(pos.getWidth() - 100, pos.getHeight() - 100, 200, 200);

        } catch (final Exception e) {
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    World world;
    Body body, body2;

    @Override
    protected void setupCurrentLevel(Level levelBeingLoaded) {
        for (Entity e : levelBeingLoaded.getEntitiesWithType("player")) {
            e.addScript(new ChameleonScript());
        }

        world = new World(new Vec2(0.0f, 9.814f), true);

        // Create an JBox2D body defination for ball.
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(120, 0);

        CircleShape cs = new CircleShape();
        cs.m_radius = 20;

        // Create a fixture for ball
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 0.9f;
        fd.friction = 0.3f;
        fd.restitution = 0.6f;
        body = world.createBody(bd);
        body.createFixture(fd);

        // Create rigid thing
        BodyDef bd2 = new BodyDef();
        bd2.type = BodyType.STATIC;
        bd2.position.set(100, 300);

        CircleShape cs2 = new CircleShape();
        cs2.m_radius = 100;

        // Create a fixture for ball
        FixtureDef fd2 = new FixtureDef();
        fd2.shape = cs2;
        fd2.density = 0.9f;
        fd2.friction = 0.3f;
        fd2.restitution = 0.6f;
        body2 = world.createBody(bd2);
        body2.createFixture(fd2);
    }

    @Override
    protected Level getStartingLevel() {
        try {
            return Importer.importLevel(new File("snakemeleon/TestLevel.xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
