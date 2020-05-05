package fr.zeamateis.kubbi.common.entity;

import fr.leviathanstudio.engine.Window;
import fr.leviathanstudio.engine.inputs.Keyboard;
import fr.leviathanstudio.engine.inputs.MouseInput;

/**
 * @author ZeAmateis
 */
public interface IControllable {
    static final float MOUSE_SENSITIVITY = 0.2f;
    static final float CAMERA_POS_STEP = 0.40f;

    void onInput(Window window, MouseInput mouseInput, Keyboard keyboard);

    void onUpdate(float interval, MouseInput mouseInput, Window window);
}
