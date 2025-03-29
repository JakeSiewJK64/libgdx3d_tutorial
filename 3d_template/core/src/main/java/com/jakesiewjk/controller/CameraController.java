package com.jakesiewjk.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.jakesiewjk.constants.Settings;

public class CameraController extends InputAdapter {
  private final PerspectiveCamera perspectiveCamera;
  private final PerspectiveCamera camera;

  public CameraController() {
    camera = new PerspectiveCamera();
    perspectiveCamera = new PerspectiveCamera(Settings.CAMERA_FOV, Gdx.graphics.getWidth(),
        Gdx.graphics.getHeight());

    Gdx.input.setCursorCatched(true);
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
  }

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
      case Input.Keys.ESCAPE:
        Gdx.app.exit();
        break;
      default:
        break;
    }

    return true;
  }

  public PerspectiveCamera getCamera() {
    return camera;
  }

  public PerspectiveCamera getPerspectiveCamera() {
    return perspectiveCamera;
  }
}
