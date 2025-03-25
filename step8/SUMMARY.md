### Description

One problem is faced going through the tutorial is that when the player goes up the ramp, the player slides down rather than having the gravity disabled. because the ground ray z axis was not negative hence it couldnt detect the gound.

```java
public boolean isGrounded(GameObject player, Vector3 playerPos, float rayLength, Vector3 groundNormal) {
  ...
  // has to be negative z
  groundRay.set(playerPos.x, -playerPos.z, playerPos.y, 0, 0, -1);
  ...
}
```

another confusion was why the player was clipping through the floor and bouncing back, turns out i needed a `sign` variable to invert the collision normal when o2 is an instance of DRay. without this, the normal might point in the wrong direction. Since jumping relies on the upward force (usually the normal vector), a wrongly oriented normal results in weak or incorrect jumps.

```java
@Override
public void call(Object data, DGeom o1, DGeom o2) {
  GameObject go;

  final int N = 1;
  DContactBuffer contacts = new DContactBuffer(N);
  int n = OdeHelper.collide(o1, o2, N, contacts.getGeomBuffer());

  if (n > 0) {
    float sign = 1;

    if (o2 instanceof DRay) {
      go = (GameObject) o1.getData();
      sign = -1f;
    } else {
      go = (GameObject) o2.getData();
    }

    // ignore collision with player itself
    if (go == player) {
      return;
    }

    DVector3 normal = contacts.get(0).getContactGeom().normal;

    // swap Y&Z
    ((Vector3) data).set((float) (sign * normal.get(0)), (float) (sign * normal.get(2)),
        -(float) (sign * normal.get(1)));
  }
}
```

### Summary
