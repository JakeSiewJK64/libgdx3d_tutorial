package com.jakesiewjk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Disposable;
import com.jakesiewjk.constants.Settings;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GameView implements Disposable {

  private final SceneManager sceneManager;
  private final SceneAsset sceneAssets;

  public GameView() {
    sceneManager = new SceneManager();

    // load scene assets
    sceneAssets = getSceneAssets();

    // set skybox
    sceneManager.setSkyBox(getSkybox());
  }

  private SceneAsset getSceneAssets() {
    SceneAsset assets = new GLTFLoader().load(Gdx.files.internal(Settings.GLTF_FILE));

    for (Node node : assets.scene.model.nodes) {
      Gdx.app.log("DEBUG", node.id);
    }

    return assets;
  }

  private DirectionalLightEx getDirectionalLightEx() {
    DirectionalLightEx light = new DirectionalShadowLight(Settings.shadowMapSize, Settings.shadowMapSize)
        .setViewport(50, 50, 10, 100);
    light.direction.set(1, -3, 1).nor();
    light.color.set(Color.WHITE);
    light.intensity = 3f;
    return light;
  }

  // image based lighting
  private SceneSkybox getSkybox() {
    DirectionalLightEx light = getDirectionalLightEx();
    IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
    Cubemap environmentCubemap = iblBuilder.buildEnvMap(1024);
    SceneSkybox skybox = new SceneSkybox(environmentCubemap);
    return skybox;
  }

  @Override
  public void dispose() {
    sceneManager.dispose();
  }
}
