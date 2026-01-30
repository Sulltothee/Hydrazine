package ProjectScripts;

import BehaviorSystems.BehaviorSystem;
import Components.Component;
import Components.Rigidbody;
import SystemInterfaces.IInput;
import Utility.Vec2;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.BitSet;

public class Player extends BehaviorSystem implements IInput {

    float playerSpeed = 3;

    //Takes the rigidbody
    @Override
    public void RecieveInputs(BitSet keyValues, float deltaTime, ArrayList<Component> components) {
        //Handling player inputs and turning them into force
        for(Component component : components) {
            Vec2 moveDir = new Vec2(0);
            moveDir.y = (keyValues.get(KeyEvent.VK_S) ? -1 : 0) + (keyValues.get(KeyEvent.VK_W) ? 1 : 0);
            moveDir.x = (keyValues.get(KeyEvent.VK_A) ? -1 : 0) + (keyValues.get(KeyEvent.VK_D) ? 1 : 0);

            Rigidbody.AddForce((Rigidbody) component, Vec2.Scale(moveDir.getNormalized(),playerSpeed * deltaTime));
        }
    }

    public Player(){
        ReliantComponents.set(Component.ComponentTypes.Rigidbody.ordinal());
        Calls.add(CallTypes.Input);
        Type = SystemTypes.Player;
    }
}
