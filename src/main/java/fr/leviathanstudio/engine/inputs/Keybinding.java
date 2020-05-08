package fr.leviathanstudio.engine.inputs;

import fr.leviathanstudio.engine.config.Configuration;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZeAmateis
 */
public class Keybinding {
    private final Configuration configuration;

    public static final List<Keybinding> KEYBINDINGS = new ArrayList<>();

    public static Keybinding FORWARD = new Keybinding("forward", "move character forward", GLFW.GLFW_KEY_W);
    public static Keybinding BACKWARD = new Keybinding("backward", "move character backward", GLFW.GLFW_KEY_S);
    public static Keybinding LEFT = new Keybinding("left", "move character on the left", GLFW.GLFW_KEY_A);
    public static Keybinding RIGHT = new Keybinding("right", "move character on the right", GLFW.GLFW_KEY_D);

    public static Keybinding CROUCH = new Keybinding("down", "move character down", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static Keybinding JUMP = new Keybinding("jump", "jump", GLFW.GLFW_KEY_SPACE);

    private final String name, description;
    private final int keyCode;

    public Keybinding(String nameIn, String descriptionIn, int keyCodeIn) {
        this.configuration = new Configuration(Paths.get("./config/controls.conf"));
        this.name = nameIn;
        this.description = descriptionIn;

        this.keyCode = this.configuration.getConfig() != null ? this.configuration.getInt(nameIn) : keyCodeIn;
        KEYBINDINGS.add(this);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getKeyCode() {
        return keyCode;
    }

}
