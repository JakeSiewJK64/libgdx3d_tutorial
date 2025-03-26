package com.jakesiewjk;

import com.badlogic.gdx.math.Vector3;
import com.jakesiewjk.physics.PhysicsBody;

import net.mgsx.gltf.scene3d.scene.Scene;

public class GameObject {
  public final GameObjectType type;
  public final Scene scene;
  public final Vector3 direction;
  public final PhysicsBody body;
  public boolean visible;

  public GameObject(GameObjectType type, Scene scene, PhysicsBody body) {
    this.type = type;
    this.scene = scene;
    this.body = body;

    if (body != null) {
      body.geom.setData(this);
    }

    visible = true;
    direction = new Vector3();
  }

  public Vector3 getPosition() {
    if (body == null) {
      return Vector3.Zero;
    }

    return body.getPosition();
  }

  public Vector3 getDirection() {
    direction.set(0, 0, 1);
    direction.mul(body.getOrientation());
    return direction;
  }
}
