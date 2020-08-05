package ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import audio.SoundHandler;

import files.ImageHandler;

public class RUIImageButton extends RUIButton {
	public static final int HOVERACTION_NONE = 0, HOVERACTION_INDENT = 1;
	
	public static String hoverSound = "";
	int x, y;
	int hoverAction = HOVERACTION_NONE;
	BufferedImage image;
	
	public RUIImageButton(int x, int y, String imageFilename, int hoverAction){
		this.x = x;
		this.y = y;
		this.hoverAction = hoverAction;
		this.image = ImageHandler.loadImage(imageFilename);
	}
	
	public RUIImageButton(int x, int y, BufferedImage image, int hoverAction){
		this.x = x;
		this.y = y;
		this.hoverAction = hoverAction;
		this.image = image;
	}
	
	public void onDraw(Graphics2D g) {
		if(hovered){
			switch(hoverAction){
			case HOVERACTION_INDENT:
				g.drawImage(image, null, x + 10, y);
				return;
			}
			g.drawImage(image, null, x, y);
		} else {
			g.drawImage(image, null, x, y);
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