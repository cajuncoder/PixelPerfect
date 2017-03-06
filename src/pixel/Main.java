package pixel;

import pixel.input.Keyboard;
import pixel.input.Mouse;
import pixel.input.MouseMotion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

/**
 * Created by lukes on 2017/02/25.
 * to do:
 * - clr names: red, red orange, orange, yellow orange, yellow, yellow green, green, blue green, blue, blue violet, violet, red violet
 * - add HSV commands
 * - add color fill
 * - add undo buffer
 * - add a color palette
 * - add select and move
 * - add copy and paste images to and from clipboard
 * - allow images to be centered
 * - add pan for large images
 * - add color picker?
 * - save as pbg, bmp, etc
 */
public class Main {
	// ****************************************************//
	//                    Pixel Perfect                    //
	// ****************************************************//

	/* 
	 * Don't Touch My Globals (By Which I Mean Touch Them Whenever You Want, 
	 * That's What They're Here For So Don't Encapsulate Them!) 
	 */
	public static String brushType = "brush";
	public static Color brushColor = Color.BLACK;
	public static int brushSize = 1;
	public static enum tool {PENCIL, SELECT};
	// JFrame
	public JFrame jframe = new JFrame();
	public static JPanel jpanel = new JPanel();

	// Graphics
	public static int width = 960;//32*16; //272
	public static int height = 540; //208
	public int oldHeight = 0;
	public int  oldWidth = 0;
	public int scale = 1;
	public BufferedImage bufferImage;
	public Graphics2D bufferGraphics;
	public Graphics2D g;
	public Worksheet worksheet;
	public Worksheet palette;
	public Worksheet activeSheet = worksheet;
	// Keyboard
	public Keyboard keyboard;
	public Mouse mouse;
	public Console console;
	public MouseMotion mouseMotion;
	//public MouseWheel mouseWheel;
	
	public Main() {
		System.setProperty("sun.java2d.opengl","True");
		System.setProperty("java.awt.Graphics2D.opengl", "True");
		long maxBytes = Runtime.getRuntime().maxMemory();
		System.out.println("Max memory: " + maxBytes / 1024 / 1024 + "M");
		//JOptionPane.showMessageDialog(jframe, ("Max memory: " + maxBytes / 1024 / 1024 + "M"));
		// JFrame
		jframe.setContentPane(jpanel);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//jframe.setResizable(false);
		jframe.setVisible(true);
		jframe.setFocusable(true);
		
		// JPanel
		jpanel.setPreferredSize(new Dimension(width * scale, height * scale));
		jpanel.setFocusable(false);

		// jpanel.requestFocus();
		jframe.pack();
		jframe.setLocationRelativeTo(null);

		// Input
		keyboard = new Keyboard(this);
		mouse = new Mouse(this);
		mouseMotion = new MouseMotion(this);
		console = new Console(this);
		//mouseWheel = new MouseWheel();
		jframe.addKeyListener(keyboard);
		jpanel.addMouseListener(mouse);
		jpanel.addMouseMotionListener(mouseMotion);
		//jpanel.addMouseWheelListener(mouseWheel);
		jframe.setFocusTraversalKeysEnabled(false);
	}
	
	// ------------------Main--------------------//
	public static void main(String[] args) {
		Main program = new Main();
		program.run();
	}

	// -------------------Run--------------------//
	public void run() {
		try{

			width = jpanel.getWidth()/scale;
			height = jpanel.getHeight()/scale;
			//bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			bufferImage = (BufferedImage) jpanel.createImage(width, height);
			bufferGraphics = (Graphics2D) bufferImage.getGraphics();
			//bufferGraphics.finalize();

		// time	
		long dt = 1000000000 / 20;
		long fpslimit = 1000000000 / 60;
		long currentTime = System.nanoTime();
		long oldfps = 0;
		long lastRender = 0;
		int ticks = 0;
		long accumulator = 0;
		long t = 0;
		int fps = 0;

			//////////////////////////////////////////
			//test canvas
			worksheet = new Worksheet(32, 32);
			palette = new Worksheet(32, 32);
			palette.load("palette.png");
			palette.zoom=24;
			//////////////////////////////////////////

		while (true) {
			width = jpanel.getWidth()/scale;
			height = jpanel.getHeight()/scale;
			
			if(oldWidth != width || oldHeight != height) {
				bufferImage = (BufferedImage) jpanel.createImage(width, height);

				//bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				bufferGraphics = (Graphics2D) bufferImage.getGraphics();
				bufferGraphics.finalize();
				g = graphicsContext();
			}

			// update
			long newTime = System.nanoTime();
			long frameTime = newTime - currentTime;
			currentTime = newTime;
			accumulator += frameTime;
			
			while (accumulator >= dt) {
				//we don't need t passed into update.
				//this runs at 60 ticks regardless, which is all we need.
				update();
				accumulator -= dt;
				t += dt;
				ticks++;
			}
			
			if (currentTime - lastRender >= fpslimit) {
				//jpanel.repaint();
				render();
				fps++;
				lastRender = currentTime;
			}
			//THIS BREAKS FPS COUNTER
			if (currentTime - oldfps >= 100000000) {//1000000000) {
				String f = "";
				int w = 0;
				int h = 0;
				int x = 0;
				int y = 0;

				if(worksheet!=null) {
					String[] s=worksheet.filename.split("\\\\"); //how many backslashes are required to split a string?
					String s1 = String.join("/", s);
					String[] s2=s1.split("/");
					int count=0;
					f+=".../";
					if(s2.length>1) {
						f+=s2[s2.length-2];
						f+="/";
					}
					f+=s2[s2.length-1];
					//f = worksheet.filename;
					x = worksheet.cursorX;
					y = worksheet.cursorY;
					w = worksheet.width;
					h = worksheet.height;
				}
				jframe.setTitle("PixelPerfect [" + f + "] (" + w + "x" + h + ")" + " x:" + x + " y:" + y);
				fps = 0; //FPS COUNTER IS BROKEN
				ticks = 0;
				oldfps = currentTime;
			}
			
			oldWidth = width;
			oldHeight = height;
		}
		}catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(jframe, e.getStackTrace(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		}
	}

	public void update() {
		keyboard.update();
		console.update(keyboard);
		if(keyboard.shift) {
			activeSheet=palette;
		}else {
			activeSheet=worksheet;
		}
		activeSheet.update(mouseMotion.x, mouseMotion.y, mouse.clickL, mouse.clickR, keyboard);
	}
	
	public void render() {
		g = graphicsContext();
		bufferGraphics.setColor(Color.GRAY);
		bufferGraphics.fillRect(0, 0, width * scale, height * scale);
		//drawBackground(bufferGraphics);
		worksheet.draw(bufferGraphics);
		if(activeSheet==palette) palette.draw(bufferGraphics);
		console.draw(bufferGraphics, width, height);
		g.drawImage(bufferImage, 0, 0, width * scale, height * scale, jpanel);
		g.dispose();
	}


	public Graphics2D graphicsContext() {
		//if(g!=null) g.dispose();
		Graphics2D gc = (Graphics2D) jpanel.getGraphics();
		gc.finalize();
		gc.setBackground(Color.black);
		return gc;
	}

	public void saveWorksheet() {
		if(worksheet!=null) worksheet.save();
	}

	public void loadWorksheet(String path) {
		String result = worksheet.load(path);
		console.lastCommand = result;
	}
	public void saveAsWorksheet(String string) {
		if(worksheet!=null) {
			worksheet.saveAs(string);
		}
	}

	public void setZoom(int z) {
		worksheet.zoom = z;
	}

	public void resize(int x, int y) {
		worksheet.resize(x, y);
	}

	public static String getWorkingDir() {
		String path = System.getProperty("user.dir");
		String stringsArentMutable = path.replace('\\', '/');
		stringsArentMutable+='/';
		return stringsArentMutable;
	}
	/*
	public void drawBackground(Graphics2D g) {
		//Draws a checkered background
		int size=8;
		int xiterations=width/size;
		int yiterations=height/size;
		boolean color=true;
		for (int y=0; y<yiterations; y++) {
			for (int x=0; x<xiterations; x++) {
				color=!color;
				if(color) {
					g.setColor(Color.white);
				} else {
					g.setColor(Color.lightGray);
				}
				int xstart=x*size;
				int ystart=y*size;
				g.fillRect(xstart, ystart, xstart+size, ystart+size);
			}
			color=!color;
		}
	}*/
}
