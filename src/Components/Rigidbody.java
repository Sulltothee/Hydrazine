package Components;

import Utility.Vec2;

public class Rigidbody extends Component {
    //Velocity of the object in game units per frame (u/f)
    public Vec2 Velocity = Vec2.Zero();

    //Acceleration of the object in (u/f^2)
    public Vec2 Acceleration = Vec2.Zero();

    //The mass of the object
    public float Mass = 1;

    //the strength of gravity in newtons, Earth's is (approximately) 9.8 hence the default
    public float GravityScale = 9.8f;

    //Whether this object should also simulate gravity
    public boolean SimulateGravity = false;

    //The direction of gravity
    public Vec2 GravityDirection = Vec2.Down();

    //Stores information about the force this rigidbody is on
    public SurfaceInfo surfaceInfo = new SurfaceInfo();

    //Whether to slow down this rigidbody manually using frictional force
    public boolean SimulateKineticFriction = true;

    //if the speed/ magnitude of velocity is below this number it is set to 0
    public float SpeedCutoff = 1e-3f;

    public Rigidbody(){
        TypeID = ComponentTypes.Rigidbody;
    }

    public class SurfaceInfo{
        public float CoefficientofFriction = 0.25f;
        //public float CoefficientofKineticFriction = 0.5f;
    }

    public float GetMaxFriction(){
        return Mass * GravityScale * surfaceInfo.CoefficientofFriction;
    }

    public static void AddForce(Rigidbody rb, Vec2 Force){
        //Calculating the friction force
        Vec2 FrictionForce = Vec2.Scale(Force, rb.surfaceInfo.CoefficientofFriction);
        if(FrictionForce.getMagnitude() > rb.GetMaxFriction())
        {FrictionForce = Vec2.SetMagnitude(FrictionForce,rb.GetMaxFriction());}

        //Adding to the acceleration (F = ma)
        Rigidbody.AddAcceleration(rb ,Vec2.Scale(Vec2.Subtract(Force, FrictionForce), 1/rb.Mass));
    }

    //Directly modifies the acceleration, only used internally
    public static void AddAcceleration(Rigidbody rb, Vec2 acceleration){
        rb.Acceleration = Vec2.Add(rb.Acceleration, acceleration);
    }
}
