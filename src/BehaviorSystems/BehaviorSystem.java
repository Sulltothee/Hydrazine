package BehaviorSystems;

import Components.Component;
import Main.Game;

import java.util.ArrayList;
import java.util.BitSet;

public abstract class BehaviorSystem {

    //Reference to the main.Game controller object
    public Game mainGame = null;

    public void setMainGame(Game mainGame) {
        this.mainGame = mainGame;
    }

    //The components this system requires to run
    public BitSet ReliantComponents = new BitSet(16);

    public static enum SystemTypes {TestSystem, RigidbodySimulator, CollisionSystem, SpriteRenderer}
    public SystemTypes Type;

    //Types of times to be called
    public static enum CallTypes{Start, Update, PhysicsUpdate, CheckCollisions, Collide ,Render}

    //The call type of this System
    public ArrayList<CallTypes> Calls = new ArrayList<>();

    //The list of entity ids this affects
    public ArrayList<Integer> AffectingObjects = new ArrayList<>();
}
