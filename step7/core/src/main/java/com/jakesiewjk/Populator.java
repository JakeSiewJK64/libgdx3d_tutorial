package com.jakesiewjk;

import com.badlogic.gdx.math.Vector3;
import com.jakesiewjk.physics.enums.CollisionShapeType;

public class Populator {
  public static void populate(World world) {
    world.clear();

    world.spawnObject(true, "groundbox", null, CollisionShapeType.BOX, true, Vector3.Zero, 1f);
    // world.spawnObject(true, "brickcube", CollisionShapeType.BOX, true,
    // Vector3.Zero, 1);
    // world.spawnObject(true, "brickcube.001", CollisionShapeType.BOX, true,
    // Vector3.Zero, 1f);
    // world.spawnObject(true, "brickcube.002", CollisionShapeType.BOX, true,
    // Vector3.Zero, 1f);
    // world.spawnObject(true, "brickcube.003", CollisionShapeType.BOX, true,
    // Vector3.Zero, 1f);
    // world.spawnObject(true, "wall", CollisionShapeType.BOX, true, Vector3.Zero,
    // 1f);
    // world.spawnObject(false, "ball", CollisionShapeType.SPHERE, false, new
    // Vector3(0, 4, 0), 1f);
    // world.spawnObject(false, "ball", CollisionShapeType.SPHERE, false, new
    // Vector3(-1, 5, 0), 1f);
    // world.spawnObject(false, "ball", CollisionShapeType.SPHERE, false, new
    // Vector3(-2, 6, 0), 1f);
    world.player = world.spawnObject(false, "ducky", null, CollisionShapeType.SPHERE, false, new Vector3(5, 5, 5), 1f);
    world.spawnObject(true, "arch", null, CollisionShapeType.MESH, false, Vector3.Zero, 1f);
    world.spawnObject(true, "stairs", "stairsProxy", CollisionShapeType.MESH, false, Vector3.Zero, 1f);
  }
}
