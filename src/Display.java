import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Display extends Frame {
	private static final long serialVersionUID = 1L;
	public static final Display instance = new Display();
	
	static int drawScale;
	static boolean drawCellVision = false;
	static boolean drawFollowHighlight = false;
	
	static Color bgColor_summer = new Color(126, 126, 126);
	static Color bgColor_winter = new Color(102, 102, 102);
	
	static boolean drawAll = false;
	static int viewX, viewY;
	static int tileSize = 10;
	static int viewRadiusInTiles = 48;
	
	public static void move(Direction direction) {
		Point newLocation = new Point(direction.getVector());
		newLocation.x += viewX;
		newLocation.y += viewY;
		ArtificialLife.wrapPoint(newLocation);
		viewX = newLocation.x;
		viewY = newLocation.y;
		
		// Deselect the selected cell when we move the view. //
		ArtificialLife.selectedCell = null;
	}
	
	Display(){
		setTitle("Artificial Life Sim");
		setLocationRelativeTo(null);
		setResizable(false);
		setSize();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	public void draw(){
		Graphics2D g = (Graphics2D)getBufferStrategy().getDrawGraphics();
		Insets insets = getInsets();
		g.translate(insets.left, insets.top);
		
		// Draw background //
		g.setColor(ArtificialLife.isSummer ? bgColor_summer : bgColor_winter);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if(drawAll) { // If in map-mode, draw the whole map. //
			// Draw world objects //
			for(int x = 0; x < ArtificialLife.width; x ++){
				for(int y = 0; y < ArtificialLife.height; y ++){
					WorldObject object = ArtificialLife.grid[x][y];
					if(object != null){
						g.setColor(object.getColor());
						g.fillRect(drawScale*x, drawScale*y, drawScale, drawScale);
					}
				}
			}
			
			// Draw UI over followed cell. //
			Cell cell = ArtificialLife.selectedCell;
			if(cell != null){
				g.setColor(Color.WHITE);
				if(drawFollowHighlight){
					g.drawLine(0, 0, drawScale*cell.location.x, drawScale*cell.location.y);
				}
				if(drawCellVision){
					cell.drawSenses(g);
				}
			}
		} else { // If not in map-mode, draw only the area around the player. //
			int viewWidth = 2*viewRadiusInTiles + 1;
			for(int x = 0; x < viewWidth; x ++) {
				for(int y = 0; y < viewWidth; y ++) {
					int dx = x - viewRadiusInTiles;
					int dy = y - viewRadiusInTiles;
					WorldObject object = ArtificialLife.getObjectAt(viewX + dx, viewY + dy);
					if(object != null) {
						g.setColor(object.getColor());
						g.fillRect(tileSize*x, tileSize*y, tileSize, tileSize);
					}
				}
			}
			
			// Reticle //
			g.setColor(Color.BLUE);
			g.drawRect(tileSize*viewRadiusInTiles, tileSize*viewRadiusInTiles, tileSize, tileSize);
		}
		
		g.dispose();
		getBufferStrategy().show();
	}
	
	private void setSize() {
		Insets insets = getInsets();
		if(drawAll) {
			setSize(drawScale*ArtificialLife.width + insets.left + insets.right, drawScale*ArtificialLife.height + insets.top + insets.bottom);
		} else {
			int viewSize = (2*viewRadiusInTiles + 1)*tileSize;
			setSize(viewSize + insets.left + insets.right, viewSize + insets.top + insets.bottom);
		}
		setLocationRelativeTo(null);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible) {
			createBufferStrategy(2);
			setSize();
		}
	}
	
	public void toggleDisplayMode() {
		drawAll = !drawAll;
		setSize();
	}
}