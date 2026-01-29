package Components;

//class that stores data used for systems. EVERY SUBCLASS MUST HAVE AT LEAST ONE "EMPTY" CONSTRUCTOR
public abstract class Component {
    //Whether this Component should be used
    public boolean enabled = true;

    //Enable/Disable functions
    public void Enable(){enabled = true;}
    public void Disable(){enabled = false;}
    public void SetEnabled(boolean enabled){this.enabled = enabled;}
    public boolean GetEnabled() {return enabled;}

    //The types of components
    public static enum ComponentTypes{TestComponent, Transform, Rigidbody, Collider, Sprite}

    //The type of component this object is
    ComponentTypes TypeID;
    public ComponentTypes getTypeID(){
        return TypeID;
    }

}
