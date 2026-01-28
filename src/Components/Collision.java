package Components;

import Utility.Vec2;

//Class that stores information
public class Collision {
    //references to the colliders involved
    public int[] colliders = new int[2];

    //Vectors that define the normal to the surface at the point
    public Vec2[] normals = new Vec2[2];

    //Where the collision actually happened
    public Vec2 point;

    //what happened with the colliders
    public static enum CollisionTypes{Enter,Stay,Leave}
    public CollisionTypes collisionType;

    //Checks whether the collider contains BOTH colliders
    public boolean ContainsColliders(int collider1,int collider2){
        return (colliders[0] == collider1 && colliders[1] == collider2) || (colliders[1] == collider1 && colliders[0] == collider2);
    }
    //Checks whether the collider contains the collider
    public boolean ContainsCollider(int collider){
        return colliders[0] == collider || colliders[1] == collider;
    }
}