package fr.leviathanstudio.engine.resources;

import fr.leviathanstudio.engine.GameEngine;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZeAmateis
 */
public class AssetManager {

    private final String assetsDir = "assets";

    private final String gameIdentifier;
    private Map<String, Asset> assetsList = new HashMap<>();

    public AssetManager(String gameIdentifier) {
        this.gameIdentifier = gameIdentifier;
    }


    public Map<String, Asset> getAssets() {
        return assetsList;
    }

    public void registerAsset(Asset assetIn) {
        try {
            if (assetPathExist(assetIn)) {
                this.assetsList.put(assetIn.getAssetName(), assetIn);
            } else throw new FileNotFoundException(String.format("Asset [%s] was not found !", assetIn.toString()));
        } catch (FileNotFoundException ex) {
            GameEngine.LOGGER.throwing(ex);
        }

    }

    public void registerAssets(Asset... assetsIn) throws FileNotFoundException {
        for (Asset asset : assetsIn) {
            this.registerAsset(asset);
        }
    }

    public void unRegisterAsset(Asset assetIn) {
        this.assetsList.remove(assetIn.getAssetName());
    }

    public void unRegisterAsset(String assetNameIn) {
        this.assetsList.remove(assetNameIn);
    }

    public boolean assetPathExist(Asset assetIn) {
        return getAssetPath(assetIn).toFile().exists();
    }

    public Path getAssetPath(Asset assetIn) {
        return Paths.get(this.assetsDir, this.gameIdentifier, assetIn.getAssetPath());
    }

    public Path getAssetRootPath() {
        return Paths.get(this.assetsDir, this.gameIdentifier);
    }

    public Asset getAsset(String assetNameIn) {
        return isAssetRegistered(assetNameIn) ? assetsList.get(assetNameIn) : null;
    }

    public boolean isAssetRegistered(Asset assetIn) {
        return isAssetRegistered(assetIn.getAssetName());
    }

    public boolean isAssetRegistered(String assetNameIn) {
        return assetsList.get(assetNameIn) != null;
    }
}
