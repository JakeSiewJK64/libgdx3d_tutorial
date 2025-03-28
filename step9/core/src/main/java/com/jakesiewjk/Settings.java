package com.jakesiewjk;

public class Settings {
  static public float eyeHeight = 8f; // meters
  static public float camDistanceFromFront = -1f;

  static public float walkSpeed = 5f; // m/s
  static public float runFactor = 3f; // m/s
  static public float turnSpeed = 120f; // degrees/s
  static public float jumpForce = 10f;

  // camera properties
  static public boolean invertLook = false;
  static public boolean freeLook = true;
  static public float headBobDuration = 0.6f;
  static public float cameraFOV = 50f;
  static public float headBobHeight = 0.04f;

  // higher the scale, the higher the head bob
  static public float headBobScale = 2f;
  static public float degreesPerPixel = 0.1f; // mouse sensitivity

  static public float gravity = -9.8f; // meters / s^2

  static public final int shadowMapSize = 4096;

  static public float ballMass = 0.2f;
  static public float ballForce = 100f;

  static public float playerMass = 1.0f;
  static public float playerLinearDamping = 0.05f;
  static public float playerAngularDamping = 0.5f;

  static public final String GLTF_FILE = "models/step12.gltf";

  static public float groundRayLength = 1.2f;
}
