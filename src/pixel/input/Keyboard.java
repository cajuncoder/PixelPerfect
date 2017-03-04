package pixel.input;



import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

import pixel.Main;


public class Keyboard implements KeyListener, Serializable {
	boolean[] keys = new boolean[256]; //keys pressed
	public String keyTyped = "";
	public int keyCodeTyped = 0;
	private String lastKeyTyped = "";
	private int lastKeyCodeTyped = 0;
	public boolean up, down, left, right, shift, ctrl, r, g, q, s, tab;
	Main main;

	public Keyboard(Main main) {
		this.main = main;
	}
	//----------------------Keyboard.UPDATE-METHOD----------------------//
	public void update() {
		//note: these key indexes correspond to key IDs containing booleans
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
		shift = keys[KeyEvent.VK_SHIFT];
		ctrl = keys[KeyEvent.VK_CONTROL];
		r = keys[KeyEvent.VK_R];
		g = keys[KeyEvent.VK_G];
		q = keys[KeyEvent.VK_Q];
		s = keys[KeyEvent.VK_S];
		tab = keys[KeyEvent.VK_TAB];
		
		if(keyTyped.equals(lastKeyTyped)) keyTyped = "";
		lastKeyTyped = keyTyped;
		if(keyCodeTyped == lastKeyCodeTyped) keyCodeTyped = 0;
		lastKeyCodeTyped = keyCodeTyped;
		//print key ID for fun, which will be same as index
		//for (int i = 0; i < keys.length; i++) {
		//	if (keys[i]) {
		//		System.out.println("KEY: " + i);
		//	}
		//}
		
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() < keys.length) keys[e.getKeyCode()] = true;
		//System.out.println(e.getKeyCode());
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() < keys.length) keys[e.getKeyCode()] = false;

	}

	public void keyTyped(KeyEvent e) {
		keyTyped = Character.toString(e.getKeyChar());
		//System.out.println(keyTyped);
		keyCodeTyped = e.getKeyChar();
		main.update();
	}
}