package BehaviorSystems;

import Components.Component;
import Components.TestComponent;
import SystemInterfaces.IStart;
import SystemInterfaces.IUpdate;

import java.util.ArrayList;

//A temporary system for testing purposes
public class TestSystem extends BehaviorSystem implements IUpdate, IStart
{
    //Constructor
    public TestSystem(){
        ReliantComponents.set(Component.ComponentTypes.TestComponent.ordinal());
        Calls.add(CallTypes.Start);Calls.add(CallTypes.Update);
        Type = SystemTypes.TestSystem;
    }

    public void Update(float deltaTime, ArrayList<Component> components){
        for (Component component : components) {
            System.out.println(((TestComponent)component).getString());
        }
    }

    public void Start(ArrayList<Component> components){
        for (Component component : components) {
            System.out.println("Starting");
        }
    }
}
