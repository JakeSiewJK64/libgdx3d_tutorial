package com.jakesiewjk.physics;

import static com.github.antzGames.gdx.ode4j.ode.OdeConstants.dContactApprox1;
import static com.github.antzGames.gdx.ode4j.ode.OdeConstants.dContactSlip1;
import static com.github.antzGames.gdx.ode4j.ode.OdeConstants.dContactSlip2;
import static com.github.antzGames.gdx.ode4j.ode.OdeConstants.dContactSoftCFM;
import static com.github.antzGames.gdx.ode4j.ode.OdeConstants.dContactSoftERP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.github.antzGames.gdx.ode4j.ode.DBody;
import com.github.antzGames.gdx.ode4j.ode.DCapsule;
import com.github.antzGames.gdx.ode4j.ode.DContact;
import com.github.antzGames.gdx.ode4j.ode.DContactBuffer;
import com.github.antzGames.gdx.ode4j.ode.DGeom;
import com.github.antzGames.gdx.ode4j.ode.DJoint;
import com.github.antzGames.gdx.ode4j.ode.DJointGroup;
import com.github.antzGames.gdx.ode4j.ode.DSapSpace;
import com.github.antzGames.gdx.ode4j.ode.DSpace;
import com.github.antzGames.gdx.ode4j.ode.DSphere;
import com.github.antzGames.gdx.ode4j.ode.DWorld;
import com.github.antzGames.gdx.ode4j.ode.OdeHelper;
import com.jakesiewjk.GameObject;
import com.jakesiewjk.Settings;
import com.jakesiewjk.World;

public class PhysicsWorld implements Disposable {

  public DWorld world;
  public DSpace space;
  private final World gameWorld;
  private final DJointGroup contactGroup;

  public PhysicsWorld(World gameWorld) {
    this.gameWorld = gameWorld;
    OdeHelper.initODE2(0);
    Gdx.app.log("ODE version", OdeHelper.getVersion());
    Gdx.app.log("ODE config", OdeHelper.getVersion());
    contactGroup = OdeHelper.createJointGroup();
    reset();
  }

  public void reset() {
    if (world != null) {
      world.destroy();
    }

    if (space != null) {
      space.destroy();
    }

    world = OdeHelper.createWorld();
    space = OdeHelper.createSapSpace(null, DSapSpace.AXES.XYZ);

    world.setGravity(0, 0, Settings.gravity);
    world.setCFM(1e-5);
    world.setERP(.4);
    world.setQuickStepNumIterations(40);
    world.setAngularDamping(.5f);

    // set auto disable parameters to make inactive objects
    world.setAutoDisableFlag(true);
    world.setAutoDisableLinearThreshold(.1);
    world.setAutoDisableAngularThreshold(.1);
    world.setAutoDisableTime(2);
  }

  public void update() {
    space.collide(null, nearCallback);
    world.quickStep(.025f);
    contactGroup.empty();
  }

  private DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {
    @Override
    public void call(Object data, DGeom o1, DGeom o2) {
      DBody b1 = o1.getBody();
      DBody b2 = o2.getBody();

      if (b1 != null && b2 != null && OdeHelper.areConnected(b1, b2)) {
        return;
      }

      final int N = 8;
      DContactBuffer contacts = new DContactBuffer(N);

      int n = OdeHelper.collide(o1, o2, N, contacts.getGeomBuffer());

      if (n > 0) {
        gameWorld.onCollision((GameObject) o1.getData(), (GameObject) o2.getData());

        for (int i = 0; i < n; i++) {
          DContact contact = contacts.get(i);
          contact.surface.mode = dContactSlip1 | dContactSlip2 | dContactSoftERP | dContactSoftCFM | dContactApprox1;

          if (o1 instanceof DSphere || o2 instanceof DSphere || o1 instanceof DCapsule || o2 instanceof DCapsule) {
            contact.surface.mu = 0.01; // low friction for balls & capsules
          } else {
            contact.surface.mu = 0.5;
          }

          contact.surface.slip1 = 0.0;
          contact.surface.slip2 = 0.0;
          contact.surface.soft_erp = 0.8;
          contact.surface.soft_cfm = 0.01;

          DJoint c = OdeHelper.createContactJoint(world, contactGroup, contact);
          c.attach(o1.getBody(), o2.getBody());
        }
      }
    }
  };

  @Override
  public void dispose() {
    contactGroup.destroy();
    space.destroy();
    world.destroy();
    OdeHelper.closeODE();
  }
}
