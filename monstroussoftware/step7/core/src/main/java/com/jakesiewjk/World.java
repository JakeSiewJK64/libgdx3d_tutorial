package com.jakesiewjk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jakesiewjk.controllers.PlayerController;
import com.jakesiewjk.physics.PhysicsBody;
import com.jakesiewjk.physics.PhysicsBodyFactory;
import com.jakesiewjk.physics.PhysicsWorld;
import com.jakesiewjk.physics.enums.CollisionShapeType;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {
  private final Array<GameObject> gameObjects;
  private final SceneAsset sceneAsset;
  private final PhysicsBodyFactory factory;
  private final PhysicsWorld physicsWorld;
  private final Vector3 dir = new Vector3();
  private final Vector3 spawnPos = new Vector3();
  private final Vector3 shootDirection = new Vector3();
  private final PlayerController playerController;
  private boolean isDirty;
  public GameObject player;

  public World(String modelFileName) {
    playerController = new PlayerController();
    gameObjects = new Array<>();
    physicsWorld = new PhysicsWorld();
    factory = new PhysicsBodyFactory(physicsWorld);
    sceneAsset = new GLTFLoader().load(Gdx.files.internal(modelFileName));

    for (Node node : sceneAsset.scene.model.nodes) {
      Gdx.app.log("node: ", node.id);
    }

    isDirty = true;
  }

  public void setPlayer(GameObject player) {
    this.player = player;
    player.body.setPlayerCharacteristics();
  }

  public void clear() {
    physicsWorld.reset();
    playerController.reset();
    gameObjects.clear();
    player = null;
    isDirty = true;
  }

  public GameObject spawnObject(boolean isStatic, String name, String proxyName, CollisionShapeType shape,
      boolean resetPosition,
      Vector3 position, float mass) {
    Scene scene = loadNode(name, resetPosition, position);
    ModelInstance collisionInstance = scene.modelInstance;

    if (proxyName != null) {
      Scene proxyScene = loadNode(proxyName, resetPosition, position);
      collisionInstance = proxyScene.modelInstance;
    }

    PhysicsBody body = factory.createBody(collisionInstance, shape, mass, isStatic);
    GameObject go = new GameObject(scene, body);
    gameObjects.add(go);
    isDirty = true;

    return go;
  }

  private Scene loadNode(String nodeName, boolean resetPosition, Vector3 position) {
    Scene scene = new Scene(sceneAsset.scene, nodeName);

    if (scene.modelInstance.nodes.size == 0) {
      throw new RuntimeException("Cannot find node in GLTF file: " + nodeName);
    }

    applyNodeTransform(resetPosition, scene.modelInstance, scene.modelInstance.nodes.first());

    scene.modelInstance.transform.translate(position);

    return scene;
  }

  private void applyNodeTransform(boolean resetPosition, ModelInstance modelInstance, Node node) {
    if (!resetPosition) {
      modelInstance.transform.mul(node.globalTransform);
    }

    node.translation.set(0, 0, 0);
    node.scale.set(1, 1, 1);
    node.rotation.idt();
    modelInstance.calculateTransforms();
  }

  public void removeObject(GameObject gameObject) {
    gameObjects.removeValue(gameObject, true);
    isDirty = true;
  }

  public void update(float deltaTime) {
    physicsWorld.update();
    syncToPhysics();

    playerController.update(player, deltaTime);
  }

  private void syncToPhysics() {
    for (GameObject go : gameObjects) {
      if (go.body.geom.getBody() != null) {
        go.scene.modelInstance.transform.set(go.body.getPosition(), go.body.getOrientation());
      }
    }

    player.scene.modelInstance.transform.setToRotation(Vector3.Z, playerController.getForwardDirection());
    player.scene.modelInstance.transform.setTranslation(player.body.getPosition());
  }

  @Override
  public void dispose() {
    sceneAsset.dispose();
    physicsWorld.dispose();
  }

  public void shootBall() {
    dir.set(playerController.getViewingDirection());
    spawnPos.set(dir);

    Vector3 playerPosition = player.getPosition();
    spawnPos.add(playerPosition);

    GameObject ball = spawnObject(false, "ball", null, CollisionShapeType.SPHERE, true, spawnPos, Settings.ballMass);
    shootDirection.set(dir);
    shootDirection.y += .5f;
    shootDirection.scl(Settings.ballForce);
    ball.body.applyForce(shootDirection);
  }

  public GameObject getPlayer() {
    return player;
  }

  public PlayerController getPlayerController() {
    return playerController;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public int getNumGameObjects() {
    return gameObjects.size;
  }

  public GameObject getGameObject(int index) {
    return gameObjects.get(index);
  }
}
