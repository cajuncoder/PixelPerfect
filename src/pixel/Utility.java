package pixel;

/**
 * Created by lukes on 2017/03/01.
 */

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.io.*;

public class Utility implements ClipboardOwner {

	public static Integer safeParse(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return 1;
		}
	}
	public static boolean inRange(int v, int minimum, int maximum) {
		if(v<maximum && v>=minimum) {
			return true;
		}
		return false;
	}
	/*
	public static void main(String...  aArguments ){
		Utility textTransfer = new Utility();

		//display what is currently on the clipboard
		System.out.println("Clipboard contains:" + textTransfer.getClipboardContents());

		//change the contents and then re-display
		textTransfer.setClipboardContents("blah, blah, blah");
		System.out.println("Clipboard contains:" + textTransfer.getClipboardContents());
	}
	*/

	/**
	 * Empty implementation of the ClipboardOwner interface.
	 */
	@Override public void lostOwnership(Clipboard aClipboard, Transferable aContents){
		//do nothing
	}

	/**
	 * Place a String on the clipboard, and make this class the
	 * owner of the Clipboard's contents.
	 */
	public static void setClipboardContents(String aString){
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	/**
	 * Get the String residing on the clipboard.
	 *
	 * @return any text found on the Clipboard; if none found, return an
	 * empty String.
	 */
	public static String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		//odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText =
				(contents != null) &&
						contents.isDataFlavorSupported(DataFlavor.stringFlavor)
				;
		if (hasTransferableText) {
			try {
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException | IOException ex){
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		return result;
	}
}

