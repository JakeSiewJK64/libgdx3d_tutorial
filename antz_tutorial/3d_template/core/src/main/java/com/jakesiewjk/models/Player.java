package com.jakesiewjk.models;

import com.badlogic.gdx.Gdx;
import com.jakesiewjk.controller.CameraController;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class Player extends GameObject {

  private final CameraController cameraController;

  public Player(Scene scene, SceneManager sceneManager) {
    super(scene, sceneManager);
    cameraController = new CameraController();
    sceneManager.setCamera(cameraController.getCamera());

    // pass control to camera controller
    Gdx.input.setInputProcessor(cameraController);
  }
}
