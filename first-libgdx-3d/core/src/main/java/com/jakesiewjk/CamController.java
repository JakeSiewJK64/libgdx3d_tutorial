package com.jakesiewjk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class CamController extends InputAdapter {
  public int forwardKey = Input.Keys.W;
  public int backwardKey = Input.Keys.S;
  public int strafeLeftKey = Input.Keys.A;
  public int strafeRightKey = Input.Keys.D;
  public int turnLeftKey = Input.Keys.Q;
  public int turnRightKey = Input.Keys.E;
  public int runShiftKey = Input.Keys.SHIFT_LEFT;
  public int jumpKey = Input.Keys.SPACE;

  protected final Vector3 fwdHorizontal = new Vector3();
  protected final Vector3 tmp = new Vector3();
  protected final Camera camera;
  protected final IntIntMap keys = new IntIntMap();
  protected float degreesPerPixel = .1f;

  final Vector3 tmp2 = new Vector3();
  final Vector3 tmp3 = new Vector3();

  private boolean isJumping = false;
  private float jumpH = 0;
  private float jumpV = 0;
  private float bobAngle = 0;

  public CamController(Camera camera) {
    this.camera = camera;
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

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    if (Gdx.input.getDeltaX() == screenX && Gdx.input.getDeltaY() == screenY) {
      return true;
    }

    float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
    float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;

    if (!Settings.invertLook) {
      deltaY = -deltaY;
    }

    if (!Settings.freeLook) {
      deltaY = 0;
      camera.direction.y = 0;
    }

    rotateView(deltaX, deltaY);
    return true;
  }

  private float bobHeight(float speed, float deltaTime) {

    if (Math.abs(speed) < .1f) {
      return 0f;
    }

    bobAngle += deltaTime * speed * .5f * Math.PI / Settings.headBobDuration;

    return (float) (Settings.headBobHeight * Math.sin(bobAngle));
  }

  private void rotateView(float deltaX, float deltaY) {
    camera.direction.rotate(camera.up, deltaX);

    // avoid gimbal lock when looking straight up/down
    Vector3 oldPitchAxis = tmp.set(camera.direction).crs(camera.up).nor();
    Vector3 newDirection = tmp2.set(camera.direction).rotate(tmp, deltaY);
    Vector3 newPitchAxis = tmp3.set(tmp2).crs(camera.up);

    if (!newPitchAxis.hasOppositeDirection(oldPitchAxis)) {
      camera.direction.set(newDirection);
    }
  }

  private void moveForward(float distance) {
    fwdHorizontal.set(camera.direction).y = 0;
    fwdHorizontal.nor();
    fwdHorizontal.scl(distance);
    camera.position.add(fwdHorizontal);
  }

  private void strafe(float distance) {
    fwdHorizontal.set(camera.direction).y = 0;
    fwdHorizontal.nor();
    tmp.set(fwdHorizontal);
    camera.position.add(tmp);
  }

  private void rotateView(float deltaX) {
    camera.direction.rotate(camera.up, deltaX);
    camera.up.set(Vector3.Y);
  }

  public void update(float deltaTime) {
    float moveSpeed = Settings.walkSpeed;
    float bobSpeed = 0;

    if (keys.containsKey(runShiftKey)) {
      moveSpeed *= Settings.runFactor;
    }

    if (keys.containsKey(forwardKey)) {
      moveForward(deltaTime * moveSpeed);
      bobSpeed = moveSpeed;
    }

    if (keys.containsKey(backwardKey)) {
      moveForward(-deltaTime * moveSpeed);
      bobSpeed = moveSpeed;
    }

    if (keys.containsKey(strafeLeftKey)) {
      strafe(-deltaTime * Settings.walkSpeed);
      bobSpeed = Settings.walkSpeed;
    }

    if (keys.containsKey(strafeRightKey)) {
      strafe(-deltaTime * Settings.walkSpeed);
      bobSpeed = Settings.walkSpeed;
    }

    if (keys.containsKey(turnLeftKey)) {
      rotateView(deltaTime * Settings.turnSpeed);
    } else if (keys.containsKey(turnRightKey)) {
      rotateView(-deltaTime * Settings.turnSpeed);
    }

    if (keys.containsKey(jumpKey) && !isJumping) {
      isJumping = true;
      jumpV = 5;
    }

    if (isJumping) {
      bobSpeed = 0;
      jumpV += deltaTime * Settings.gravity;
      jumpH += jumpV * deltaTime;

      if (jumpH < 0) {
        isJumping = false;
        jumpH = 0;
      }
    }

    camera.position.y = Settings.eyeHeight + jumpH + bobHeight(bobSpeed, deltaTime);
    camera.update(true);
  }
}
