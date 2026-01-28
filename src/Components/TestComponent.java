package Components;

import java.awt.*;

public class TestComponent extends Component
{
    String mystring = ("My special string");

    public String getString(){
        return mystring;
    }

    public TestComponent(){
        TypeID = Component.ComponentTypes.TestComponent;
    }
}