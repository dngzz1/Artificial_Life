import java.awt.Point;

enum Direction {
	N, NE, E, SE, S, SW, W, NW;
	
	private static Point[] vectorList = {
		new Point(0, -1),
		new Point(1, -1),
		new Point(1, 0),
		new Point(1, 1),
		new Point(0, 1),
		new Point(-1, 1),
		new Point(-1, 0),
		new Point(-1, -1)
	};
	
	public static Direction getDirectionFromVector(int dx, int dy){
		for(int i = 0; i < vectorList.length; i ++){
			if(vectorList[i].x == dx && vectorList[i].y == dy){
				return values()[i];
			}
		}
		return null;
	}
	
	public Point getVector(){
		return vectorList[ordinal()];
	}
	
	public Direction rotateACW(){
		return values()[(ordinal() + 7)%8];
	}
	
	public Direction rotateCW(){
		return values()[(ordinal() + 1)%8];
	}
}