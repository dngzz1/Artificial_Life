import java.awt.Color;

class Wall extends WorldObject {
	
	Color color;
	boolean isDisplaceable;
	
	Wall(boolean isDisplacable, Color color){
		this.isDisplaceable = isDisplacable;
		this.color = color;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case PUSH:
			return push(interacter, this);
		case PULL:
			return pull(interacter, this);
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