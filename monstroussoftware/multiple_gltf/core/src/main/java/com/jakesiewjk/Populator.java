package com.jakesiewjk;

import com.badlogic.gdx.math.Vector3;

public class Populator {
  public static void populate(World world) {
    world.clear();
    world.spawnObject("Node3D", Vector3.Zero);
  }
}
