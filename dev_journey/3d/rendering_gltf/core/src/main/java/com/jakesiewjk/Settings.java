package com.jakesiewjk;

import com.badlogic.gdx.Input;

public class Settings {

  // CAMERA CONFIGURATION
  static public float eyeHeight = 5f; // meters
  static public boolean freeLook = true;
  static public float headBobDuration = 0.6f; // s
  static public float headBobHeight = 0.04f; // m
  static public final float degreesPerPixel = 0.1f;
  static public final int forwardKey = Input.Keys.W;
  static public final int backwardKey = Input.Keys.S;
  static public final int strafeLeftKey = Input.Keys.A;
  static public final int strafeRightKey = Input.Keys.D;

  // MOVEMENT CONFIGURATION
  static public float walkSpeed = 5f; // m/s
  static public float runFactor = 3f; // m/s
  static public float turnSpeed = 120f; // degrees/s
}
