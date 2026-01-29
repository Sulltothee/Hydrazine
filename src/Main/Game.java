package Main;

import BehaviorSystems.*;
import Components.*;
import Components.Component;
import SystemInterfaces.*;
import Utility.Vec2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.BitSet;

public class Game implements Runnable
{
    Vec2 CameraLocation = Vec2.Zero();
    Vec2 CameraScope = new Vec2(10);

    //The window the game renders on
    private GameWindow gameWindow;

    //The thread the game runs on
    Thread gameThread;

    //The actual frames per second
    private int FPScurrent;

    //The maximum frames per second
    private float FPSmax = 20f;

    //List of empty Main.Entity slots
    ArrayList<Integer> emptyEntitySlots = new ArrayList<>();

    //All of the entities in the game
    ArrayList<Entity> Entities = new ArrayList<>();

    //All the components, X is the type, Y is the Main.Entity ID
    ArrayList<ArrayList<Component>> Components = new ArrayList<>(Component.ComponentTypes.values().length);

    //All the classes of components in type order
    static Class[] ComponentClasses = {TestComponent.class, Transform.class, Rigidbody.class, Collider.class, Sprite.class};

    //All the systems
    ArrayList<BehaviorSystem> Systems = new ArrayList<>();

    //References to the systems based on when they're called.
    ArrayList<ArrayList<Integer>> CallList = new ArrayList<>(BehaviorSystem.CallTypes.values().length);

    //All the entities who need their start functions ran
    ArrayList<Integer> StartList = new ArrayList<>();

    ArrayList<Collision> collisionList = new ArrayList<>();

    //Constructor
    public Game(){
        //Initializing the window
        gameWindow = new GameWindow(this);

        //Initializing the arraylists
        for (int i = 0; i < Component.ComponentTypes.values().length; i++) {
            Components.add(new ArrayList<>());
        }
        for (int i = 0; i < BehaviorSystem.CallTypes.values().length; i++) {
            CallList.add(new ArrayList<>());
        }

        //Instantiating systems, probably needs to be done in enum order
        InstantiateSystem(TestSystem.class);
        InstantiateSystem(RigidbodySimulator.class);
        InstantiateSystem(CollisionSystem.class);
        InstantiateSystem(SpriteRenderer.class);

        //setting values to be given to the constructor
        BitSet components = new BitSet(Component.ComponentTypes.values().length);
        components.set(Component.ComponentTypes.Rigidbody.ordinal());
        components.set(Component.ComponentTypes.Transform.ordinal());
        components.set(Component.ComponentTypes.Collider.ordinal());
        components.set(Component.ComponentTypes.Sprite.ordinal());
        BitSet systems = new BitSet(BehaviorSystem.SystemTypes.values().length);
        systems.set(BehaviorSystem.SystemTypes.RigidbodySimulator.ordinal());
        systems.set(BehaviorSystem.SystemTypes.CollisionSystem.ordinal());
        systems.set(BehaviorSystem.SystemTypes.SpriteRenderer.ordinal());

        //Adding entities
        InstantiateEntity(components,systems);

        InstantiateEntity(components, systems);

        Rigidbody.AddForce((Rigidbody)(getComponent(Component.ComponentTypes.Rigidbody,0)), new Vec2(2,0));
        ((Transform)getComponent(Component.ComponentTypes.Transform,1)).Position = new Vec2(5,0);
        try {
            ((Sprite)getComponent(Component.ComponentTypes.Sprite,1)).sprite = ImageIO.read(getClass().getResourceAsStream("/Robot.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Returns the component with type type associated with entity Entity)
    public Component getComponent(Component.ComponentTypes Type, int entity){
        return Components.get(Type.ordinal()).get(entity);
    }

    //Adds a new System of the given class
    public BehaviorSystem InstantiateSystem(Class systemClass){
        //Checks it's a valid class
        if(BehaviorSystem.class.isAssignableFrom(systemClass)){
        try {
            //the constructor of the class
            Constructor constructor;

            //Fetching then running the "blank" constructor
            constructor = systemClass.getConstructor(null);
            BehaviorSystem newSystem = (BehaviorSystem) constructor.newInstance(null);

            //Adding the system to the systemList
            Systems.add(newSystem);

            //The index of the system in the system array
            int sysIndex = Systems.toArray().length-1;

            //Adding its index to all the appropriate call lists
            for (BehaviorSystem.CallTypes calltype : newSystem.Calls) {
                CallList.get(calltype.ordinal()).add(sysIndex);
            }

            //adding this object as a reference if it wants it
            newSystem.setMainGame(this);

            //returning the system for (potential) further modification
            return newSystem;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        }
        return null;
    }

    //Instantiates an object
    public Entity InstantiateEntity(BitSet linkedComponents, BitSet linkedSystems){
        //Creating the new entity
        Entity entity = new Entity();
        int entityIndex = 0;

        //if there are gaps in the entity list adding the new entity to the lowest gap
        if(!emptyEntitySlots.isEmpty()){
            entityIndex = emptyEntitySlots.getFirst();
            Entities.set(emptyEntitySlots.getFirst(), entity);
            emptyEntitySlots.removeFirst();
        }
        //Otherwise adding it to the end of the array
        else
        {
            Entities.add(entity); entityIndex = Entities.toArray().length-1;
            for(ArrayList<Component> components : Components){
                components.add(null);
            }
        }

        //Adding it to the start list to finish creating it
        StartList.add(entityIndex);

        //Setting the archetype in accordance with the components it has
        Entities.get(entityIndex).Archetype = linkedComponents;

        //Adding and instantiating the components
        for (int i = 0; i < Component.ComponentTypes.values().length; i++) {

            //if it should have that component
            if(linkedComponents.get(i) && Component.class.isAssignableFrom(ComponentClasses[i])){

                //Making and adding the component
                try {
                    Component component = (Component)(ComponentClasses[i].getConstructor(null).newInstance(null));
                    Components.get(i).set(entityIndex,component);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //Adding the object to the call list
        for (int i = 0; i < BehaviorSystem.SystemTypes.values().length; i++) {
            //Adding the Object to the affected entities list if the associated system's point in the bitset is true;
            if (linkedSystems.get(i)) {
                Systems.get(i).AffectingObjects.add(entityIndex);
            }
        }

        //returning the entity for further modification
        return entity;
    }

    //Starts the thread
    public void StartGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    //Contains the game loop
    public void run(){
        //The time since last frame
        long deltaTime = (long)(1e9/FPSmax);

        //The nanoTime when the frame started
        long FrameStartTime;

        //The nanotime between frames on the maximum fps
        long maxDeltaTime = (long)(1e9/FPSmax);

        while(gameThread != null){
            //Updating the frame's start time for FPS calculation
            FrameStartTime = System.nanoTime();

            //Do stuff
            //Empty the start Queue
            RunStarts();

            //Run Update functions
            Update(deltaTime/1e9f);

            //Run physics calculations
            PhysicsUpdate(deltaTime);

            //Run collision checks
            CheckCollisions();

            //Fix hard collisions

            //Run collision triggers
            RunCollisions();

            //Rendering
            gameWindow.gPanel.repaint();

            //Update delta time
            deltaTime =  System.nanoTime() - FrameStartTime;

            //UpdateFrameRate
            FPScurrent = (int)(1/(deltaTime + 1));

            //Sleeping if the max FPS was exceeded
            if(deltaTime < maxDeltaTime){
                try
                {
                    Thread.sleep((long)((maxDeltaTime-deltaTime)/1e6));
                }
                catch (InterruptedException ie)
                {
                    ie.printStackTrace();
                }
            }


        }
    }

    //Runs the Start methods for all objects that haven't been started yet
    void RunStarts(){
        //For every system with a start
        for(int SystemIndex : CallList.get(BehaviorSystem.CallTypes.Start.ordinal())){
            //list of components to be passed to the system
            ArrayList<Component> ArgumentComponents = new ArrayList<>();

            //For every Main.Entity affected by that system
            for(int EntityIndex : Systems.get(SystemIndex).AffectingObjects){

                //if the Start list contains that entity and the Main.Entity has all the reliant components
                BitSet comparison = (BitSet)Systems.get(SystemIndex).ReliantComponents.clone();
                comparison.and(Entities.get(EntityIndex).Archetype);
                if(StartList.contains(EntityIndex) && comparison.cardinality() == Systems.get(SystemIndex).ReliantComponents.cardinality()){
                    //The index of the Previous true
                    int pSetBit = 0;

                    //Adding all the necessary components to the list
                    for (int i = 0; i < comparison.cardinality(); i++) {
                        //Getting the next necessary component
                        pSetBit = comparison.nextSetBit(pSetBit);

                        //Adding the component to the list
                        ArgumentComponents.add(Components.get(pSetBit).get(EntityIndex));
                    }
                }
            }

            //Running start after all the components have been accumulated
            ((IStart)Systems.get(SystemIndex)).Start(ArgumentComponents);
        }

        //Emptying the startlist
        StartList.clear();
    }

    //Runs every frame, takes the time in seconds for the last frame to complete
    void Update(float deltaTime){
        try {
            CallTimes(BehaviorSystem.CallTypes.Update, deltaTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Checks and updates rigidbody physics, ran every frame
    void PhysicsUpdate(float deltaTime){
        try {
            CallTimes(BehaviorSystem.CallTypes.PhysicsUpdate, deltaTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Checks Collisions between colliders and adds collisontriggers 
    void CheckCollisions(){

        //needs to take camera and simulation range
        try {
            ArrayList<Object> argList = new ArrayList<>();

            //Adding the centre point
            argList.add(Vec2.Zero());

            //Adding the WidthHeight
            argList.add(new Vec2(30));
            CallTimes(BehaviorSystem.CallTypes.CheckCollisions, argList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //adds collisions to the list of unprocessed collisions
    public void addCollisions(ArrayList<Collision> collisions){
        collisionList.addAll(collisions);
    }

    //Runs collision triggers
    void RunCollisions(){
        if(!collisionList.isEmpty()) {

            //compiling a list of all the collided component IDS
            ArrayList<Integer> collidedEntities = new ArrayList<>();
            for (Collision collision : collisionList){
                if(!collidedEntities.contains(collision.colliders[0])){
                    collidedEntities.add(collision.colliders[0]);
                }

                if(!collidedEntities.contains(collision.colliders[1])){
                    collidedEntities.add(collision.colliders[1]);
                }
            }

            //For every system with a collision
            for(int SystemIndex : CallList.get(BehaviorSystem.CallTypes.Collide.ordinal())){
                //list of components to be passed to the system
                ArrayList<Component> ArgumentComponents = new ArrayList<>();

                //List of Entity IDs in the order they appear in the components
                ArrayList<Integer> EntityIDs = new ArrayList<>();

                //For every Main.Entity affected by that system
                for(int EntityIndex : Systems.get(SystemIndex).AffectingObjects){

                    //if the Main.Entity has all the reliant components and was involved in a collision
                    BitSet comparison = (BitSet)Systems.get(SystemIndex).ReliantComponents.clone();
                    comparison.and(Entities.get(EntityIndex).Archetype);

                    if(comparison.cardinality() == Systems.get(SystemIndex).ReliantComponents.cardinality() && collidedEntities.contains(EntityIndex)){

                        EntityIDs.add(EntityIndex);

                        //The index of the first component type
                        int pSetBit = comparison.nextSetBit(0);

                        //Adding all the necessary components to the list
                        for (int i = 0; i < comparison.cardinality(); i++) {
                            //Adding the component to the list
                            ArgumentComponents.add(Components.get(pSetBit).get(EntityIndex));

                            //Getting the next necessary component type
                            pSetBit = comparison.nextSetBit(pSetBit+1);
                        }
                    }
                }

                //Running The relevant function after all the components have been accumulated
                ((ICollide)Systems.get(SystemIndex)).OnCollision(collisionList, EntityIDs , ArgumentComponents);
            }

            //Emptying the collision list
            collisionList.clear();
        }
    }

    //Renders
    public void Render(Graphics2D g){
        CallTimes(BehaviorSystem.CallTypes.Render, g );
    }


    //Calls every system with a given calltype. calls the given method with the given parameters with the necessary components after ordered by the "parent" entity then sub-ordered buy the components ID
    void CallTimes(BehaviorSystem.CallTypes calltype, Object parameter)
    {
        //Instantiates a new list
        ArrayList<Object> tempList = new ArrayList<>();

        //Doesn't add an empty parameter to the list
        if(parameter != null)  {tempList.add(parameter);}

        //calls the "real" method
        CallTimes(calltype,tempList);
    }
    void CallTimes(BehaviorSystem.CallTypes calltype, ArrayList<Object> parameters){
        //For every system with a start
        for(int SystemIndex : CallList.get(calltype.ordinal())){
            //list of components to be passed to the system
            ArrayList<Component> ArgumentComponents = new ArrayList<>();

            //For every Main.Entity affected by that system
            for(int EntityIndex : Systems.get(SystemIndex).AffectingObjects){

                //if the Main.Entity has all the reliant components
                BitSet comparison = (BitSet)Systems.get(SystemIndex).ReliantComponents.clone();
                comparison.and(Entities.get(EntityIndex).Archetype);
                if(comparison.cardinality() == Systems.get(SystemIndex).ReliantComponents.cardinality()){
                    //The index of the first component type
                    int pSetBit = comparison.nextSetBit(0);

                    //Adding all the necessary components to the list
                    for (int i = 0; i < comparison.cardinality(); i++) {
                        //Adding the component to the list
                        ArgumentComponents.add(Components.get(pSetBit).get(EntityIndex));

                        //Getting the next necessary component type
                        pSetBit = comparison.nextSetBit(pSetBit+1);
                    }
                }
            }

            //Adding the components to the parameters
            parameters.add(ArgumentComponents);

            //Running The relevant function after all the components have been accumulated
            switch (calltype){
                case Update -> {
                    ((IUpdate)Systems.get(SystemIndex)).Update( (float)parameters.getFirst() ,ArgumentComponents);
                    break;
                }
                case PhysicsUpdate -> {
                    ((IPhysicsUpdate)Systems.get(SystemIndex)).PhysicsUpdate((float)parameters.getFirst(), ArgumentComponents);
                    break;
                }
                case CheckCollisions -> {
                    ((ICheckCollisions)Systems.get(SystemIndex)).CheckCollisions((Vec2)parameters.getFirst(), (Vec2)parameters.get(1), ArgumentComponents);
                    break;
                }
                case Render -> {
                    ((IRender)Systems.get(SystemIndex)).Render( (Graphics2D)parameters.getFirst(), CameraLocation, CameraScope,new Vec2(gameWindow.GetWidth()/2, gameWindow.GetHeight()/2),ArgumentComponents);
                    break;
                }
            }
        }
    }
}