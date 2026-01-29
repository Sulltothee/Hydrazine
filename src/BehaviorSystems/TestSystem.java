package BehaviorSystems;

import Components.Collision;
import Components.Component;
import Components.TestComponent;
import SystemInterfaces.ICollide;
import SystemInterfaces.IStart;
import SystemInterfaces.IUpdate;

import java.util.ArrayList;

//A temporary system for testing purposes
public class TestSystem extends BehaviorSystem implements IUpdate, IStart, ICollide
{
    //Constructor
    public TestSystem(){
        ReliantComponents.set(Component.ComponentTypes.TestComponent.ordinal());
        Calls.add(CallTypes.Start);Calls.add(CallTypes.Update);Calls.add(CallTypes.Collide);
        Type = SystemTypes.TestSystem;
    }

    public void Update(float deltaTime, ArrayList<Component> components){
        for (Component component : components) {
            System.out.println("Update");
        }
    }

    public void Start(ArrayList<Component> components){
        for (Component component : components) {
            System.out.println("Starting");
        }
    }

    @Override
    public void OnCollision(ArrayList<Collision> collisions, ArrayList<Integer> EntityIDs, ArrayList<Component> components) {
        for (int i = 0 ; i < components.size(); i++) {
            for(Collision collision : collisions){
                if(collision.colliders[0] == i || collision.colliders[1] == i){
                    System.out.println("Collision");
                }
            }
        }
    }
}
