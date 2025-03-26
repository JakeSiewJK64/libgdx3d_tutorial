package com.jakesiewjk.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.github.antzGames.gdx.ode4j.math.DQuaternion;
import com.github.antzGames.gdx.ode4j.math.DQuaternionC;
import com.github.antzGames.gdx.ode4j.math.DVector3C;
import com.github.antzGames.gdx.ode4j.ode.DBody;
import com.github.antzGames.gdx.ode4j.ode.DGeom;
import com.jakesiewjk.Settings;

public class PhysicsBody {
  public DGeom geom;
  private final Vector3 linearVelocity;
  private Vector3 position;
  private Quaternion quaternion;
  private ModelInstance debugInstance;

  static private final Color COLOR_ACTIVE = Color.GREEN;
  static private final Color COLOR_STATIC = Color.GRAY;
  static private final Color COLOR_SLEEPING = Color.TEAL;

  public PhysicsBody(DGeom geom, ModelInstance debugInstance) {
    this.geom = geom;
    this.debugInstance = debugInstance;
    position = new Vector3();
    linearVelocity = new Vector3();
    quaternion = new Quaternion();
  }

  public Vector3 getPosition() {
    DVector3C pos = geom.getPosition();
    position.x = (float) pos.get0();
    position.y = (float) pos.get2();
    position.z = -(float) pos.get1();
    return position;
  }

  public void setPosition(Vector3 pos) {
    geom.setPosition(pos.x, -pos.z, pos.y);
    DBody rigidBody = geom.getBody();

    if (rigidBody != null) {
      rigidBody.setPosition(pos.x, -pos.z, pos.y);
    }
  }

  public Quaternion getOrientation() {
    DQuaternionC odeQ = geom.getQuaternion();
    float ow = (float) odeQ.get0();
    float ox = (float) odeQ.get1();
    float oy = (float) odeQ.get2();
    float oz = (float) odeQ.get3();

    quaternion.set(ox, oz, -oy, ow);
    return quaternion;
  }

  public void setOrientation(Quaternion q) {
    DQuaternion odeQ = new DQuaternion(q.w, q.x, -q.z, q.y);
    geom.setQuaternion(odeQ);

    DBody rigidBody = geom.getBody();

    if (rigidBody != null) {
      rigidBody.setQuaternion(odeQ);
    }
  }

  public void render(ModelBatch batch) {
    debugInstance.transform.set(getPosition(), getOrientation());

    // render physics debug
    Color color = COLOR_STATIC;

    if (geom.getBody() != null) {
      if (geom.getBody().isEnabled()) {
        color = COLOR_ACTIVE;
      } else {
        color = COLOR_SLEEPING;
      }
    }

    debugInstance.materials.first().set(ColorAttribute.createDiffuse(color));
    batch.render(debugInstance);
  }

  public void applyForce(Vector3 force) {
    DBody rigidBody = geom.getBody();
    rigidBody.addForce(force.x, -force.z, force.y); // swap -z and y
  }

  public Vector3 getVelocity() {
    if (geom.getBody() == null) {
      linearVelocity.set(Vector3.Zero);
    } else {
      DVector3C v = geom.getBody().getLinearVel();
      linearVelocity.set((float) v.get0(), (float) v.get1(), (float) v.get2());
    }

    return linearVelocity;
  }

  // player physics does not go to sleep if inactivity
  public void setPlayerCharacteristics() {
    DBody rigidBody = geom.getBody();
    rigidBody.setDamping(Settings.playerLinearDamping, Settings.playerAngularDamping);
    rigidBody.setAutoDisableFlag(false);
    rigidBody.setMaxAngularSpeed(0);
  }
}
