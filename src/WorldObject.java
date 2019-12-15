import java.awt.Color;
import java.awt.Point;

abstract class WorldObject {
	protected Point location;
	
	public static boolean displace(WorldObject object1, WorldObject object2){
		Point location1 = object1.getLocation();
		Point location2 = object2.getLocation();
		ArtificialLife.grid[location1.x][location1.y] = object2;
		ArtificialLife.grid[location2.x][location2.y] = object1;
		object1.location.setLocation(location2);
		object2.location.setLocation(location1);
		return true;
	}
	
	public static boolean pull(WorldObject puller, WorldObject pulled){
		Point pullerLocation = puller.getLocation();
		boolean canPull = push(pulled, puller);
		if(canPull){
			pulled.setLocation(pullerLocation);
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean push(WorldObject pusher, WorldObject pushed){
		int dx = pushed.getX() - pusher.getX();
		int dy = pushed.getY() - pusher.getY();
		Point p = new Point(pushed.getX() + dx, pushed.getY() + dy);
		ArtificialLife.wrapPoint(p);
		if(ArtificialLife.grid[p.x][p.y] == null){
			pushed.setLocation(p);
			return true;
		} else {
			return false;
		}
	}
	
	public abstract Color getColor();
	
	public Point getAdjacentLocation(Direction direction) {
		Point adjacentLocation = M.add(direction.getVector(), getLocation());
		ArtificialLife.wrapPoint(adjacentLocation);
		return adjacentLocation;
	}
	
	public Point getLocation() {
		return new Point(location);
	}
	
	public int getX() {
		return location.x;
	}
	
	public int getY() {
		return location.y;
	}
	
	public abstract boolean interact(WorldObject interacter, Interaction interactionType, Object data);
	
	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}
	
	public void setLocation(int x, int y) {
		if(location != null){
			ArtificialLife.grid[location.x][location.y] = null;
			location.setLocation(x, y);
		} else {
			location = new Point(x, y);
		}
		ArtificialLife.grid[x][y] = this;
	}
}