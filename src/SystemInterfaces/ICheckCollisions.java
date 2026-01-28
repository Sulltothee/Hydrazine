package SystemInterfaces;

import Components.Component;
import Utility.Vec2;

import java.util.ArrayList;

public interface ICheckCollisions {
    //takes the centre of the area to be checked, the width and height of the simulation range (goes up height and down height), a list of Transforms and Colliders
    void CheckCollisions(Vec2 Centre, Vec2 WidthHeight, ArrayList<Component> components);
}
