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
                    Vec2 frictionForce = Vec2.Scale(currentRigidbody.Velocity.getNormalized().invert() , deltaTime * currentRigidbody.GetMaxFriction());

                    if(frictionForce.getMagnitude() > currentRigidbody.Velocity.getMagnitude()){
                        frictionForce = Vec2.SetMagnitude(frictionForce, currentRigidbody.Velocity.getMagnitude());
                    }

                    if(frictionForce.getMagnitude() > 0.05) {System.out.println(frictionForce.getMagnitude());}

                    Rigidbody.AddAcceleration(currentRigidbody, frictionForce);
                }

                //Updating the entity's position
                currentTransform.Position = Vec2.Add(new Vec2[]{currentTransform.Position, currentRigidbody.Velocity, Vec2.Scale(currentRigidbody.Acceleration, (float) 1 / 2)});


                Vec2 oldVelocity = Vec2.Copy(currentRigidbody.Velocity);

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

            //Elastic collision maths don't @ me

            Vec2 NormalVec0 =  Vec2.Scale(collision.normals[0].getNormalized() ,Vec2.Dot(ZeroRb.Velocity, collision.normals[0].getNormalized()));
            Vec2 NormalVec1 =   Vec2.Scale(collision.normals[1].getNormalized() ,Vec2.Dot(OneRb.Velocity, collision.normals[1].getNormalized()));

            Vec2 newVelocity0 = Vec2.Add(Vec2.Scale(NormalVec0 ,(ZeroRb.Mass - OneRb.Mass)/(OneRb.Mass + ZeroRb.Mass)) , Vec2.Scale(NormalVec1 ,(OneRb.Mass * 2)/(OneRb.Mass + ZeroRb.Mass)));
            Vec2 newVelocity1 = Vec2.Add(Vec2.Scale(NormalVec1 ,(OneRb.Mass - ZeroRb.Mass)/(OneRb.Mass + ZeroRb.Mass)) , Vec2.Scale(NormalVec0 ,(ZeroRb.Mass * 2)/(OneRb.Mass + ZeroRb.Mass)));

            Rigidbody.AddAcceleration(ZeroRb, Vec2.Subtract(newVelocity0,ZeroRb.Velocity));
            Rigidbody.AddAcceleration(OneRb, Vec2.Subtract(newVelocity1,OneRb.Velocity));
        }
    }
}
