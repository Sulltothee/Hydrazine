package BehaviorSystems;

import Components.Component;
import Components.Sprite;
import Components.TestComponent;
import Components.Transform;
import SystemInterfaces.IRender;
import Utility.Vec2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class SpriteRenderer extends BehaviorSystem implements IRender {
    @Override
    public void Render(Graphics2D g, Vec2 CameraLocation, Vec2 CameraScope, Vec2 WindowScope, ArrayList<Component> components) {
        for(int i = 0; i<components.size(); i+=2){

            //Turning the sprite's world position into screen positon
            float CameraToWindow = WindowScope.y/(CameraScope.y);

            //The world position relative to the camera
            Vec2 worldPosRel2Cam = Vec2.Subtract(((Transform)components.get(i)).Position, CameraLocation);

            Vec2 WindowPos = Vec2.Scale(   new Vec2(worldPosRel2Cam.x + CameraScope.x, CameraScope.y -worldPosRel2Cam.y)  , CameraToWindow);

            Sprite currentSprite =((Sprite)components.get(i+1));
            g.drawImage(currentSprite.sprite, (int) (WindowPos.x - currentSprite.spriteDimensions.x + WindowScope.x/2)  , (int) (WindowPos.y - currentSprite.spriteDimensions.y), (int)(CameraToWindow) ,(int)(CameraToWindow) , null);
        }
    }

    public SpriteRenderer(){
        ReliantComponents.set(Component.ComponentTypes.Sprite.ordinal());
        ReliantComponents.set(Component.ComponentTypes.Transform.ordinal());
        Calls.add(CallTypes.Render);
        Type = SystemTypes.SpriteRenderer;
    }
}
