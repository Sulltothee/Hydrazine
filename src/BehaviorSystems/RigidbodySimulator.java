package BehaviorSystems;

import Components.Component;
import Components.Rigidbody;
import Components.Transform;
import SystemInterfaces.IPhysicsUpdate;
import Utility.Vec2;

import java.util.ArrayList;

public class RigidbodySimulator extends BehaviorSystem implements IPhysicsUpdate {

    //Takes Transform THEN Rigidbody
    @Override
    public void PhysicsUpdate(float deltaTime, ArrayList<Component> components) {
        //For every set of transform and rigidbody
        for (int i = 0; i < components.toArray().length/2; i++) {

            //Sets the current components
            Transform currentTransform = (Transform)components.get(2 * i);

            //if the position isn't locked
            if(!currentTransform.positionLocked) {

                Rigidbody currentRigidbody = (Rigidbody) components.get((2 * i) + 1);

                //Adding a gravitational force if it's enabled
                if (currentRigidbody.GravityScale > 0 && currentRigidbody.SimulateGravity) {
                    Rigidbody.AddAcceleration(currentRigidbody, Vec2.Scale(currentRigidbody.GravityDirection, (currentRigidbody.Mass * currentRigidbody.GravityScale)));
                }

                //Adding frictional force if the object is moving if that setting is enabled
                if (currentRigidbody.SimulateKineticFriction) {
                    Vec2 frictionForce = Vec2.Scale(currentRigidbody.Velocity.invert(), currentRigidbody.surfaceInfo.CoefficientofFriction);

                    if (frictionForce.getMagnitude() > currentRigidbody.GetMaxFriction() * deltaTime) {
                        Vec2.SetMagnitude(frictionForce, currentRigidbody.GetMaxFriction() * deltaTime);
                    }

                    Rigidbody.AddAcceleration(currentRigidbody, frictionForce);
                }

                //Updating the entity's position
                currentTransform.Position = Vec2.Add(new Vec2[]{currentTransform.Position, currentRigidbody.Velocity, Vec2.Scale(currentRigidbody.Acceleration, (float) 1 / 2)});

                //Updating the entity's velocity
                currentRigidbody.Velocity = Vec2.Add(currentRigidbody.Velocity, currentRigidbody.Acceleration);

                //if the velocity is too small it becomes 0 for computational sanctity
                if (currentRigidbody.Velocity.getMagnitude() < currentRigidbody.SpeedCutoff) {
                    currentRigidbody.Velocity = Vec2.Zero();
                }

                //Resets the acceleration
                currentRigidbody.Acceleration = Vec2.Zero();

                //TEST
                currentTransform.Position.output("Rigidbody position " + i);
            }
        }
    }

    //Constructor
    public RigidbodySimulator(){
        //Setting the type
        Type = SystemTypes.RigidbodySimulator;

        //Setting the requirements
        ReliantComponents.set(Component.ComponentTypes.Rigidbody.ordinal());
        ReliantComponents.set(Component.ComponentTypes.Transform.ordinal());

        //Setting when and how this should be called
        Calls.add(CallTypes.PhysicsUpdate);
    }
}
