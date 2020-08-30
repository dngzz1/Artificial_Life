import java.awt.Color;

class Wall extends WorldObject {
	
	Color color;
	boolean isDisplaceable;
	boolean isMovable;
	
	Wall(){
		color = Color.BLACK;
	}
	
	Wall(boolean isDisplacable, boolean isMovable, Color color){
		this.isDisplaceable = isDisplacable;
		this.isMovable = isMovable;
		this.color = color;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String getDisplayName() {
		return "Wall";
	}

	@Override
	public String getInfo() {
		String info = "";
		return info;
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object[] data) {
		switch (interactionType) {
		case PUSH:
			if(isMovable){
				return push(interacter, this);
			} else {
				return false;
			}
		case PULL:
			if(isMovable){
				return pull(interacter, this);
			} else {
				return false;
			}
		case DISPLACE:
			if(isDisplaceable){
				return displace(interacter, this);
			} else {
				return false;
			}
		default:
			return false;
		}
	}
}