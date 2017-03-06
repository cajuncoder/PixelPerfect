package pixel.input;

import pixel.Main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

public class MouseMotion implements MouseMotionListener {

	public int x, y;
	public boolean mouseDragged = false;
	Main main;

	public MouseMotion(Main main) {
		this.main=main;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseDragged = true;
		x = e.getX();
		y = e.getY();
		e.consume();
		main.update();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseDragged = false;
		x = e.getX();
		y = e.getY();
		e.consume();
		main.update();
	}

}
