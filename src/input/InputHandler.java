package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JFrame;

public class InputHandler implements MouseListener, KeyListener {
	private boolean[] buttonPress = new boolean[4];
	LinkedList<Integer> keyPressList = new LinkedList<Integer>();
	
	public void addListeners(JFrame frame){
		frame.addMouseListener(this);
		frame.addKeyListener(this);
	}
	
	public boolean isMousePressed(int button){
		return buttonPress[button];
	}
	
	public void releaseMouse(int button){
		buttonPress[button] = false;
	}

	public void mousePressed(MouseEvent e) {
		buttonPress[e.getButton()] = true;
	}

	public void mouseReleased(MouseEvent e) {
		buttonPress[e.getButton()] = false;
	}
	
	public boolean isKeyPressed(int keyCode){
		return keyPressList.contains((Integer)keyCode);
	}
	
	public void releaseKey(int keyCode){
		keyPressList.remove((Integer)keyCode);
	}
	
	public void keyPressed(KeyEvent key) {
		if(!keyPressList.contains((Integer)key.getKeyCode())){
			keyPressList.add((Integer)key.getKeyCode());
		}
	}
	
	public void keyReleased(KeyEvent key) {
		releaseKey(key.getKeyCode());
	}
	
	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}
	
	public void keyTyped(KeyEvent arg0) {
	}
}