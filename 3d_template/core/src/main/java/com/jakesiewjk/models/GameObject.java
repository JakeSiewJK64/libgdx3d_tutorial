package com.jakesiewjk.models;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class GameObject {
  private final Scene scene;
  private final SceneManager sceneManager;

  public GameObject(Scene scene, SceneManager sceneManager) {
    this.scene = scene;
    this.sceneManager = sceneManager;
  }

  public Scene getScene() {
    return scene;
  }

  public SceneManager getSceneManager() {
    return sceneManager;
  }
}
