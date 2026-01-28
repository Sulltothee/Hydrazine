package Components;

import Utility.Vec2;

public class Transform extends Component {

    //The position of the attached object
    public Vec2 Position;

    //Whether the position is locked
    public boolean positionLocked = false;

    //The rotation of the attached object (currently unused)
    public float EulerRotation;

    //Constructor
    public Transform() {
        this.Position = Vec2.Zero();
        this.EulerRotation = 0.0F;
        TypeID = Component.ComponentTypes.Transform;
    }
}

