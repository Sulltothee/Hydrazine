package Main;

import javax.swing.JFrame;

//The class used for outputting
public class GameWindow
{
    private JFrame frame;

    private String WindowName = "Fun Main.Game";

    public GameWindow(){
        initialize();
    }

    private void initialize(){
        frame = new JFrame();

        frame.setTitle(WindowName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setSize(500,400);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}