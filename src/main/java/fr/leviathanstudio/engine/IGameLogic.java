package fr.leviathanstudio.engine;

import fr.leviathanstudio.engine.inputs.Keyboard;
import fr.leviathanstudio.engine.inputs.MouseInput;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput, Keyboard keyboard);

    void update(float interval, MouseInput mouseInput, Window window);

    void render(Window window);

    void cleanup();
}