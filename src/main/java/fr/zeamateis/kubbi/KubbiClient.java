package fr.zeamateis.kubbi;

import fr.leviathanstudio.engine.GameEngine;
import fr.leviathanstudio.engine.IGameLogic;
import fr.leviathanstudio.engine.Window;
import fr.leviathanstudio.engine.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;

public class KubbiClient {
    private static Configuration configuration;
    public static final Logger LOGGER = LogManager.getLogger(KubbiClient.class);

    public static void main(String[] args) throws IOException, DataFormatException {
        try {
            Path configPath = Paths.get("./config/kubbi.conf");
            configuration = new Configuration(configPath);
            LOGGER.debug("Define Configurations...");
            boolean vSync = configuration.getBoolean("client.vSync");
            IGameLogic gameLogic = new KubbiGame(configuration);
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = configuration.getBoolean("client.cullFace");
            opts.showFps = configuration.getBoolean("client.showFps");
            opts.compatibleProfile = configuration.getBoolean("client.compatibleProfile");
            opts.antialiasing = configuration.getBoolean("client.antiAliasing");
            opts.frustumCulling = configuration.getBoolean("client.frustumCulling");
            LOGGER.debug("Configuration defined!");
            GameEngine gameEng = new GameEngine("Kubbi", vSync, opts, gameLogic);
            gameEng.run();
            LOGGER.info("Game Stopped !");
        } catch (Exception excp) {
            LOGGER.throwing(excp);
            System.exit(-1);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
