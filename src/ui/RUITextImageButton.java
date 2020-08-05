package ui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import audio.SoundHandler;

import files.ImageHandler;

public class RUITextImageButton extends RUIButton {
	public static final int HOVERACTION_NONE = 0, HOVERACTION_INDENT = 1;
	
	public static String hoverSound = "";
	int x, y;
	int hoverAction = HOVERACTION_NONE;
	BufferedImage image;
	String text;
	Font font;
	
	public RUITextImageButton(int x, int y, String imageFilename, String text, Font font, int hoverAction){
		this.x = x;
		this.y = y;
		this.hoverAction = hoverAction;
		image = ImageHandler.loadImage(imageFilename);
		this.text = text;
		this.font = font;
	}
	
	public void onDraw(Graphics2D g) {
		g.setFont(font);
		if(hovered){
			switch(hoverAction){
			case HOVERACTION_INDENT:
				g.drawImage(image, null, x + 10, y);
				g.drawString(text, x + font.getSize() + 10, y + font.getSize());
				return;
			}
			g.drawImage(image, null, x, y);
			g.drawString(text, x + font.getSize(), y + font.getSize());
		} else {
			g.drawImage(image, null, x, y);
			g.drawString(text, x + font.getSize(), y + font.getSize());
		}
	}
	
	public void drawOverlay(BufferedImage overlay, Graphics2D g){
		g.drawImage(overlay, null, x, y);
	}
	
	protected void onHover(){
		if(hoverSound != ""){
			SoundHandler.playSound(hoverSound);
		}
	}
	
	protected boolean containsPoint(Point p){
		return (p.x > x && p.x < x + image.getWidth() && p.y > y && p.y < y + image.getHeight());
	}
}