package BehaviorSystems;

import Components.*;
import Main.Game;
import SystemInterfaces.ICollide;
import SystemInterfaces.IPhysicsUpdate;
import Utility.Vec2;

import java.text.CollationElementIterator;
import java.util.ArrayList;

public class RigidbodySimulator extends BehaviorSystem implements IPhysicsUpdate, ICollide {

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
        Calls.add(CallTypes.Collide);
    }

    //Takes Transform THEN Rigidbody, FIxing hard collisions. THis is SOOOOOO Jank
    @Override
    public void OnCollision(ArrayList<Collision> collisions, ArrayList<Integer> EntityIDs, ArrayList<Component> components) {
        //For every collision
        for(Collision collision : collisions){
            //Aquiring references to the entities the colliders are associated with
            int local0ID = EntityIDs.indexOf(collision.colliders[0]);
            int local1ID = EntityIDs.indexOf(collision.colliders[1]);

            //Aquiring references to the Transforms and rigidbodies that the colliders are associated with
            Transform ZeroTransform = (Transform)components.get(2 * local0ID);
            Transform OneTransform = (Transform)components.get(2 * local1ID);

            Rigidbody ZeroRb = (Rigidbody) components.get((2 * local0ID) + 1);
            Rigidbody OneRb = (Rigidbody) components.get((2 * local1ID) + 1);

            //Adding force to Each rigibody equal to the normal of the other collider times the dotproduct of the other Velocity and the normal
            Rigidbody.AddForce(OneRb, Vec2.Scale(Vec2.DotVector(ZeroRb.Velocity, collision.normals[0].getNormalized()), ZeroRb.Mass/OneRb.Mass));
            Rigidbody.AddForce(ZeroRb, Vec2.Scale(Vec2.DotVector(OneRb.Velocity, collision.normals[1].getNormalized()), OneRb.Mass/ZeroRb.Mass));

            //Rigidbodies applying reaction forces to themselves
            Rigidbody.AddForce(ZeroRb, Vec2.Scale(Vec2.DotVector(ZeroRb.Velocity, collision.normals[0].getNormalized()).invert(), 3.7f));
            Rigidbody.AddForce(OneRb, Vec2.Scale(Vec2.DotVector(OneRb.Velocity, collision.normals[1].getNormalized()).invert(), 3.7f));
        }
    }
}
