package BehaviorSystems;

import Components.Component;

import java.util.ArrayList;
import java.util.BitSet;

public abstract class BehaviorSystem {
    //The components this system requires to run
    public BitSet ReliantComponents = new BitSet(16);

    public static enum SystemTypes {TestSystem, RigidbodySimulator, CollisionSystem}
    public SystemTypes Type;

    //Types of times to be called
    public static enum CallTypes{Start, Update, PhysicsUpdate, CheckCollisions, Render}

    //The call type of this System
    public ArrayList<CallTypes> Calls = new ArrayList<>();

    //The list of entity ids this affects
    public ArrayList<Integer> AffectingObjects = new ArrayList<>();
}
