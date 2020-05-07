package fr.zeamateis.kubbi;

import fr.leviathanstudio.engine.GameEngine;
import fr.leviathanstudio.engine.IGameLogic;
import fr.leviathanstudio.engine.Window;
import fr.leviathanstudio.engine.config.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

public class KubbiClient {
    private static Configuration configuration;

    public static void main(String[] args) {
        try {
            Path configPath = Paths.get("./config/kubbi.conf");
            Configuration configuration = new Configuration(configPath);

            boolean vSync = configuration.getBoolean("client.vSync");
            IGameLogic gameLogic = new KubbiGame(configuration);
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = configuration.getBoolean("client.cullFace");
            opts.showFps = configuration.getBoolean("client.showFps");
            opts.compatibleProfile = configuration.getBoolean("client.compatibleProfile");
            opts.antialiasing = configuration.getBoolean("client.antiAliasing");
            opts.frustumCulling = configuration.getBoolean("client.frustumCulling");
            GameEngine gameEng = new GameEngine("Kubbi", vSync, opts, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
