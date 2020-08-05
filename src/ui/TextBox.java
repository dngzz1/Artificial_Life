package ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import general.Util;

public class TextBox {
	private Font font;
	private int x, y, w;
	private String text;
	
	public TextBox(String text, Font font, int x, int y, int w){
		this.text = text;
		this.font = font;
		this.x = x;
		this.y = y;
		this.w = w;
	}
	
	public void draw(Graphics2D g) {
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		int fontHeight = fm.getAscent() + fm.getDescent();
		
		// Split text into lines and words. //
		LinkedList<String> wordList = new LinkedList<String>();
		LinkedList<String> lineList = new LinkedList<String>();
		String[] lines = text.split("\n");
		for(String line : lines) {
			for(String word : line.split(" ")) {
				wordList.add(word);
			}
			wordList.add("\n");
		}
		
		// Perform word-wrapping. //
		String nextLine = "";
		for(String word : wordList) {
			if(word.equals("\n")) {
				lineList.add(nextLine);
				nextLine = "";
			} else if(fm.stringWidth(nextLine+" "+word) > w) {
				lineList.add(nextLine);
				nextLine = word;
			} else if(nextLine.equals("")){
				nextLine = word;
			} else {
				nextLine = nextLine+" "+word;
			}
		}
		
		// Draw the lines of text. //
		int i = 0;
		for(String line : lineList) {
			Util.drawStringCenteredY(line, x, y + i*fontHeight, g);
			i ++;
		}
	}
}