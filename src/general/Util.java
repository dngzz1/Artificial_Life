package general;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.LinkedList;

import javax.swing.JFrame;

import maths.M;

public class Util {
	
	public static void centreWindowOnScreen(JFrame window){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((int)(screenSize.getWidth() - window.getWidth())/2, (int)(screenSize.getHeight() - window.getHeight())/2);
	}
	
	@SuppressWarnings("unchecked")
	public static <X> LinkedList<X> cloneList(LinkedList<X> list){
		return (LinkedList<X>)list.clone();
	}
	
	/**
	 * Returns true if there exists an index i such that array[i] == item.
	 * @param array
	 * @param item
	 * @return 
	 */
	public static boolean doesArrayContain(Object[] array, Object item){
		for(int i = 0; i < array.length; i ++){
			if(array[i] == item){
				return true;
			}
		}
		return false;
	}
	
	public static void drawCircleCentered(double x, double y, double r, Graphics2D g){
		int ix = (int)(x - r);
		int iy = (int)(y - r);
		int diameter = (int)(2*r);
		g.drawOval(ix, iy, diameter, diameter);
	}
	
	public static void drawStringCenteredX(String str, int x, int y, Graphics2D g){
		int strX = x - g.getFontMetrics().stringWidth(str)/2;
		g.drawString(str, strX, y);
	}
	
	public static void drawStringCenteredXY(String str, int x, int y, Graphics2D g){
		int strX = x - g.getFontMetrics().stringWidth(str)/2;
		int strY = y + (g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent())/2;
		g.drawString(str, strX, strY);
	}
	
	public static void drawStringCenteredY(String str, int x, int y, Graphics2D g){
		int strY = y + (g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent())/2;
		g.drawString(str, x, strY);
	}
	
	public static void fillCircleCentered(double x, double y, double r, Graphics2D g){
		int ix = (int)(x - r);
		int iy = (int)(y - r);
		int diameter = (int)(2*r);
		g.fillOval(ix, iy, diameter, diameter);
	}
	
	/**
	 * Iterates through the array and returns the first index i such that array[i] == item.
	 * @param array
	 * @param item
	 * @return the first index i such that array[i] == item
	 */
	public static int getIndexOf(Object[] array, Object item){
		for(int i = 0; i < array.length; i ++){
			if(array[i] == item){
				return i;
			}
		}
		return -1;
	}
	
	public static boolean isAllTrue(boolean[] array){
		for(int i = 0; i < array.length; i ++){
			if(!array[i]){
				return false;
			}
		}
		return true;
	}
	
	public static String repeatCharacter(char character, int length){
		String str = "";
		for(int i = 0; i < length; i ++) {
			str += character;
		}
		return str;
	}
	
	public static String repeatString(String str, int length){
		String returnString = "";
		for(int i = 0; i < length; i ++) {
			returnString += str;
		}
		return returnString;
	}
	
	public static <X> void shuffle(LinkedList<X> listToShuffle){
		LinkedList<X> shuffledList = new LinkedList<X>();
		while(!listToShuffle.isEmpty()){
			shuffledList.add(M.removeRandom(listToShuffle));
		}
		listToShuffle.addAll(shuffledList);
	}
	
	public static <X> void updateList(LinkedList<X> list, LinkedList<X> addList, LinkedList<X> removeList){
		list.addAll(addList);
		addList.clear();
		list.removeAll(removeList);
		removeList.clear();
	}
}