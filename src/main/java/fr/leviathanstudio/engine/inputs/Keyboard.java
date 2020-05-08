package fr.leviathanstudio.engine.inputs;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

/**
 * @author ZeAmateis
 */
public class Keyboard {

    private long windowHandle;

    public void init(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    public boolean isKeyPressed(Keybinding keyIn) {
        return glfwGetKey(windowHandle, keyIn.getKeyCode()) == GLFW_PRESS;
    }

}
