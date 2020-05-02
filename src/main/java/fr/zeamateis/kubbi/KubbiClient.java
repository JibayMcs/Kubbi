package fr.zeamateis.kubbi;

import fr.leviathanstudio.engine.GameEngine;
import fr.leviathanstudio.engine.IGameLogic;
import fr.leviathanstudio.engine.Window;

public class KubbiClient {

    public static void main(String[] args) {
        try {
            boolean vSync = false;
            IGameLogic gameLogic = new KubbiGame();
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = false;
            opts.showFps = true;
            opts.compatibleProfile = true;
            opts.antialiasing = true;
            opts.frustumCulling = true;
            GameEngine gameEng = new GameEngine("Kubbi", vSync, opts, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}
