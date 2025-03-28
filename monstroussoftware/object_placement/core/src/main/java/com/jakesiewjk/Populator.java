package com.jakesiewjk;

import com.badlogic.gdx.math.Vector3;

public class Populator {
  public static void populate(World world) {
    world.clear();

    world.spawnObject(true, "brickcube", CollisionShapeType.BOX, true, Vector3.Zero, 1);
    world.spawnObject(true, "groundbox", CollisionShapeType.BOX, true, Vector3.Zero, 1f);
    world.spawnObject(true, "brickcube.001", CollisionShapeType.BOX, true, Vector3.Zero, 1f);
    world.spawnObject(true, "brickcube.002", CollisionShapeType.BOX, true, Vector3.Zero, 1f);
    world.spawnObject(true, "brickcube.003", CollisionShapeType.BOX, true, Vector3.Zero, 1f);
    world.spawnObject(true, "wall", CollisionShapeType.BOX, true, Vector3.Zero, 1f);
    world.spawnObject(false, "ball", CollisionShapeType.SPHERE, false, new Vector3(0, 4, 0), 1f);
    world.spawnObject(false, "ball", CollisionShapeType.SPHERE, false, new Vector3(-1, 5, 0), 1f);
    world.spawnObject(false, "ball", CollisionShapeType.SPHERE, false, new Vector3(-2, 6, 0), 1f);
    world.player = world.spawnObject(false, "ducky", CollisionShapeType.SPHERE, false, new Vector3(5, 5, 5), 1f);
  }
}
