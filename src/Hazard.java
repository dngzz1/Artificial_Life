import java.awt.Color;

class Hazard extends WorldObject {
	static Color color = Color.RED;
	
	@Override
	public Color getColor() {
		return color;
	}
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType) {
		switch (interactionType) {
		case PUSH:
			interacter.interact(this, Interaction.KILL);
			return true;
		case EAT:
			interacter.interact(this, Interaction.KILL);
			return true;
		case PULL:
			return pull(interacter, this);
		case DISPLACE:
			interacter.interact(this, Interaction.KILL);
			return true;
		default:
			return false;
		}
	}
}