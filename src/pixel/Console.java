package pixel;
import pixel.input.Keyboard;
import pixel.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lukes on 2017/02/27.
 */
public class Console {

	int fontSize = 14;
	Font font = new Font("Consolas", Font.PLAIN, fontSize);
	public static String inputLine = "";
	public static String lastCommand = "Press <enter> to type a command.";
	public boolean inputMode = false;
	boolean oldInputMode = false;
	Map<String, ProcessingMethod> methodMap;
	AffineTransform affinetransform = new AffineTransform();
	FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
	String[] commands = {
			"<KEYBINDINGS>",
			"left click        paint",
			"right click       pick color",
			"",
			"<COMMANDS>",
			"save",
			"saveas",
			"saveas <url>",
			"load",
			"load <url>",
			"size <width> <height>",
			"width +/-<num>",
			"height +/-<num>",
			"rgb <red> <green> <blue>",
			"rgba <red> <green> <blue> <alpha>",

	};

	public Console(Main main) {

		methodMap = new HashMap<String, ProcessingMethod>();

		ProcessingMethod cd = new ProcessingMethod() {
			public void method(String[] args) { lastCommand = Main.getWorkingDir();}
		};

		methodMap.put("rgb", new ProcessingMethod() {
			public void method(String[] args) {
				if(args.length>=4) {
					Color color = new Color(Utility.safeParse(args[1]), Utility.safeParse(args[2]), Utility.safeParse(args[3]));
					Main.brushColor=color;
					lastCommand = "Brush color set to " + (Utility.safeParse(args[1]) + ", " + Utility.safeParse(args[2]) + ", " + Utility.safeParse(args[3]));
				}
			}
		});
		methodMap.put("rgba", new ProcessingMethod() {
			public void method(String[] args) {
				if(args.length>=5) {
					Color color = new Color(Utility.safeParse(args[1]), Utility.safeParse(args[2]), Utility.safeParse(args[3]), Utility.safeParse(args[4]));
					Main.brushColor=color;
					lastCommand = "Brush color set to " + (Utility.safeParse(args[1]) + ", " + Utility.safeParse(args[2]) + ", " + Utility.safeParse(args[3]) + ", " + Utility.safeParse(args[4]));
				}
			}
		});
		methodMap.put("save", new ProcessingMethod() {
			public void method(String[] args) {
				main.saveWorksheet();
			}
		});

		methodMap.put("saveas", new ProcessingMethod() {
			public void method(String[] args) {
				if(args.length>1) {
					main.saveAsWorksheet(args[1]);
				}else {
					JFileChooser chooser= new JFileChooser();
					chooser.setCurrentDirectory(new File(main.worksheet.filename));
					int choice = chooser.showSaveDialog(null);
					if (choice != JFileChooser.APPROVE_OPTION) return;
					File chosenFile = chooser.getSelectedFile();
					String url = chosenFile.getAbsolutePath();
					main.saveAsWorksheet(url);
				}

			}
		});

		methodMap.put("zoom", new ProcessingMethod() {
			public void method(String[] args) {
				main.setZoom(Integer.parseInt(args[1]));
			}
		});
		methodMap.put("load", new ProcessingMethod() {
			public void method(String[] args) {
				if(args.length>1) {
					main.loadWorksheet(args[1]);
				}else {
					JFileChooser chooser= new JFileChooser();
					chooser.setCurrentDirectory(new File(main.worksheet.filename));
					int choice = chooser.showOpenDialog(null);
					if (choice != JFileChooser.APPROVE_OPTION) return;
					File chosenFile = chooser.getSelectedFile();
					String url = chosenFile.getAbsolutePath();
					main.loadWorksheet(url);
				}

			}
		});
		methodMap.put("browse", new ProcessingMethod() {
			public void method(String[] args) {
				JFileChooser chooser= new JFileChooser();
				chooser.setCurrentDirectory(new File(main.worksheet.filename));
				int choice = chooser.showOpenDialog(null);
				if (choice != JFileChooser.APPROVE_OPTION) return;
				File chosenFile = chooser.getSelectedFile();
				String url = chosenFile.getAbsolutePath();
				main.loadWorksheet(url);
			}
		});
		methodMap.put("size", new ProcessingMethod() {
			public void method(String[] args) {
				main.resize(Utility.safeParse(args[1]), Utility.safeParse(args[2]));
			}
		});
		methodMap.put("height", new ProcessingMethod() {
			public void method(String[] args) {
				main.resize(main.worksheet.width, Utility.safeParse(args[1])+main.worksheet.height);
			}
		});
		methodMap.put("width", new ProcessingMethod() {
			public void method(String[] args) {
				main.resize(Utility.safeParse(args[1])+main.worksheet.width, main.worksheet.height);
			}
		});
		methodMap.put("cd", cd);
	}

	public void update(Keyboard keyboard){
		if(!inputMode){
			if(keyboard.keyCodeTyped == 10) {
				inputMode = true;
			}
		}
		if(inputMode){

			if(keyboard.keyCodeTyped != 32 && keyboard.keyCodeTyped != 19) {
				inputLine+=keyboard.keyTyped.trim();
			}else{inputLine+=keyboard.keyTyped;}

			if(keyboard.keyCodeTyped == 22) {
				inputLine += Utility.getClipboardContents().trim();
			}

			if(keyboard.keyCodeTyped == 8 && inputLine.length() > 0) inputLine = inputLine.substring(0, inputLine.length()-1);
			if(keyboard.keyCodeTyped == 10 && inputLine.length() > 0) {
				java.lang.System.out.println("Input line "+inputLine);
				//String[] args = inputLine.split(" ");
				String[] tmp0 = inputLine.split("\\(");
				String tmp1 = String.join(" ", tmp0);
				String[] tmp2 = tmp1.split("\\)");
				String tmp3 = String.join(" ", tmp2);
				String[] tmp4 = tmp3.split(", ");
				String tmp5 = String.join(" ", tmp4);
				String[] tmp6 = tmp3.split(",");
				String tmp7 = String.join(" ", tmp6);
				String noRepeatSpaces = tmp7.replaceAll("\\s+", " ").trim();
				String[] args = noRepeatSpaces.split(" ");
				if(args!=null && args.length>0) {
					java.lang.System.out.println(methodMap);
					if (methodMap.containsKey(args[0])) methodMap.get(args[0]).method(args);
				}
				inputLine = "";
				inputMode = false;
			}
			if(keyboard.keyCodeTyped == 10 && inputLine.length() == 0 && oldInputMode == true){
				inputMode = false;
			}
		}
		oldInputMode = inputMode;
	}

	public synchronized void draw(Graphics g, int width, int height){
		long curtime = System.currentTimeMillis();
		g.setFont(font);
		g.setColor(Color.white);
		/*if(lines.size()>0){
			for(int l = lines.size()-1; l >= 0; l--){
				if(lines.get(l).clr != null){
					g.setColor(lines.get(l).clr);
				}else{g.setColor(chatColor);}
				if(curtime - lines.get(l).time < 7000 || inputMode) {
					Color clr = lines.get(l).clr;
					if(curtime - lines.get(l).time > 6800 && !inputMode) g.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 128));
					g.drawString(lines.get(l).str, x, lowerY-(((lines.size() - l)*textSize)));
				}
			}
			g.setColor(game.userColor);
			if(inputMode) {
				g.drawString(inputLine+"_", x, lowerY);
			}else{g.drawString(inputLine+"", x, lowerY);}
		}
		*/

		if(inputMode) {
			int w = (int)(font.getStringBounds("> "+inputLine+"_", frc).getWidth());
			int h = (int)(font.getStringBounds("> "+inputLine+"_", frc).getHeight());
			g.setColor(new Color(0,0,0,96));
			g.fillRect(2-2, height-fontSize-4, w+8, h+4);
			g.fillRect(0, 0, Main.width, Main.height);
			g.setColor(Color.white);

			//Instructions
			int xi=2;
			int yi=fontSize;
			String extra=null;
			int iterations=commands.length;
			int max=(Main.height/(fontSize))-2;
			if(iterations>max) {
				iterations=max-1;
				extra="[...]";
			}
			for (int i = 0; i < iterations; i++) {
				g.drawString(commands[i], xi, yi);
				yi+=fontSize;
			}
			if(extra!=null) {
				//yi+=fontSize;
				g.drawString(extra, xi, yi);
			}

			//Input Line
			g.drawString("> "+inputLine+"_", 2, height-4);
		}
		else {
			int w = (int)(font.getStringBounds(lastCommand+" ", frc).getWidth());
			int h = (int)(font.getStringBounds(lastCommand+" ", frc).getHeight());
			g.setColor(new Color(0,0,0,128));
			g.fillRect(2-2, height-fontSize-4, w+8, h+4);
			g.setColor(Color.white);
			g.drawString(lastCommand, 2, height-4);
		}
	}
}
