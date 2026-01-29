package Main;

import javax.swing.JFrame;

//The class used for outputting
public class GameWindow
{
    private JFrame frame;

    private String WindowName = "Fun Game";

    public GameWindow(Game game){
        initialize(game);
    }

    public GamePanel gPanel;

    private void initialize(Game game){
        frame = new JFrame();

        frame.setTitle(WindowName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gPanel = new GamePanel(game);
        frame.add(gPanel);

        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public int GetWidth(){
        return frame.getWidth();
    }

    public int GetHeight(){
        return frame.getHeight();
    }
}