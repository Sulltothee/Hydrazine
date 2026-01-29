package Components;

import Utility.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Sprite extends Component{
    public BufferedImage sprite;

    public Vec2 spriteDimensions = new Vec2(48);

    public Sprite(){
        TypeID = ComponentTypes.Sprite;

        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/Wizard.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
