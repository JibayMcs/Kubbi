package fr.leviathanstudio.engine;

import fr.leviathanstudio.engine.inputs.Keyboard;
import fr.leviathanstudio.engine.inputs.MouseInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameEngine implements Runnable {

    public static final Logger LOGGER = LogManager.getRootLogger();

    public static final int TARGET_FPS = 120;

    public static final int TARGET_UPS = 60;

    private final Window window;

    private final Timer timer;

    private final IGameLogic gameLogic;

    private final MouseInput mouseInput;

    private final Keyboard keyboard;

    private double lastFps;

    private int fps;

    private String windowTitle;

    public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this(windowTitle, 0, 0, vSync, opts, gameLogic);
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this.windowTitle = windowTitle;
        this.window = new Window(windowTitle, width, height, vSync, opts);
        this.mouseInput = new MouseInput();
        this.keyboard = new Keyboard();
        this.gameLogic = gameLogic;
        this.timer = new Timer();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            LOGGER.throwing(excp);
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        LOGGER.debug("Initialize Widow...");
        this.window.init();
        LOGGER.debug("Widow Initialized!");

        LOGGER.debug("Initialize Timer...");
        this.timer.init();
        LOGGER.debug("Timer Initialized!");

        LOGGER.debug("Initialize MouseInput...");
        this.mouseInput.init(window);
        LOGGER.debug("MouseInput Initialized!");

        LOGGER.debug("Initialize Keyboard...");
        this.keyboard.init(window.getWindowHandle());
        LOGGER.debug("Keyboard Initialized!");

        LOGGER.debug("Initialize Game Logic...");
        this.gameLogic.init(window);
        LOGGER.debug("Game Logic Initialized!");

        this.lastFps = this.timer.getTime();
        this.fps = 0;
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    protected void cleanup() {
        this.gameLogic.cleanup();
        LOGGER.debug("Game Logic Cleaned!");
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                LOGGER.throwing(ie);
            }
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput, keyboard);
    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput, window);
    }

    protected void render() {
        if (window.getWindowOptions().showFps && timer.getLastLoopTime() - lastFps > 1) {
            lastFps = timer.getLastLoopTime();
            window.setWindowTitle(windowTitle + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
        gameLogic.render(window);
        window.update();
    }

}
