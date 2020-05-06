package fr.leviathanstudio.engine.resources;

/**
 * @author ZeAmateis
 */
public class Asset {

    private final String assetName, assetPath;

    public Asset(String assetName, String assetPath) {
        this.assetName = assetName;
        this.assetPath = assetPath;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetPath() {
        return assetPath;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "assetName='" + assetName + '\'' +
                ", assetPath='" + assetPath + '\'' +
                '}';
    }
}
