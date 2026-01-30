package SystemInterfaces;

import Components.Component;

import java.util.ArrayList;
import java.util.BitSet;

public interface IInput {
    public void RecieveInputs(BitSet keyValues, float deltaTime,  ArrayList<Component> components);
}
