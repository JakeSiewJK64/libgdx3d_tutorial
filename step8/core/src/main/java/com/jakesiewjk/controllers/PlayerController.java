package com.jakesiewjk.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.jakesiewjk.GameObject;
import com.jakesiewjk.Settings;
import com.jakesiewjk.physics.PhysicsRayCaster;

public class PlayerController extends InputAdapter {
  public int forwardKey = Input.Keys.W;
  public int backwardKey = Input.Keys.S;
  public int strafeLeftKey = Input.Keys.A;
  public int strafeRightKey = Input.Keys.D;
  public int turnLeftKey = Input.Keys.Q;
  public int turnRightKey = Input.Keys.E;
  public int jumpKey = Input.Keys.SPACE;
  public int runShiftKey = Input.Keys.SHIFT_LEFT;

  private boolean isOnGround = false;
  private final IntIntMap keys = new IntIntMap();
  private final Vector3 linearForce;
  private final Vector3 forwardDirection;
  private final Vector3 viewingDirection;
  private float mouseDeltaX;
  private float mouseDeltaY;
  private final Vector3 tmp = new Vector3();
  private final Vector3 tmp2 = new Vector3();
  private final Vector3 tmp3 = new Vector3();
  private final Vector3 groundNormal = new Vector3();
  private final PhysicsRayCaster physicsRayCaster;

  public PlayerController(PhysicsRayCaster physicsRayCaster) {
    this.physicsRayCaster = physicsRayCaster;

    linearForce = new Vector3();
    forwardDirection = new Vector3();
    viewingDirection = new Vector3();
    reset();
  }

  public void reset() {
    forwardDirection.set(0, 0, 1);
    viewingDirection.set(forwardDirection);
  }

  public Vector3 getViewingDirection() {
    return viewingDirection;
  }

  public Vector3 getForwardDirection() {
    return forwardDirection;
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
    if (Math.abs(Gdx.input.getDeltaX()) >= 100 && Math.abs(Gdx.input.getDeltaY()) >= 100) {
      return true;
    }

    mouseDeltaX = -Gdx.input.getDeltaX() * Settings.degreesPerPixel;
    mouseDeltaY = -Gdx.input.getDeltaY() * Settings.degreesPerPixel;

    return true;
  }

  private void rotateView(float deltaX, float deltaY) {
    viewingDirection.rotate(Vector3.Y, deltaX);

    if (!Settings.freeLook) {
      viewingDirection.y = 0;
      return;
    }

    if (Settings.invertLook) {
      deltaY = -deltaY;
    }

    // avoid gimbal lock when looking straight up/down
    Vector3 oldPitchAxis = tmp.set(viewingDirection).crs(Vector3.Y).nor();
    Vector3 newDirection = tmp2.set(viewingDirection).rotate(tmp, deltaY);
    Vector3 newPitchAxis = tmp3.set(tmp2).crs(Vector3.Y);

    if (!newPitchAxis.hasOppositeDirection(oldPitchAxis)) {
      viewingDirection.set(newDirection);
    }
  }

  private void moveForward(float distance) {
    linearForce.set(forwardDirection).scl(distance);
  }

  private void strafe(float distance) {
    tmp.set(forwardDirection).crs(Vector3.Y);
    tmp.scl(distance);
    linearForce.add(tmp);
  }

  public boolean getIsOnGround() {
    return isOnGround;
  }

  public void update(GameObject player, float deltaTime) {
    // derive forward direction vector from viewing direction
    forwardDirection.set(viewingDirection);
    forwardDirection.y = 0;
    forwardDirection.nor();

    linearForce.set(0, 0, 0);

    isOnGround = physicsRayCaster.isGrounded(player, player.getPosition(), Settings.groundRayLength,
        groundNormal);

    if (isOnGround) {
      float dot = groundNormal.dot(Vector3.Y);
      player.body.geom.getBody().setGravityMode(dot >= .99f);
    } else {
      player.body.geom.getBody().setGravityMode(true);
    }

    float moveSpeed = Settings.walkSpeed;

    if (keys.containsKey(runShiftKey)) {
      moveSpeed *= Settings.runFactor;
    }

    // mouse to move view direction
    rotateView(mouseDeltaX * deltaTime * Settings.turnSpeed, mouseDeltaY * deltaTime * Settings.turnSpeed);
    mouseDeltaX = 0;
    mouseDeltaY = 0;

    if (keys.containsKey(forwardKey)) {
      moveForward(deltaTime * moveSpeed);
    }

    if (keys.containsKey(backwardKey)) {
      moveForward(-deltaTime * moveSpeed);
    }

    if (keys.containsKey(strafeLeftKey)) {
      strafe(-deltaTime * Settings.walkSpeed);
    }

    if (keys.containsKey(strafeRightKey)) {
      strafe(deltaTime * Settings.walkSpeed);
    }

    if (keys.containsKey(turnLeftKey)) {
      rotateView(deltaTime * Settings.turnSpeed, 0);
    }

    if (keys.containsKey(turnRightKey)) {
      rotateView(-deltaTime * Settings.turnSpeed, 0);
    }

    if (isOnGround && keys.containsKey(jumpKey)) {
      linearForce.y = Settings.jumpForce;
    }

    linearForce.scl(80);
    player.body.applyForce(linearForce);
  }
}
