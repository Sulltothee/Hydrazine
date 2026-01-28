package SystemInterfaces;

import Components.Component;

import java.util.ArrayList;

public interface IUpdate {
    public void Update(float deltaTime, ArrayList<Component> components);
}
