package SystemInterfaces;

import Components.Component;
import Utility.Vec2;

import java.awt.*;
import java.util.ArrayList;

public interface IRender {
    void Render(Graphics2D g, Vec2 CameraLocation, Vec2 CameraScope, Vec2 WindowScope, ArrayList<Component> components);
}
