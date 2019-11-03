import java.awt.Color;

class Creator extends WorldObject implements Stepable {
	static Color color = Color.MAGENTA;
	
	@Override
	public Color getColor() {
		return color;
	}
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case PUSH:
			interacter.interact(this, Interaction.KILL, null);
			return true;
		case EAT:
			interacter.interact(this, Interaction.KILL, null);
			return true;
		case PULL:
			interacter.interact(this, Interaction.KILL, null);
		case DISPLACE:
			interacter.interact(this, Interaction.KILL, null);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public void step(){
		int x = M.randInt(location.x - 1, location.x + 1);
		int y = M.randInt(location.y - 1, location.y + 1);
		ArtificialLife.place(new Wall(true, Color.DARK_GRAY), x, y);
	}
}