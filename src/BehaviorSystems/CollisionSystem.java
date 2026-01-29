package BehaviorSystems;

import Components.Collider;
import Components.Collision;
import Components.Component;
import Components.Transform;
import Main.Game;
import SystemInterfaces.ICheckCollisions;
import Utility.MiscFunctions;
import Utility.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class CollisionSystem extends  BehaviorSystem implements ICheckCollisions {

    //Centre is the centre of the simulation area, WidthHeight half of the width and height (centre + Widthheight is the topright corner),Takes the Transform THEN Collider
    @Override
    public void CheckCollisions(Vec2 Centre, Vec2 WidthHeight, ArrayList<Component> components) {

        //Setting up an array of references to a set of components
        ArrayList<Integer> refArray = new ArrayList<>();
        for (int i = 0; i < components.toArray().length/2; i++) {
            refArray.add(i);
            currentTransforms.add((Transform) components.get(i*2));
            currentColliders.add((Collider) components.get((i*2)+1));
            pushVectors.add(Vec2.Zero());
        }

        QuadTreeCollisions(refArray, Centre, WidthHeight );

        //Fixing the hard collisions HERE
        for (int i = 0; i < pushVectors.size(); i++){
            currentTransforms.get(i).Position = Vec2.Add(currentTransforms.get(i).Position , pushVectors.get(i));
        }

        //emptying the list so they're not stored for longer than necessary
        currentTransforms.clear();
        currentColliders.clear();

        //adding the collisions to the Game for processing
        if(mainGame!=null && !collisions.isEmpty()){mainGame.addCollisions(collisions);}

        collisions.clear();
        pushVectors.clear();
    }

    //Stores vectors to be added to the transforms so hard colliders arent colliders
    ArrayList<Vec2> pushVectors = new ArrayList<>();


    //The current list of collisions
    ArrayList<Collision> collisions = new ArrayList<>();

    //The number of objects in a quad necessary for it to split
    final int maxInQuad = 20;

    //the current components being calculated with
    ArrayList<Transform> currentTransforms = new ArrayList<>();
    ArrayList<Collider> currentColliders = new ArrayList<>();

    //Constructor
    public CollisionSystem(){
        Type = SystemTypes.CollisionSystem;
        Calls.add(CallTypes.CheckCollisions);
        ReliantComponents.set(Component.ComponentTypes.Transform.ordinal());
        ReliantComponents.set(Component.ComponentTypes.Collider.ordinal());
    }

    void QuadTreeCollisions(ArrayList<Integer> References, Vec2 Centre, Vec2 WidthHeight){
        //Determining the centres and dimensions of the quads, ordered NW, NE, SE, SW
        Vec2[] centres = new Vec2[]{new Vec2(Centre.x - WidthHeight.x/2, Centre.y + WidthHeight.y/2),
                                    new Vec2(Centre.x + WidthHeight.x/2, Centre.y + WidthHeight.y/2),
                                    new Vec2(Centre.x + WidthHeight.x/2, Centre.y - WidthHeight.y/2),
                                    new Vec2(Centre.x - WidthHeight.x/2, Centre.y - WidthHeight.y/2)};
        Vec2 SubWidthHeight = Vec2.Scale(WidthHeight, (float) 1 /2);

        //Lists of references to be sent to each of the respective quads
        ArrayList<ArrayList<Integer>> QuadLists = new ArrayList<>();
        for (int i = 0; i < 4; i++) { QuadLists.add(new ArrayList<>());}

        //Sorting into quads
        for (int i = 0; i < References.toArray().length; i++)
        {
            for (int j = 0; j < 4; j++) {
                if(isInRangeOfQuad(i, centres[j], SubWidthHeight)){
                    QuadLists.get(j).add(i);
                }
            }
        }

        //Checking quad sizes. if one is too big this function is called on it, otherwise collisions are checked within that list
        for (int i = 0; i < 4; i++) {
            //Too many so subdivides
            if (QuadLists.get(i).size() > maxInQuad){
                QuadTreeCollisions(QuadLists.get(i), centres[i], SubWidthHeight);
            }
            //not too many so checks collisions
            else {
                CheckCollisions(QuadLists.get(i));
            }
        }
    }

    //Checks whether the collider referenced by the integer is within the bounds of the quad indicated by the centre, height and width
    boolean isInRangeOfQuad(Integer Reference, Vec2 Centre, Vec2 WidthHeight){

        //Gets the closest point on the collider to the centre of the quad
        Vec2 reach = GetClosestPosition(currentColliders.get(Reference), currentTransforms.get(Reference), Centre);

        //The top right and bottom left corners of the quad
        Vec2 TopRight = Vec2.Add(Centre,WidthHeight);
        Vec2 BotLeft = Vec2.Subtract(Centre,WidthHeight);

        //Returning true if the "Reach" point is within the bounds, thus meaning it is in the quad
        return (reach.x <= TopRight.x && reach.x >= BotLeft.x && reach.y <= TopRight.y && reach.y >= BotLeft.y);
    }

    void CheckCollisions(List<Integer> References){
        //for every collider-pair within the list
        for (int A = 0; A < References.size(); A++) {

            for (int B = A+1; B < References.size(); B++) {

                //if this collision hasn't already been checked (two colliders can be overlapping in two or more quads which would create excess collisions)
                if(!collisionHappened(A,B)) {

                    //getting the closest positions in either collider to the other
                    Vec2 closestAtoB = GetClosestPosition(currentColliders.get(A), currentTransforms.get(A) ,Vec2.Add(currentTransforms.get(B).Position, currentColliders.get(B).OffsetVector));
                    Vec2 closestBtoA = GetClosestPosition(currentColliders.get(B), currentTransforms.get(B) ,Vec2.Add(currentTransforms.get(A).Position, currentColliders.get(A).OffsetVector));

                    //Checking whether the "Reach" point is in the other collider
                    boolean AinB = isInCollider(currentColliders.get(B), currentTransforms.get(B), closestAtoB);
                    boolean BinA = isInCollider(currentColliders.get(A), currentTransforms.get(A), closestBtoA) ;

                    if (BinA || AinB ) {

                        //There is a collision
                        Collision collision = new Collision();
                        collision.colliders = new int[]{A,B};

                        //Determining the point of collision
                        if(AinB && BinA)
                        {
                            //if both reach points were in the other collider
                            if(currentTransforms.get(A).positionLocked){
                                collision.point = closestAtoB;
                            }
                            else if(currentTransforms.get(B).positionLocked){
                                collision.point = closestBtoA;
                            }
                            else {
                                collision.point = Vec2.Scale(Vec2.Add(closestAtoB, closestBtoA), (float) 1/2 );
                            }
                        }
                        else if (!AinB) {
                            collision.point = closestBtoA;
                        }
                        else {
                            collision.point = closestAtoB;
                        }

                        //Determining normals
                        collision.normals[0] = getNormal(currentColliders.get(A), currentTransforms.get(A),collision.point);
                        collision.normals[1] = getNormal(currentColliders.get(B), currentTransforms.get(B),collision.point);

                        //Doing diff actions depending on the collider types (first digit is ordinal of A, second is ordinal of B)
                        switch (10 * currentColliders.get(A).colliderType.ordinal() + currentColliders.get(B).colliderType.ordinal()){
                            case 0 ->{
                                //Updating the push directions
                                pushVectors.set(A, Vec2.Add(pushVectors.get(A), Vec2.Subtract(collision.point, closestAtoB)));
                                pushVectors.set(B, Vec2.Add(pushVectors.get(B), Vec2.Subtract(collision.point, closestBtoA)));

                                collision.collisionType = Collision.CollisionTypes.Enter;
                                break;
                            }
                            case 11 ->{
                            }
                            case 10->{
                                if(currentColliders.get(A).containedColliders.contains(B)){
                                    collision.collisionType = Collision.CollisionTypes.Stay;
                                }
                                else {
                                    collision.collisionType = Collision.CollisionTypes.Enter;
                                    currentColliders.get(A).containedColliders.add(B);
                                }

                                if(currentColliders.get(B).colliderType.ordinal() < 1){break;}
                            }
                            case 01->{
                                if(currentColliders.get(B).containedColliders.contains(A)){
                                    collision.collisionType = Collision.CollisionTypes.Stay;
                                }
                                else {
                                    collision.collisionType = Collision.CollisionTypes.Enter;
                                    currentColliders.get(B).containedColliders.add(A);
                                }

                                break;
                            }


                        }

                        //Adding the collision to the list
                        collisions.add(collision);
                    }

                    //Managing leave triggers
                    else if(currentColliders.get(A).containedColliders.contains(B) || currentColliders.get(B).containedColliders.contains(A)){

                        //There is a collision
                        Collision collision = new Collision();
                        collision.colliders = new int[]{A,B};

                        //Doing diff actions depending on the collider types (first digit is ordinal of A, second is ordinal of B)
                        switch (10 * currentColliders.get(A).colliderType.ordinal() + currentColliders.get(B).colliderType.ordinal()){
                            case 11 ->{
                            }
                            case 10->{
                                if(currentColliders.get(A).containedColliders.contains(B)){
                                    collision.collisionType = Collision.CollisionTypes.Leave;
                                    currentColliders.get(A).containedColliders.remove((Object)B);
                                }
                                if(currentColliders.get(B).colliderType.ordinal() < 1){break;}
                            }
                            case 01->{
                                if(currentColliders.get(B).containedColliders.contains(A)){
                                    collision.collisionType = Collision.CollisionTypes.Leave;
                                    currentColliders.get(B).containedColliders.remove((Object)A);
                                }

                                break;
                            }
                        }

                        //Adding the collision to the list
                        collisions.add(collision);
                    }
                }
            }
        }
    }

    //Gets the closest position on the collider referenced to the position
    Vec2 GetClosestPosition(Collider collider, Transform transformPos, Vec2 position){

        //Fetching references to safe repetitive fetching
        Vec2 colliderCentre = Vec2.Add(collider.OffsetVector, transformPos.Position);

        //uses diff methods depending on the collider shape
        switch (collider.getShape()){
            case Box -> {
                return position.getClamped(colliderCentre.x - collider.Width / 2.0F, colliderCentre.x + collider.Width / 2.0F, colliderCentre.y - collider.Height / 2.0F, colliderCentre.y + collider.Height / 2.0F);
            }
            case Round -> {
                Vec2 hereToThere = Vec2.Subtract(position,colliderCentre);
                hereToThere.x *= collider.Height / collider.Width;
                hereToThere = Vec2.SetMagnitude(hereToThere, collider.Height / 2.0F);
                hereToThere.x *= collider.Width / collider.Height;
                return Vec2.Add(hereToThere, colliderCentre);
            }
            case Vertex -> {
                if (collider.vertexes.length < 2) {
                    return colliderCentre;
                }
                else {
                    float currentDistance = -1.0F;
                    Vec2 currentClosest = Vec2.Zero();

                    for(int a = 0; a < collider.vertexes.length; ++a) {
                        int b = MiscFunctions.incrementIndex(collider.vertexes, a);
                        Vec2 d = Vec2.getClosestOnEdge(collider.vertexes[a], collider.vertexes[b], position);
                        if (currentDistance < 0.0F || currentDistance > Vec2.Subtract(d, position).getMagnitude()) {
                            currentDistance = Math.abs(Vec2.GetDistanceFromLine(collider.vertexes[a], collider.vertexes[b], position));
                            currentClosest = d;
                        }
                    }

                    return currentClosest;
                }
            }
            case Complex -> {
                float currentDistance = -1.0F;
                Vec2 currentClosest = colliderCentre;

                for(Collider Tcollider : collider.colliders) {
                    Vec2 tempClosest = GetClosestPosition(collider, transformPos, position);
                    float distance = Vec2.Subtract(tempClosest, position).getMagnitude();
                    if (currentDistance < 0.0F || currentDistance > distance) {
                        currentDistance = distance;
                        currentClosest = tempClosest;
                    }
                }

                return currentClosest;
            }
        }
        //Catch
        return Vec2.Zero();
    }

    //Checks whether a given point is inside a collider
    boolean isInCollider(Collider currentcollider, Transform currentTransform , Vec2 position){

        //Fetching references to safe repetitive fetching
        Vec2 colliderCentre = Vec2.Add(currentcollider.OffsetVector, currentTransform.Position);

        switch (currentcollider.getShape()){
            case Box -> {
                return position.x >= colliderCentre.x - currentcollider.Width / 2.0F && position.x <= colliderCentre.x + currentcollider.Width / 2.0F && position.y >= colliderCentre.y - currentcollider.Height / 2.0F && position.y <= colliderCentre.y + currentcollider.Height / 2.0F;
            }
            case Round -> {
                Vec2 hereToThere = Vec2.Subtract(position, colliderCentre);
                hereToThere.x *= currentcollider.Height / currentcollider.Width;
                return hereToThere.getMagnitude() <= currentcollider.Height / 2.0F;
            }
            case Vertex -> {
                if (currentcollider.vertexes.length < 2) {
                    return false;
                } else {
                    for(int a = 0; a < currentcollider.vertexes.length; ++a) {
                        int b = MiscFunctions.incrementIndex(currentcollider.vertexes, a);
                        if (!Vec2.isInEdge(currentcollider.vertexes[a], currentcollider.vertexes[b], position)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            case Complex -> {
                for(Collider collider : currentcollider.colliders) {
                    if (isInCollider(collider ,currentTransform, position)) {
                        return true;
                    }
                }
                return false;
            }
        }
        //Catch
        return false;
    }

    //Returns the normal from the reference collider to the position FILL IN WITH KNOWN CALCULATIONS
    Vec2 getNormal(Collider currentcollider, Transform currentTransform , Vec2 position){

        //Fetching references to safe repetitive fetching
        Vec2 colliderCentre = Vec2.Add(currentcollider.OffsetVector, currentTransform.Position);

        switch (currentcollider.getShape()){
            case Box -> {
                Vec2 heretoThere = Vec2.Subtract(position, Vec2.Add(currentcollider.OffsetVector, currentTransform.Position));
                if ((double)heretoThere.getMagnitude() < 1.0E-5) {
                    heretoThere = Vec2.Scale(heretoThere, 10000.0F);
                }

                return Math.abs(heretoThere.x) > Math.abs(heretoThere.y) ? new Vec2(heretoThere.x, 0.0F) : new Vec2(0.0F, heretoThere.y);
            }
            case Round -> {
                return Vec2.Subtract(position, colliderCentre);
            }
            case Vertex -> {
                if (currentcollider.vertexes.length < 2) {
                    return colliderCentre;
                } else {
                    float currentDistance = -1.0F;
                    Vec2 currentClosest = Vec2.Zero();

                    for(int a = 0; a < currentcollider.vertexes.length; ++a) {
                        int b = MiscFunctions.incrementIndex(currentcollider.vertexes, a);
                        Vec2 d = Vec2.getClosestOnEdge(currentcollider.vertexes[a], currentcollider.vertexes[b], position);
                        if (currentDistance < 0.0F || currentDistance > Vec2.Subtract(d, position).getMagnitude()) {
                            currentDistance = Math.abs(Vec2.GetDistanceFromLine(currentcollider.vertexes[a], currentcollider.vertexes[b], position));
                            currentClosest = Vec2.normalFromLine(currentcollider.vertexes[a], currentcollider.vertexes[b]).invert();
                        }
                    }

                    return currentClosest;
                }
            }
            case Complex -> {
                float currentDistance = -1.0F;
                Vec2 bestVector = Vec2.Zero();
                for(Collider collider : currentcollider.colliders) {
                    Vec2 normal = getNormal(collider, currentTransform, position);
                    if (currentDistance < 0 || normal.getMagnitude() < currentDistance){
                        currentDistance = normal.getMagnitude();
                        bestVector = normal;
                    }
                }
                return bestVector;
            }
        }

        //Catch
        return Vec2.Zero();
    }

    //Checks whether a collision has already happened between two colliders
    boolean collisionHappened(int ref1, int ref2){
        int LeaveCollision = -1;
        for(int i = 0; i < collisions.size(); i++){
            Collision collision = collisions.get(i);
            if(collision.ContainsColliders(ref1,ref2) && collision.collisionType != Collision.CollisionTypes.Leave){
                return true;
            }
            else if(collision.collisionType == Collision.CollisionTypes.Leave){
                LeaveCollision = i;
            }
        }
        if(LeaveCollision != -1){collisions.remove(LeaveCollision);}
        return false;
    }
}
