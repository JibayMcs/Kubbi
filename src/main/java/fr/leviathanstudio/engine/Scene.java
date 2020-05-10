package fr.leviathanstudio.engine;

import fr.leviathanstudio.engine.data.DataStorageUtils;
import fr.leviathanstudio.engine.data.JsonStorageUtils;
import fr.leviathanstudio.engine.graph.InstancedMesh;
import fr.leviathanstudio.engine.graph.Mesh;
import fr.leviathanstudio.engine.graph.particles.IParticleEmitter;
import fr.leviathanstudio.engine.graph.weather.Fog;
import fr.leviathanstudio.engine.items.GameItem;
import fr.leviathanstudio.engine.items.SkyBox;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private final Map<Mesh, List<GameItem>> meshMap;

    private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;

    private SkyBox skyBox;

    private SceneLight sceneLight;

    private Fog fog;

    private boolean renderShadows;

    private IParticleEmitter[] particleEmitters;

    private List<GameItem> gameItems = new ArrayList<>();

    public Scene() {
        meshMap = new HashMap();
        instancedMeshMap = new HashMap();
        fog = Fog.NOFOG;
        renderShadows = true;
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getGameInstancedMeshes() {
        return instancedMeshMap;
    }

    public boolean isRenderShadows() {
        return renderShadows;
    }

    public void setGameItems(List<GameItem> gameItems) {
        // Create a map of meshes to speed up rendering
        int numGameItems = gameItems != null ? gameItems.size() : 0;
        for (int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems.get(i);
            this.gameItems.add(gameItem);

            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                boolean instancedMesh = mesh instanceof InstancedMesh;
                List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
                if (list == null) {
                    list = new ArrayList<>();
                    if (instancedMesh) {
                        instancedMeshMap.put((InstancedMesh) mesh, list);
                    } else {
                        meshMap.put(mesh, list);
                    }
                }
                list.add(gameItem);

            }
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
        for (Mesh mesh : instancedMeshMap.keySet()) {
            mesh.cleanUp();
        }
        if (particleEmitters != null) {
            for (IParticleEmitter particleEmitter : particleEmitters) {
                particleEmitter.cleanup();
            }
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    /**
     * @return the fog
     */
    public Fog getFog() {
        return fog;
    }

    /**
     * @param fog the fog to set
     */
    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        this.particleEmitters = particleEmitters;
    }

    public void saveScene() {
        Thread thread = new Thread(() -> {
            List<DataGameItem> dataGameItems = new ArrayList<>();
            if (!gameItems.isEmpty()) {
                for (int i = 0; i < gameItems.size(); i++) {
                    DataGameItem dataGameItem = new DataGameItem(gameItems.get(i));
                    dataGameItems.add(dataGameItem);
                }
                System.out.println("Number Of Game Objects: " + gameItems.size());
                JsonStorageUtils.compressToFile(dataGameItems, Paths.get("./testmap.mapp"));
                DataStorageUtils.decompressToFile(Paths.get("./testmap.mapp"), Paths.get("./testmap-decompressed.json"));

            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    static class DataGameItem {
        private final boolean selected;

        //private final Mesh[] meshes;

        private final float[] position;

        private final float scale;

        private final float[] rotation;

        private final int textPos;

        private final boolean disableFrustumCulling;

        private final boolean insideFrustum;

        public DataGameItem(GameItem gameItemIn) {
            this.selected = gameItemIn.isSelected();
            this.position = new float[]{gameItemIn.getPosition().x, gameItemIn.getPosition().z, gameItemIn.getPosition().z};
            this.scale = gameItemIn.getScale();
            this.rotation = new float[]{gameItemIn.getRotation().w, gameItemIn.getRotation().x, gameItemIn.getRotation().y, gameItemIn.getRotation().z};
            this.textPos = gameItemIn.getTextPos();
            this.disableFrustumCulling = gameItemIn.isDisableFrustumCulling();
            this.insideFrustum = gameItemIn.isInsideFrustum();
        }
    }
}
