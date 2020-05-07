package fr.zeamateis.kubbi.common.entity;

import fr.leviathanstudio.engine.Window;
import fr.leviathanstudio.engine.graph.Camera;
import fr.leviathanstudio.engine.graph.Mesh;
import fr.leviathanstudio.engine.inputs.Keyboard;
import fr.leviathanstudio.engine.inputs.MouseInput;
import fr.leviathanstudio.engine.items.GameItem;
import org.joml.Vector2f;

/**
 * @author ZeAmateis
 */
public class Player extends GameItem implements IControllable {

    private final Camera camera;

    public Player(Camera camera, Mesh[] meshes) {
        super(meshes);
        this.camera = camera;

        camera.getPosition().x = this.getPosition().x + 0.5F;
        camera.getPosition().y = this.getPosition().y + 3.5F;
        camera.getPosition().z = this.getPosition().z - 2.5F;
        camera.getRotation().x = 0f;
        camera.getRotation().y = 180f;
    }

    @Override
    public void onInput(Window window, MouseInput mouseInput, Keyboard keyboard) {
        this.camera.getCameraInc().set(0, 0, 0);
        if (keyboard.isKeyPressed(Keyboard.Z)) {
            this.camera.getCameraInc().z = -2;
        } else if (keyboard.isKeyPressed(Keyboard.S)) {
            this.camera.getCameraInc().z = 2;
        }
        if (keyboard.isKeyPressed(Keyboard.Q)) {
            this.camera.getCameraInc().x = -2;
        } else if (keyboard.isKeyPressed(Keyboard.D)) {
            this.camera.getCameraInc().x = 2;
        }
        if (keyboard.isKeyPressed(Keyboard.LEFT_SHIFT)) {
            this.camera.getCameraInc().y = -2;
        } else if (keyboard.isKeyPressed(Keyboard.SPACE)) {
            this.camera.getCameraInc().y = 2;
        }
    }

    @Override
    public void onUpdate(float interval, MouseInput mouseInput, Window window) {
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(
                    rotVec.x * MOUSE_SENSITIVITY,
                    rotVec.y * MOUSE_SENSITIVITY,
                    0
            );

            // this.getRotation().x -= rotVec.x * (MOUSE_SENSITIVITY / 2);
            // this.getRotation().y += rotVec.y * (MOUSE_SENSITIVITY / 2);
        }

        // Update camera position
        camera.movePosition(
                camera.getCameraInc().x * CAMERA_POS_STEP,
                camera.getCameraInc().y * CAMERA_POS_STEP,
                camera.getCameraInc().z * CAMERA_POS_STEP
        );
    }
}
