package fr.zeamateis.kubbi;

import fr.leviathanstudio.engine.*;
import fr.leviathanstudio.engine.graph.*;
import fr.leviathanstudio.engine.graph.anim.AnimGameItem;
import fr.leviathanstudio.engine.graph.anim.Animation;
import fr.leviathanstudio.engine.graph.lights.DirectionalLight;
import fr.leviathanstudio.engine.graph.lights.PointLight;
import fr.leviathanstudio.engine.graph.particles.FlowParticleEmitter;
import fr.leviathanstudio.engine.graph.particles.Particle;
import fr.leviathanstudio.engine.items.GameItem;
import fr.leviathanstudio.engine.items.SkyBox;
import fr.leviathanstudio.engine.loaders.assimp.AnimMeshesLoader;
import fr.leviathanstudio.engine.loaders.assimp.StaticMeshesLoader;
import fr.leviathanstudio.engine.loaders.obj.OBJLoader;
import fr.leviathanstudio.engine.sound.SoundBuffer;
import fr.leviathanstudio.engine.sound.SoundListener;
import fr.leviathanstudio.engine.sound.SoundManager;
import fr.leviathanstudio.engine.sound.SoundSource;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL11;

import static org.lwjgl.glfw.GLFW.*;

public class KubbiGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private static final float CAMERA_POS_STEP = 0.40f;

    private float angleInc;

    private float lightAngle;

    private boolean firstTime;

    private boolean sceneChanged;

    private Vector3f pointLightPos;

    private Animation animation;

    private AnimGameItem animItem;

    private Hud hud = new Hud();

    private FlowParticleEmitter particleEmitter;

    private final SoundManager soundManager;

    private enum Sounds {MUSIC, BEEP, FIRE}


    public KubbiGame() {
        renderer = new Renderer();
        camera = new Camera();
        soundManager = new SoundManager();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 90;
        firstTime = true;
    }

    @Override
    public void init(Window window) throws Exception {
        soundManager.init();
        hud.init(window);

        renderer.init(window);

        scene = new Scene();

        Mesh[] houseMesh = StaticMeshesLoader.load("models/house/house.obj", "models/house");
        GameItem house = new GameItem(houseMesh);

        Mesh[] terrainMesh = StaticMeshesLoader.load("models/terrain/terrain.obj", "models/terrain");
        GameItem terrain = new GameItem(terrainMesh);
        terrain.setScale(100.0f);

        animItem = AnimMeshesLoader.loadAnimGameItem("models/bob/boblamp.md5mesh", ".");
        animItem.setScale(0.05f);
        animation = animItem.getCurrentAnimation();
        animItem.setPosition(50, 0, 0);

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
        Texture texture = new Texture("./textures/rock.png");
        Texture normalMap = new Texture("./textures/rock_normals.png");

        float reflectance = 1.0f;
        Material quadMaterial2 = new Material(texture, reflectance);
        quadMaterial2.setNormalMap(normalMap);
        Mesh cubeMesh = new Mesh(positions, textCoords, textCoords, indices);

        cubeMesh.setMaterial(quadMaterial2);

        GameItem cubeTest = new GameItem(cubeMesh);

        scene.setGameItems(new GameItem[]{terrain, animItem, cubeTest});

        int maxParticles = 200;
        Vector3f particleSpeed = new Vector3f(0, 1, 0);
        particleSpeed.mul(2.5f);
        long ttl = 4000;
        long creationPeriodMillis = 300;
        float range = 0.2f;
        float scale = 1.0f;

        Mesh partMesh = OBJLoader.loadMesh("./models/particle.obj", maxParticles);
        Texture particleTexture = new Texture("./textures/particle_anim.png", 4, 4);
        Material partMaterial = new Material(particleTexture, reflectance);
        partMesh.setMaterial(partMaterial);
        Particle particle = new Particle(partMesh, particleSpeed, ttl, 100);
        particle.setScale(scale);
        particleEmitter = new FlowParticleEmitter(particle, maxParticles, creationPeriodMillis);
        particleEmitter.setActive(true);
        particleEmitter.setPositionRndRange(range);
        particleEmitter.setSpeedRndRange(range);
        particleEmitter.setAnimRange(10);
        this.scene.setParticleEmitters(new FlowParticleEmitter[]{particleEmitter});

        // Shadows
        scene.setRenderShadows(true);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        // scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox
        float skyBoxScale = 100.0F;
        SkyBox skyBox = new SkyBox("models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        skyBox.setScale(skyBoxScale);
        //scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        camera.getPosition().x = -17.0f;
        camera.getPosition().y = 17.0f;
        camera.getPosition().z = -30.0f;
        camera.getRotation().x = 20.0f;
        camera.getRotation().y = 140.f;

        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        setupSounds();
    }

    private void setupSounds() throws Exception {
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
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);

        pointLightPos = new Vector3f(0.0f, 25.0f, 0.0f);
        Vector3f pointLightColour = new Vector3f(0.0f, 1.0f, 0.0f);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(1, 0.0f, 0);
        PointLight pointLight = new PointLight(pointLightColour, pointLightPos, lightIntensity, attenuation);
        sceneLight.setPointLightList(new PointLight[]{pointLight});
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        sceneChanged = false;
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            sceneChanged = true;
            cameraInc.z = -2;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            sceneChanged = true;
            cameraInc.z = 2;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            sceneChanged = true;
            cameraInc.x = -2;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            sceneChanged = true;
            cameraInc.x = 2;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            sceneChanged = true;
            cameraInc.y = -2;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            sceneChanged = true;
            cameraInc.y = 2;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
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
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse            
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            sceneChanged = true;
        }

        animation.nextFrame();


        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();

        // Update view matrix
        camera.updateViewMatrix();

        particleEmitter.update((long) (interval * 1000));

        soundManager.updateListenerPosition(camera);
    }

    @Override
    public void render(Window window) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, sceneChanged);
        hud.render(window);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        soundManager.cleanup();

        scene.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }
}
