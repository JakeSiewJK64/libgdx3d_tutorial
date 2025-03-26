package com.jakesiewjk.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jakesiewjk.Settings;
import com.jakesiewjk.World;
import com.jakesiewjk.controllers.CamController;

import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GameView implements Disposable {

  private final World world;
  private final SceneManager sceneManager;
  private final PerspectiveCamera camera;
  private CamController cameraController;
  private Cubemap diffuseCubemap;
  private Cubemap environmentCubemap;
  private Cubemap specularCubemap;
  private Texture brdfLUT;
  private SceneSkybox skybox;
  private float bobAngle;

  public GameView(World world) {
    this.world = world;
    sceneManager = new SceneManager();

    // Prepare your screen here.
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
    camera.position.set(10f, 1.5f, 5.f);
    camera.lookAt(0, Settings.eyeHeight, 0);
    camera.near = 1f;
    camera.far = 300f;
    camera.update();

    cameraController = new CamController(camera);
    sceneManager.setCamera(camera);

    Gdx.input.setInputProcessor(cameraController);
    Gdx.input.setCursorCatched(true);
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

    // setup light
    // DirectionalLightEx light = new DirectionalLightEx();
    DirectionalLightEx light = new DirectionalShadowLight(Settings.shadowMapSize, Settings.shadowMapSize)
        .setViewport(50, 50, 10, 100);
    light.direction.set(1, -3, 1).nor();
    light.color.set(Color.WHITE);
    light.intensity = 3f;
    sceneManager.environment.add(light);

    // image based lighting
    IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
    environmentCubemap = iblBuilder.buildEnvMap(1024);
    diffuseCubemap = iblBuilder.buildIrradianceMap(256);
    specularCubemap = iblBuilder.buildIrradianceMap(10);
    iblBuilder.dispose();

    // This texture is provided by the library, no need to have it in your assets.
    brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

    sceneManager.setAmbientLight(1f);
    sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
    sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
    sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

    // skybox
    skybox = new SceneSkybox(environmentCubemap);
    sceneManager.setSkyBox(skybox);
  }

  public PerspectiveCamera getCamera() {
    return camera;
  }

  public void refresh() {
    sceneManager.getRenderableProviders().clear();

    // add scene for each game object
    int num = world.getNumGameObjects();

    for (int i = 0; i < num; i++) {
      Scene scene = world.getGameObject(i).scene;

      if (world.getGameObject(i).visible) {
        sceneManager.addScene(scene, false);
      }
    }
  }

  public void resize(int width, int height) {
    sceneManager.updateViewport(width, height);
  }

  public void render(float delta) {

    if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
      boolean thirdPersonView = !cameraController.getThirdPersonMode();
      cameraController.setThirdPersonMode(thirdPersonView);

      world.getPlayer().visible = thirdPersonView;
      refresh();
    }

    cameraController.update(world.getPlayer().getPosition(), world.getPlayerController().getViewingDirection());
    addHeadBob(delta);
    camera.update();

    if (world.isDirty()) {
      refresh();
    }

    sceneManager.update(delta);

    // render
    ScreenUtils.clear(Color.PURPLE, true);
    sceneManager.render();
  }

  private void addHeadBob(float dt) {
    float speed = world.getPlayer().body.getVelocity().len();
    bobAngle += speed * dt * Math.PI / Settings.headBobDuration;
    camera.position.y += Settings.headBobScale * Settings.headBobHeight * (float) Math.sin(bobAngle);
  }

  public CamController getCameraController() {
    return cameraController;
  }

  @Override
  public void dispose() {
    sceneManager.dispose();
    environmentCubemap.dispose();
    diffuseCubemap.dispose();
    specularCubemap.dispose();
    brdfLUT.dispose();
    skybox.dispose();
  }
}
