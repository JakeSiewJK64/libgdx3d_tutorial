package com.jakesiewjk.controllers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.jakesiewjk.Settings;

public class CamController extends InputAdapter {
  private final Camera camera;
  private boolean thirdPersonMode = true;
  private float distance = 5f;

  public CamController(Camera camera) {
    this.camera = camera;
  }

  public void setThirdPersonMode(boolean mode) {
    thirdPersonMode = mode;
  }

  public boolean getThirdPersonMode() {
    return thirdPersonMode;
  }

  public void update(Vector3 playerPosition, Vector3 viewDirection) {
    camera.position.set(playerPosition);

    if (thirdPersonMode) {
      // offset camera from player position
      Vector3 offset = new Vector3();
      offset.set(viewDirection).scl(-1);
      offset.y = Math.max(0, offset.y);
      offset.nor().scl(distance);
      camera.position.add(offset);

      camera.lookAt(playerPosition);
      camera.up.set(Vector3.Y);
    } else {
      // Push camera slightly back along the view direction
      Vector3 pushBack = new Vector3(viewDirection).scl(Settings.camDistanceFromFront);
      camera.position.add(pushBack);
      camera.direction.set(viewDirection);
    }

    camera.update(true);
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return zoom(amountY);
  }

  private boolean zoom(float amount) {
    if (amount < 0 && distance < 5f) {
      return false;
    }

    if (amount > 0 && distance > 30f) {
      return false;
    }

    distance += amount;
    return true;
  }
}
