package com.jakesiewjk;

import com.badlogic.gdx.math.Vector3;
import com.jakesiewjk.physics.enums.CollisionShapeType;

public class Populator {
  public static void populate(World world) {
    world.clear();
    world.spawnObject(GameObjectType.TYPE_STATIC, "groundbox", null, CollisionShapeType.BOX, true, Vector3.Zero, 1f);
    world.spawnObject(GameObjectType.TYPE_DYNAMIC, "ball", null, CollisionShapeType.SPHERE, true, new Vector3(0, 4, -2),
        Settings.ballMass);
    world.spawnObject(GameObjectType.TYPE_DYNAMIC, "ball", null, CollisionShapeType.SPHERE, true,
        new Vector3(-1, 5, -2), Settings.ballMass);
    world.spawnObject(GameObjectType.TYPE_DYNAMIC, "ball", null, CollisionShapeType.SPHERE, true,
        new Vector3(-2, 6, -2), Settings.ballMass);

    world.spawnObject(GameObjectType.TYPE_PICKUP_COIN, "coin", null, CollisionShapeType.BOX, true,
        new Vector3(-5, 1, 0), 1);
    world.spawnObject(GameObjectType.TYPE_PICKUP_COIN, "coin", null, CollisionShapeType.BOX, true,
        new Vector3(5, 1, 15), 1);
    world.spawnObject(GameObjectType.TYPE_PICKUP_COIN, "coin", null, CollisionShapeType.BOX, true,
        new Vector3(-12, 1, 13), 1);
    world.spawnObject(GameObjectType.TYPE_PICKUP_HEALTH, "healthpack", null, CollisionShapeType.BOX, true,
        new Vector3(26, 0.1f, -26), 1);
    world.spawnObject(GameObjectType.TYPE_PICKUP_HEALTH, "healthpack", null, CollisionShapeType.BOX, true,
        new Vector3(-26, 0.1f, 26), 1);
    world.spawnObject(GameObjectType.TYPE_STATIC, "arch", null, CollisionShapeType.MESH, false, Vector3.Zero, 1f);
    world.spawnObject(GameObjectType.TYPE_STATIC, "stairs", "stairsProxy", CollisionShapeType.MESH, false, Vector3.Zero,
        1f);
    GameObject go = world.spawnObject(GameObjectType.TYPE_PLAYER, "ducky", null, CollisionShapeType.CAPSULE, true,
        new Vector3(0, 1, 0),
        Settings.playerMass);
    world.setPlayer(go);
  }
}
