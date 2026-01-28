package SystemInterfaces;

import Components.Component;

import java.util.ArrayList;

public interface IPhysicsUpdate {
    public void PhysicsUpdate(float deltaTime, ArrayList<Component> components);
}
