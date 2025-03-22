package com.jakesiewjk;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class CamController extends InputAdapter {
  private final Camera camera;
  private boolean thirdPersonMode = true;
  private final Vector3 offset = new Vector3();
  private float distance = 5f;

  public CamController(Camera camera) {
    this.camera = camera;
    offset.set(0, 2, -3);
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
      offset.set(viewDirection).scl(-1);
      offset.y = Math.max(0, offset.y);
      offset.nor().scl(distance);
      camera.position.add(offset);

      camera.lookAt(playerPosition);
      camera.up.set(Vector3.Y);
    } else {
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
