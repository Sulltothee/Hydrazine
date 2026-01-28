package Main;

import Components.Component;

import java.util.BitSet;

public class Entity {
    public int ID = 0;
    public BitSet Archetype = new BitSet(Component.ComponentTypes.values().length);
}
