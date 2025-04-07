package com.jakesiewjk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class CameraController extends InputAdapter {

  private float bobAngle = 0;
  private final PerspectiveCamera camera;
  private final IntIntMap keys;
  private final Vector3 fwdHorizontal;
  private final Vector3 tmp = new Vector3();
  private final Vector3 tmp2 = new Vector3();
  private final Vector3 tmp3 = new Vector3();

  public CameraController() {
    fwdHorizontal = new Vector3();
    keys = new IntIntMap();
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(10f, Settings.eyeHeight, 5f);

    Gdx.input.setCursorCatched(true);
    Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
  }

  private void moveForward(float distance) {
    fwdHorizontal.set(camera.direction).y = 0;
    fwdHorizontal.nor();
    fwdHorizontal.scl(distance);
    camera.position.add(fwdHorizontal);
  }

  private float bobHeight(float speed, float deltaTime) {
    if (Math.abs(speed) < 0.1f) {
      return 0f;
    }

    bobAngle += deltaTime * speed * 0.5f * Math.PI / Settings.headBobDuration;

    return (float) (Settings.headBobHeight * Math.sin(bobAngle));
  }

  private void strafe(float distance) {
    fwdHorizontal.set(camera.direction).y = 0;
    fwdHorizontal.nor();
    tmp.set(fwdHorizontal).crs(camera.up).nor().scl(distance);
    camera.position.add(tmp);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    float deltaX = -Gdx.input.getDeltaX() * Settings.degreesPerPixel;
    float deltaY = -Gdx.input.getDeltaY() * Settings.degreesPerPixel;

    camera.direction.rotate(camera.up, deltaX);

    // avoid gimbal lock when looking straight up or down
    Vector3 oldPitchAxis = tmp.set(camera.direction).crs(camera.up).nor();
    Vector3 newDirection = tmp2.set(camera.direction).rotate(tmp, deltaY);
    Vector3 newPitchAxis = tmp3.set(tmp2).crs(camera.up);

    if (!newPitchAxis.hasOppositeDirection(oldPitchAxis)) {
      camera.direction.set(newDirection);
    }

    return true;
  }

  @Override
  public boolean keyDown(int keycode) {
    keys.put(keycode, keycode);
    return true;
  }

  @Override
  public boolean keyUp(int keycode) {
    keys.remove(keycode, 0);
    return true;
  }

  public void update(float deltaTime) {
    float bobSpeed = 0;

    if (keys.containsKey(Settings.forwardKey)) {
      moveForward(deltaTime * Settings.walkSpeed);
      bobSpeed = Settings.walkSpeed;
    }

    if (keys.containsKey(Settings.backwardKey)) {
      moveForward(-deltaTime * Settings.walkSpeed);
      bobSpeed = Settings.walkSpeed;
    }

    if (keys.containsKey(Settings.strafeLeftKey)) {
      strafe(-deltaTime * Settings.walkSpeed);
      bobSpeed = Settings.walkSpeed;
    }

    if (keys.containsKey(Settings.strafeRightKey)) {
      strafe(deltaTime * Settings.walkSpeed);
      bobSpeed = Settings.walkSpeed;
    }

    camera.position.y = Settings.eyeHeight + bobHeight(bobSpeed, deltaTime);
    camera.update(true);
  }

  public Camera getCamera() {
    return camera;
  }
}
