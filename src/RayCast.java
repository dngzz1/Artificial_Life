import java.awt.Point;

class RayCast {
	
	public static RayCastResult castRay(Point location, Direction direction, int maxLength) {
		WorldObject seenObject = null;
		Point p = new Point(location);
		Point d = direction.getVector();
		int distance;
		for(distance = 0; distance < maxLength; distance ++){
			p.x += d.x;
			p.y += d.y;
			seenObject = ArtificialLife.getObjectAt(p);
			if(seenObject != null){
				break;
			}
		}
		return new RayCastResult(seenObject, distance);
	}
}

class RayCastResult {
	WorldObject object;
	int distanceToObject;
	
	RayCastResult(WorldObject object, int distanceToObject) {
		this.object = object;
		this.distanceToObject = distanceToObject;
	}
}