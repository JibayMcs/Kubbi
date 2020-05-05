package fr.leviathanstudio.engine.inputs;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author ZeAmateis
 */
public class Keyboard {

    private long windowHandle;

    public static Key Z = new Key(GLFW_KEY_W);
    public static Key Q = new Key(GLFW_KEY_A);
    public static Key S = new Key(GLFW_KEY_S);
    public static Key D = new Key(GLFW_KEY_D);

    public static Key A = new Key(GLFW_KEY_Q);
    public static Key E = new Key(GLFW_KEY_E);

    public static Key SPACE = new Key(GLFW_KEY_SPACE);
    public static Key LEFT_SHIFT = new Key(GLFW_KEY_LEFT_SHIFT);
    public static Key LEFT_CTRL = new Key(GLFW_KEY_LEFT_CONTROL);


    public void init(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    public boolean isKeyPressed(Key keyIn) {
        return glfwGetKey(windowHandle, keyIn.getKey()) == GLFW_PRESS;
    }

    public static class Key {
        private int key;

        public Key(int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }
    }
}
