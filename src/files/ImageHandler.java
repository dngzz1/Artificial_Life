package files;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ImageHandler {
	
	public static BufferedImage loadImage(String filename){
		filename = FileUtils.executionPath+filename;// Append the execution path to the start of the filename to ensure we look in the correct directory.
		return loadImage(new File(filename));
	}
	
	public static BufferedImage loadImage(File file){
		BufferedImage im = null;
		try {
			im = ImageIO.read(file);
		} catch(IOException e){
			e.printStackTrace();
			Object[] messages = {e, file};
			JOptionPane.showMessageDialog(null, messages, "Error", JOptionPane.ERROR_MESSAGE);
		}
		return im;
	}
	
	public static BufferedImage[] loadTiledImage(String filename, int xTiles, int yTiles){
		filename = FileUtils.executionPath+filename;// Append the execution path to the start of the filename to ensure we look in the correct directory.
		
		// Load the image. //
		BufferedImage im = null;
		try {
			im = ImageIO.read(new File(filename));
		} catch(IOException e){
			e.printStackTrace();
		}
		// Cut the image up into tiles and put into the array. //
		BufferedImage[] imList = new BufferedImage[xTiles*yTiles];
		int tileW = im.getWidth()/xTiles;
		int tileH = im.getHeight()/yTiles;
		for(int x = 0; x < xTiles; x ++){
			for(int y = 0; y < yTiles; y ++){
				imList[y + yTiles*x] = im.getSubimage(x*tileW, y*tileH, tileW, tileH);
			}
		}
		return imList;
	}
}