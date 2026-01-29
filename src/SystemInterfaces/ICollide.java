package SystemInterfaces;

import Components.Collision;
import Components.Component;

import java.util.ArrayList;

public interface ICollide {
    public void OnCollision(ArrayList<Collision> collision, ArrayList<Integer> EntityIDs, ArrayList<Component> components);
}
