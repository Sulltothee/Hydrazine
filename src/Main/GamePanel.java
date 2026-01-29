package Main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    final int screenHeightPixels = 512; //How tall the screen is in pixels
    final int screenWidthPixels = 1024; //How wide the screen is in pixels
    Game MainGame;

    //Constructor
    public GamePanel(Game mainGame){
        this.setPreferredSize(new Dimension(screenWidthPixels, screenHeightPixels));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        MainGame = mainGame;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        MainGame.Render((Graphics2D) g);
        g.dispose();
    }
}
