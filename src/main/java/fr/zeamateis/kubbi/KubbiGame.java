package fr.zeamateis.kubbi;

import fr.leviathanstudio.engine.IGameLogic;
import fr.leviathanstudio.engine.Scene;
import fr.leviathanstudio.engine.SceneLight;
import fr.leviathanstudio.engine.Window;
import fr.leviathanstudio.engine.config.Configuration;
import fr.leviathanstudio.engine.graph.*;
import fr.leviathanstudio.engine.graph.anim.AnimGameItem;
import fr.leviathanstudio.engine.graph.anim.Animation;
import fr.leviathanstudio.engine.graph.lights.DirectionalLight;
import fr.leviathanstudio.engine.graph.lights.PointLight;
import fr.leviathanstudio.engine.graph.particles.FlowParticleEmitter;
import fr.leviathanstudio.engine.graph.particles.Particle;
import fr.leviathanstudio.engine.inputs.Keyboard;
import fr.leviathanstudio.engine.inputs.MouseInput;
import fr.leviathanstudio.engine.items.GameItem;
import fr.leviathanstudio.engine.items.SkyBox;
import fr.leviathanstudio.engine.loaders.assimp.AnimMeshesLoader;
import fr.leviathanstudio.engine.loaders.assimp.StaticMeshesLoader;
import fr.leviathanstudio.engine.loaders.obj.OBJLoader;
import fr.leviathanstudio.engine.resources.Asset;
import fr.leviathanstudio.engine.resources.AssetManager;
import fr.leviathanstudio.engine.resources.Translation;
import fr.zeamateis.kubbi.common.entity.IControllable;
import fr.zeamateis.kubbi.common.entity.Player;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class KubbiGame implements IGameLogic {

    //private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private float angleInc;

    private float lightAngle;

    private boolean firstTime;

    private boolean sceneChanged;

    private Vector3f pointLightPos;

    private Animation animation, cubeAnimation;

    private AnimGameItem animItem, cubeAnimItem;

    private Hud hud = new Hud();

    private FlowParticleEmitter particleEmitter;

    //private final SoundManager soundManager;

    //private enum Sounds {MUSIC, BEEP, FIRE}
    GameItem droid;

    private AssetManager assetManager = new AssetManager("kubbi");
    private final Configuration configurationManager;
    private final Translation translation;

    public KubbiGame(Configuration configurationManager) {
        this.translation = new Translation(assetManager, null);
        this.configurationManager = configurationManager;
        this.renderer = new Renderer();
        this.camera = new Camera();
        // soundManager = new SoundManager();
        this.angleInc = 0;
        this.lightAngle = 90;
        this.firstTime = true;
    }

    @Override
    public void init(Window window) throws Exception {
        // soundManager.init();
        this.hud.init(window);

        this.renderer.init(window);

        this.scene = new Scene();

        //====TERRAIN====//

        float reflectance = 0.5f;

        float blockScale = 1.0f;
        float skyBoxScale = 100.0f;
        float extension = 2.0f;

        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 1;

        float posx = startx;
        float posz = startz;
        float incy;

        ByteBuffer buf;
        int width;
        int height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load("./assets/kubbi/textures/heightmap2.png", w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        int instances = height * width;

        float[] positions = new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};
        Texture texture = new Texture("./assets/kubbi/textures/rock.png");
        Texture normalMap = new Texture("./assets/kubbi/textures/rock_normals.png");

        Material quadMaterial2 = new Material(texture, reflectance);
        quadMaterial2.setNormalMap(normalMap);
        Mesh cubeMesh = new Mesh(positions, textCoords, textCoords, indices);

        cubeMesh.setMaterial(quadMaterial2);
        List<GameItem> gameItems = new ArrayList<>(instances);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                GameItem gameItem = new GameItem(cubeMesh);
                gameItem.setScale(blockScale);
                int rgb = HeightMapMesh.getRGB(i, j, width, buf);
                incy = rgb / (10 * 255 * 255);
                gameItem.setPosition(posx, starty + incy, posz);
                int textPos = Math.random() > 0.5f ? 0 : 1;
                gameItem.setTextPos(textPos);
                gameItems.add(i * width + j, gameItem);

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }

        //====END TERRAIN====//

        this.animItem = AnimMeshesLoader.loadAnimGameItem("./assets/kubbi/models/bob/boblamp.md5mesh", "./assets/kubbi/");
        this.animItem.setScale(0.05f);
        this.animation = this.animItem.getCurrentAnimation();
        this.animItem.setPosition(10, 0, 0);

        GameItem bus = new GameItem(StaticMeshesLoader.load("./assets/kubbi/models/bus.dae", "./assets/kubbi/models/"));
        bus.setPosition(15, 0, 0);

        Asset droidModel = new Asset("droidModel", "models/test.dae");
        this.assetManager.registerAsset(droidModel);

        this.droid = new Player(this.camera, StaticMeshesLoader.load(this.assetManager, droidModel));
        //droid.setPosition(20, 0, 0);

        GameItem record = new GameItem(StaticMeshesLoader.load("./assets/kubbi/models/recorder.dae", "./assets/kubbi/models/"));
        record.setPosition(25, 0, 0);

        this.cubeAnimItem = AnimMeshesLoader.loadAnimGameItem("./assets/kubbi/models/cube.dae", ".");
        this.cubeAnimation = this.cubeAnimItem.getCurrentAnimation();
        this.cubeAnimItem.setPosition(30, 0, 0);

        List<GameItem> gameItemList = new ArrayList<>();
        gameItemList.add(animItem);
        gameItemList.add(droid);
        gameItemList.add(bus);
        gameItemList.add(record);
        gameItemList.add(cubeAnimItem);
        gameItemList.addAll(gameItems);
        this.scene.setGameItems(gameItemList);


        int maxParticles = 200;
        Vector3f particleSpeed = new Vector3f(0, 1, 0);
        particleSpeed.mul(2.5f);
        long ttl = 4000;
        long creationPeriodMillis = 300;
        float range = 0.2f;
        float scale = 1.0f;

        Mesh partMesh = OBJLoader.loadMesh("./assets/kubbi/models/particle.obj", maxParticles);
        Texture particleTexture = new Texture("./assets/kubbi/textures/particle_anim.png", 4, 4);
        Material partMaterial = new Material(particleTexture, reflectance);
        partMesh.setMaterial(partMaterial);
        Particle particle = new Particle(partMesh, particleSpeed, ttl, 100);
        particle.setScale(scale);
        this.particleEmitter = new FlowParticleEmitter(particle, maxParticles, creationPeriodMillis);
        this.particleEmitter.setActive(true);
        this.particleEmitter.setPositionRndRange(range);
        this.particleEmitter.setSpeedRndRange(range);
        this.particleEmitter.setAnimRange(10);
        //this.scene.setParticleEmitters(new FlowParticleEmitter[]{particleEmitter});

        // Shadows
        this.scene.setRenderShadows(true);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        // scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("./assets/kubbi/models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        skyBox.setScale(skyBoxScale);
        this.scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        //soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        //setupSounds();

        this.scene.saveScene();

    }

   /* private void setupSounds() throws Exception {
        SoundBuffer buffBack = new SoundBuffer("/sounds/background.ogg");
        soundManager.addSoundBuffer(buffBack);
        SoundSource sourceBack = new SoundSource(true, true);
        sourceBack.setBuffer(buffBack.getBufferId());
        soundManager.addSoundSource(Sounds.MUSIC.toString(), sourceBack);

        SoundBuffer buffBeep = new SoundBuffer("/sounds/beep.ogg");
        soundManager.addSoundBuffer(buffBeep);
        SoundSource sourceBeep = new SoundSource(false, true);
        sourceBeep.setBuffer(buffBeep.getBufferId());
        soundManager.addSoundSource(Sounds.BEEP.toString(), sourceBeep);

        SoundBuffer buffFire = new SoundBuffer("/sounds/fire.ogg");
        soundManager.addSoundBuffer(buffFire);
        SoundSource sourceFire = new SoundSource(true, false);
        Vector3f pos = particleEmitter.getBaseParticle().getPosition();
        sourceFire.setPosition(pos);
        sourceFire.setBuffer(buffFire.getBufferId());
        soundManager.addSoundSource(Sounds.FIRE.toString(), sourceFire);
        sourceFire.play();

        soundManager.setListener(new SoundListener(new Vector3f()));

        sourceBack.play();
    }*/

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        this.scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.0f, 0.0f, 0.0f));
        sceneLight.setSkyBoxLight(new Vector3f(0.0f, 0.0f, 0.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        this.pointLightPos = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), pointLightPos, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);

        Vector3f pointLightColorRed = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f pointLightColorGreen = new Vector3f(0.0f, 1.0f, 0.0f);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(1, 0.0f, 0);
        PointLight pointLightRed = new PointLight(pointLightColorRed, new Vector3f(-25, 1, 0), lightIntensity, attenuation);
        PointLight pointLightGreen = new PointLight(pointLightColorGreen, new Vector3f(25, 1, 0), lightIntensity, attenuation);
        sceneLight.setPointLightList(new PointLight[]{pointLightRed, pointLightGreen});
    }

    @Override
    public void input(Window window, MouseInput mouseInput, Keyboard keyboard) {
        this.sceneChanged = false;

        if (this.droid instanceof IControllable)
            ((IControllable) this.droid).onInput(window, mouseInput, keyboard);

        /*if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            sceneChanged = true;
            angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            sceneChanged = true;
            angleInc += 0.05f;
        } else {
            angleInc = 0;
        }
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            sceneChanged = true;
            pointLightPos.y += 0.5f;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            sceneChanged = true;
            pointLightPos.y -= 0.5f;
        }*/
    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {

        if (this.droid instanceof IControllable)
            ((IControllable) this.droid).onUpdate(interval, mouseInput, window);


        this.animation.nextFrame();
        this.cubeAnimation.nextFrame();

        this.lightAngle += this.angleInc;
        if (this.lightAngle < 0) {
            this.lightAngle = 0;
        } else if (this.lightAngle > 180) {
            this.lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();

        // Update view matrix
        this.camera.updateViewMatrix();

        //particleEmitter.update((long) (interval * 1000));

        //soundManager.updateListenerPosition(camera);
    }

    @Override
    public void render(Window window) {
        if (this.firstTime) {
            this.sceneChanged = true;
            this.firstTime = false;
        }
        this.renderer.render(window, camera, scene, sceneChanged);
        //hud.render(window);
    }

    @Override
    public void cleanup() {
        this.renderer.cleanup();
        //soundManager.cleanup();
        this.scene.cleanup();
        if (this.hud != null) {
            this.hud.cleanup();
        }
    }
}