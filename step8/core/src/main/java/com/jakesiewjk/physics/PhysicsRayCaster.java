package com.jakesiewjk.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.github.antzGames.gdx.ode4j.math.DVector3;
import com.github.antzGames.gdx.ode4j.ode.DContactBuffer;
import com.github.antzGames.gdx.ode4j.ode.DGeom;
import com.github.antzGames.gdx.ode4j.ode.DRay;
import com.github.antzGames.gdx.ode4j.ode.OdeHelper;
import com.jakesiewjk.GameObject;

public class PhysicsRayCaster implements Disposable {
  private final PhysicsWorld world;
  private final DRay groundRay;
  private GameObject player;

  public PhysicsRayCaster(PhysicsWorld world) {
    this.world = world;
    groundRay = OdeHelper.createRay(1);
  }

  public boolean isGrounded(GameObject player, Vector3 playerPos, float rayLength, Vector3 groundNormal) {
    this.player = player;
    groundRay.setLength(rayLength);
    groundRay.set(playerPos.x, -playerPos.z, playerPos.y, 0, 0, -1);
    groundRay.setFirstContact(true);
    groundRay.setBackfaceCull(true);

    groundNormal.set(0, 0, 0);
    OdeHelper.spaceCollide2(world.space, groundRay, groundNormal, callback);
    return !groundNormal.isZero();
  }

  private final DGeom.DNearCallback callback = new DGeom.DNearCallback() {

    @Override
    public void call(Object data, DGeom o1, DGeom o2) {
      GameObject go;

      final int N = 1;
      DContactBuffer contacts = new DContactBuffer(N);
      int n = OdeHelper.collide(o1, o2, N, contacts.getGeomBuffer());

      if (n > 0) {
        float sign = 1;

        if (o2 instanceof DRay) {
          go = (GameObject) o1.getData();
          sign = -1f;
        } else {
          go = (GameObject) o2.getData();
        }

        // ignore collision with player itself
        if (go == player) {
          return;
        }

        DVector3 normal = contacts.get(0).getContactGeom().normal;

        // swap Y&Z
        ((Vector3) data).set((float) (sign * normal.get(0)), (float) (sign * normal.get(2)),
            -(float) (sign * normal.get(1)));
      }
    }
  };

  @Override
  public void dispose() {
    groundRay.destroy();
  }
}
