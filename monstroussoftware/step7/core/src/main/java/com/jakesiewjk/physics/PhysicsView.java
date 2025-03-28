package com.jakesiewjk.physics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import com.jakesiewjk.World;

public class PhysicsView implements Disposable {

  private final ModelBatch modelBatch;
  private final World world;

  public PhysicsView(World world) {
    this.world = world;
    modelBatch = new ModelBatch();
  }

  public void render(Camera camera) {
    modelBatch.begin(camera);
    int num = world.getNumGameObjects();

    for (int i = 0; i < num; i++) {
      world.getGameObject(i).body.render(modelBatch);
    }

    modelBatch.end();
  }

  @Override
  public void dispose() {
    modelBatch.dispose();
  }
}
