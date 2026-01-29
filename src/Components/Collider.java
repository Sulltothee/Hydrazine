package Components;

import Components.Collision;
import Components.Component;
import Utility.Vec2;

import java.util.ArrayList;

public class Collider extends Component {
    //The vector from the attached entity to this collider's centre
    public Vec2 OffsetVector = Vec2.Zero();

    //Whether this collider will pass through other colliders or stop on them
    public colliderTypes colliderType;

    //what shape this collider is
    public static enum ColliderShapes{Box,Round,Vertex,Complex}
    ColliderShapes Shape = ColliderShapes.Round;

    //References to colliders currently contained within this one (if it's a soft collider)
    public ArrayList<Integer> containedColliders = new ArrayList<>();

    //The Height and width of the collider, Used for Box and Round
    public float Height = 1;
    public float Width = 1;

    //A list of vertexes, only used for vertex colliders
    public Vec2[] vertexes;

    //A list of collider components, only used for complex colliders
    public Collider[] colliders;

    //Constructor
    public Collider() {
        this.OffsetVector = Vec2.Zero();
        colliderType = colliderTypes.Hard;
        Shape = ColliderShapes.Box;
        TypeID = ComponentTypes.Collider;
    }

    //Hard colliders stop eachother while soft just log a collision
    public static enum colliderTypes {
        Hard,
        Soft;
    }

    public ColliderShapes getShape(){
        return Shape;
    }
}
