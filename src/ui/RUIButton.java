package ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public abstract class RUIButton {
	boolean hovered = false;
	boolean enabled = true;
	boolean visible = true;
	
	public void update(Point mouse){
		if(enabled){
			boolean newhovered = containsPoint(mouse);
			if(!hovered && newhovered){
				onHover();
			}
			hovered = newhovered;
		}
	}
	
	public void draw(Graphics2D g){
		if(visible){
			onDraw(g);
		}
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
		if(!enabled){
			hovered = false;
		}
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public boolean isHovered(){
		return hovered;
	}
	
	protected abstract void onHover();
	
	protected abstract void onDraw(Graphics2D g);
	
	public abstract void drawOverlay(BufferedImage overlay, Graphics2D g);
	
	protected abstract boolean containsPoint(Point p);
	
}