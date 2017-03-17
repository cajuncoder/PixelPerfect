package pixel;
import pixel.input.Keyboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StreamCorruptedException;
import java.io.StringReader;

import static java.awt.Color.BLACK;

public class Worksheet {
	BufferedImage image;// = (BufferedImage) jpanel.createImage(width, height);
	String filename = "default.png";
	//String filepath = Main.getWorkingDir();
	Graphics2D g;
	int zoom=6;
	int width;
	int height;
	int cursorX;
	int cursorY;
	int gy=0;
	int gx=0;
	//this should have its own graphics context

	public Worksheet(int width, int height) {
		this.width = width;
		this.height = height;
		image = (BufferedImage) new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		int rgba[] = {0, 0, 0, 0};
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				//image.setRGB(x,y,);
			}
		}
		g = (Graphics2D) image.getGraphics();
	}

	public String load(String fullFilePath) { // TODO: This function
		String confirmationMessage = "";
		try {
			image = ImageIO.read(new File(fullFilePath));
			filename = fullFilePath;
			confirmationMessage = "Loaded " + fullFilePath;
		}catch (Exception e) {
			confirmationMessage = "Could not load file!";
			java.lang.System.out.println(confirmationMessage);
			return confirmationMessage;
		}
		//TODO: make this not crash when loading invalid image
		this.width = image.getWidth();
		this.height = image.getHeight();
		g = (Graphics2D) image.getGraphics();
		return confirmationMessage;
	}

	public void update(int x, int y, boolean clickL, boolean clickR, Keyboard keyboard) {
		gx=x;
		gy=y;
		cursorX = x/zoom;
		cursorY = y/zoom;
		if(cursorX < 0) cursorX=0;
		if(cursorY < 0) cursorY=0;
		if(cursorX >= image.getWidth()) cursorX = image.getWidth()-1;
		if(cursorY >= image.getHeight()) cursorY = image.getHeight()-1;

		if(clickL) {
			if(Utility.inRange(gx, 0, width*zoom) && Utility.inRange(gy, 0, height*zoom)) {
				Color color = Main.brushColor;
				image.setRGB(cursorX, cursorY, color.getRGB());
			}

		}

		if(clickR) {
			Color color = new Color(image.getRGB(cursorX, cursorY), true);
			Main.brushColor=color;
			Console.lastCommand="Set color to: "+color.toString();
			//image.setRGB(cursorX, cursorY, color.getRGB());
		}

		if(keyboard.ctrl && keyboard.s) {
			save();
		}
		if(keyboard.keyCodeTyped == KeyEvent.VK_CONTROL+KeyEvent.VK_O) {
			System.out.println("TEST");
			//Console.registerCommand(new String[]{"open"});
		}
	}

	public void resize(int newWidth, int newHeight){
		BufferedImage newImage = (BufferedImage) new BufferedImage(newWidth, newHeight, BufferedImage.TRANSLUCENT);
		int rgba[] = {0, 0, 0, 0};
		for (int y=0; y<newHeight; y++) {
			for (int x=0; x<newWidth; x++) {
				//image.setRGB(x,y,);
				if(x<width && y<height && x<newWidth && y<newHeight) {
					Color color = new Color(image.getRGB(x, y), true);
					newImage.setRGB(x, y, color.getRGB());
				}
			}
		}
		image=newImage;
		g = (Graphics2D) image.getGraphics();
		this.width = newWidth;
		this.height = newHeight;
	}

	public void save(){
		try{
			File f = new File(filename);
			ImageIO.write(image, "PNG", f);
			java.lang.System.out.println("Image saved!");
		}catch(Exception e){e.printStackTrace();}
	}

	public void saveAs(String string){
		try{
			filename = string;
			File f = new File(filename);
			ImageIO.write(image, "PNG", f);
			java.lang.System.out.println("Image saved as " + filename);
		}catch(Exception e){e.printStackTrace();}
	}

	public void draw(Graphics2D screenGraphics) {
		drawBackground(screenGraphics);
		screenGraphics.drawImage(image, 0, 0, width*zoom, height*zoom, null);
		screenGraphics.setColor(Main.brushColor);
		if(Utility.inRange(gx, 0, width*zoom) && Utility.inRange(gy, 0, height*zoom)) {
			screenGraphics.fillRect(cursorX*zoom, cursorY*zoom, zoom, zoom);
		}

	}

	public void drawBackground(Graphics2D g) {
		//Draws a checkered background
		int size=(4*zoom);
		int xiterations=(width*zoom)/size;
		int yiterations=(height*zoom)/size;
		g.setColor(Color.white);
		g.fillRect(0, 0, width*zoom, height*zoom);
		boolean color=false;
		for (int y=0; y<yiterations; y++) {
			if(xiterations % 2 == 0) {
				color=!color;
			}
			for (int x=0; x<xiterations; x++) {
				color=!color;
				if(color) {
					g.setColor(Color.white);
				} else {
					g.setColor(Color.lightGray);
				}
				int xstart=x*size;
				int ystart=y*size;
				g.fillRect(xstart, ystart, size, size);
			}
		}
	}

}
