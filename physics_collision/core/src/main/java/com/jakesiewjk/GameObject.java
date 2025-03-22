package com.jakesiewjk;

import net.mgsx.gltf.scene3d.scene.Scene;

public class GameObject {
  public final Scene scene;
  public final PhysicsBody body;

  public GameObject(Scene scene, PhysicsBody body) {
    this.scene = scene;
    this.body = body;

    body.geom.setData(this);
  }
}
