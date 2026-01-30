package Main;

import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.BitSet;

public class inputManager implements KeyListener, MouseListener {

    //Stores pressed keys, look at the java documentation for which key is which
    public BitSet pressedKeys = new BitSet(128);

    //yet to work
    public BitSet mouseEvents = new BitSet();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    //Adds/ removes keys from being pressed
    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.set(e.getKeyCode());
    }
    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.clear(e.getKeyCode());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
