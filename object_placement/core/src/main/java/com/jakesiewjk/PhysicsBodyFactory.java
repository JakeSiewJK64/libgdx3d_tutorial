package com.jakesiewjk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.github.antzGames.gdx.ode4j.ode.DBody;
import com.github.antzGames.gdx.ode4j.ode.DGeom;
import com.github.antzGames.gdx.ode4j.ode.DMass;
import com.github.antzGames.gdx.ode4j.ode.OdeHelper;

public class PhysicsBodyFactory implements Disposable {

  public static final long CATEGORY_STATIC = 1;
  public static final long CATEGORY_DYNAMIC = 2;

  private PhysicsWorld physicsWorld;
  private DMass massInfo;
  private final Vector3 position;
  private Quaternion q;
  private ModelBuilder modelBuilder;
  private Material material;
  private Array<Disposable> disposables;

  public PhysicsBodyFactory(PhysicsWorld physicsWorld) {
    this.physicsWorld = physicsWorld;
    massInfo = OdeHelper.createMass();
    q = new Quaternion();
    modelBuilder = new ModelBuilder();
    material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
    disposables = new Array<>();

    position = new Vector3();
  }

  public PhysicsBody createBody(ModelInstance collisionInstance, CollisionShapeType shapeType, float mass,
      boolean isStatic) {
    BoundingBox bbox = new BoundingBox();
    Node node = collisionInstance.nodes.first();
    node.calculateBoundingBox(bbox, false);
    float w = bbox.getWidth();
    float h = bbox.getHeight();
    float d = bbox.getDepth();
    DGeom geom;
    ModelInstance instance;
    float diameter = 0;
    float radius = 0;
    float len;

    switch (shapeType) {
      case BOX:
        geom = OdeHelper.createBox(physicsWorld.space, w, d, h);
        break;
      case SPHERE:
        diameter = Math.max(Math.max(w, d), h);
        radius = diameter / 2f;
        geom = OdeHelper.createSphere(physicsWorld.space, radius);
        break;
      case CAPSULE:
        diameter = Math.max(w, d);
        radius = diameter / 2;
        len = h - 2 * radius;
        geom = OdeHelper.createCapsule(physicsWorld.space, radius, len);
        break;
      case CYLINDER:
        diameter = Math.max(w, d);
        radius = diameter / 2f;
        len = h;
        geom = OdeHelper.createCylinder(physicsWorld.space, radius, len);
        break;
      default:
        throw new RuntimeException("Unknown shape type");
    }

    // if static, no rigidbody simulation
    if (isStatic) {
      geom.setCategoryBits(CATEGORY_STATIC);
      geom.setCollideBits(0);
    } else {
      DBody rigidBody = OdeHelper.createBody(physicsWorld.world);
      massInfo.setBox(1, w, d, h);
      massInfo.setMass(mass);
      rigidBody.setMass(massInfo);
      rigidBody.enable();
      rigidBody.setAutoDisableDefaults();
      rigidBody.setGravityMode(true);
      rigidBody.setDamping(.01f, .1f);

      geom.setBody(rigidBody);
      geom.setCategoryBits(CATEGORY_DYNAMIC);
      geom.setCollideBits(CATEGORY_DYNAMIC | CATEGORY_STATIC);
    }

    modelBuilder.begin();
    MeshPartBuilder meshBuilder;

    meshBuilder = modelBuilder.part("part", GL20.GL_LINES, VertexAttributes.Usage.Position, material);

    switch (shapeType) {
      case BOX:
        BoxShapeBuilder.build(meshBuilder, w, h, d);
        break;
      case SPHERE:
        SphereShapeBuilder.build(meshBuilder, diameter, diameter, diameter, 8, 8);
        break;
      case CAPSULE:
        CapsuleShapeBuilder.build(meshBuilder, radius, h, 12);
        break;
      case CYLINDER:
        CylinderShapeBuilder.build(meshBuilder, diameter, h, diameter, 12);
        break;
      default:
        throw new RuntimeException("Unknown shape type");
    }

    Model modelShape = modelBuilder.end();
    disposables.add(modelShape);
    instance = new ModelInstance(modelShape, Vector3.Zero);

    PhysicsBody body = new PhysicsBody(geom, instance);

    // copy position and orientation from modelinstance to body
    collisionInstance.transform.getTranslation(position);
    collisionInstance.transform.getRotation(q);
    body.setPosition(position);
    body.setOrientation(q);
    return body;
  }

  @Override
  public void dispose() {
    for (Disposable d : disposables) {
      d.dispose();
    }
  }
}
