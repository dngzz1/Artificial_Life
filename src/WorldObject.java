import java.awt.Color;
import java.awt.Point;

abstract class WorldObject {
	protected Point location;
	
	public static boolean displace(WorldObject object1, WorldObject object2){
		Point location1 = object1.getLocation();
		Point location2 = object2.getLocation();
		Display.grid[location1.x][location1.y] = object2;
		Display.grid[location2.x][location2.y] = object1;
		object1.location.setLocation(location2);
		object2.location.setLocation(location1);
		return true;
	}
	
	public static boolean pull(WorldObject puller, WorldObject pulled){
		Point pullerLocation = puller.getLocation();
		boolean canPull = push(puller, puller);
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
		Display.wrapPoint(p);
		if(Display.grid[p.x][p.y] == null){
			pushed.setLocation(p);
			return true;
		} else {
			return false;
		}
	}
	
	public abstract Color getColor();
	
	public Point getAdjacentLocation(Direction direction) {
		Point adjacentLocation = M.add(direction.getVector(), getLocation());
		Display.wrapPoint(adjacentLocation);
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
	
	public abstract boolean interact(WorldObject interacter, Interaction interactionType);
	
	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}
	
	public void setLocation(int x, int y) {
		if(location != null){
			Display.grid[location.x][location.y] = null;
			location.setLocation(x, y);
		} else {
			location = new Point(x, y);
		}
		Display.grid[x][y] = this;
	}
}