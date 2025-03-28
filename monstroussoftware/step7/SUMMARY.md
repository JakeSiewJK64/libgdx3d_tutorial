### Description

World Class:
I was missing a `setPlayer` method to set the local `player` field and call `setPlayerCharacteristics` function, hence when player spawns and collides with the floor, after a certain time the capsule mesh will turn teal, and player can no longer move.

```java
public void setPlayer(GameObject player) {
  this.player = player;
  player.body.setPlayerCharacteristics();
  ...
}
```

I was also missing some function calls under `clear`

```java
public void clear() {
  physicsWorld.reset();
  playerController.reset();
  ...
}
```

the `shootBall` function was using the wrong dirction

```java
public void shootBall() {
  dir.set(playerController.getViewingDirection());
  spawnPos.set(dir);
  ...
}
```

### Summary
Up to this point, the major problems reside in `World` class. Looking at the Step7 example code from MonstrousSoftware, it seems not being able to climb the ramp at lower speed is expected.