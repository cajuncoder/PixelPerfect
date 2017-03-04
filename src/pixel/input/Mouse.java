package pixel.input;

import pixel.Main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;


//add with addMouseListener(new Mouse());
public class Mouse extends MouseAdapter {
	public boolean clickL = false;
	public boolean clickR = false;
	public int x;
	public int y;
	Main main;

	public  Mouse(Main main) {
		this.main=main;
	}
	public void clear() {
		clickL = false;
		clickR = false;
	}
	
	public void mousePressed(MouseEvent e){
		
		if (SwingUtilities.isLeftMouseButton(e)) clickL = true;
		if (SwingUtilities.isRightMouseButton(e)) clickR = true;
		
		x = e.getX();
		y = e.getY();
		main.update();
	}
	
	public void mouseReleased(MouseEvent e){
		clickL = false;
		clickR = false;
		x = e.getX();
		y = e.getY();
	}
	
}
