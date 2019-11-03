import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class Display extends Frame {
	private static final long serialVersionUID = 1L;
	
	static int drawScale;
	static boolean drawEyeRays = false;
	static boolean drawFollowHighlight = false;
	
	static Color bgColor = Color.cyan;
	static Color gridColor = Color.gray;
	
	Display(){
		setResizable(false);
		setSize(drawScale*ArtificialLife.width + 16, drawScale*ArtificialLife.height + 39);
		
		setTitle("Artificial Life Sim");
		setLocationRelativeTo(null);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	public void draw(){
		Graphics2D g = (Graphics2D)getBufferStrategy().getDrawGraphics();
		g.setBackground(bgColor);
		g.clearRect(0, 0, getWidth(), getHeight());
		Insets insets = getInsets();
		g.translate(insets.left, insets.top);
		
		//Draw background
		g.setColor(gridColor);
		g.fillRect(0, 0, drawScale*ArtificialLife.width, drawScale*ArtificialLife.height);
		
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
		Cell cell = ArtificialLife.getFollowedCell();
		if(cell != null){
			g.setColor(Color.WHITE);
			if(drawFollowHighlight){
				g.drawLine(0, 0, drawScale*cell.location.x, drawScale*cell.location.y);
			}
			if(drawEyeRays){
				cell.drawSenses(g);
			}
		}
		
		g.dispose();
		getBufferStrategy().show();
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible) {
			createBufferStrategy(2);
			Insets insets = getInsets();
			setSize(drawScale*ArtificialLife.width + insets.left + insets.right, drawScale*ArtificialLife.height + insets.top + insets.bottom);
		}
	}
}