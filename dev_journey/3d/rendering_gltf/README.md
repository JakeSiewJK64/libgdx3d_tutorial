# GLTF rendering

### This project demonstrates rendering basic GLTF map and camera movement.

use `GLTFLoader().load()` method and Gdx.files.internal to load the gltf model file.

```java
// FirstScreen.java
sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/step4a.gltf"));
```

configure the scene environment.

```java
// FirstScreen.java
Scene scene = new Scene(sceneAsset.scene);
...
sceneManager.addScene(scene);
sceneManager.setCamera(cameraController.getCamera());
sceneManager.setSkyBox(skybox);
sceneManager.setAmbientLight(0f);
sceneManager.environment.add(light);
```

important thing to note is that it is necessary to set ambient light and add light to the scene, not doing so will not render the GLTF properly. Not adding skybox will result in the sky being a black texture instead of a normal sky with a sun.

another important thing to remember is `ScreenUtils.clear()` is necessary, not including it will result in a black screen with nothing rendered.