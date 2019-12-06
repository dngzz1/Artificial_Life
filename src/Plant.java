import java.awt.Color;

class Plant extends WorldObject implements Stepable {
	static Color color = new Color(0, 100, 0);
	
	int stepsToBearFruit = M.randInt(50, 70);
	
	private void fruit() {
		int x = M.randInt(location.x - 1, location.x + 1);
		int y = M.randInt(location.y - 1, location.y + 1);
		ArtificialLife.place(new Food(), x, y);
	}
	
	@Override
	public Color getColor() {
		return color;
	}
	
	@Override
	public int getStepsToNextTurn() {
		return stepsToBearFruit;
	}
	
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType, Object data) {
		switch (interactionType) {
		case PUSH:
			return push(interacter, this);
		case PULL:
			return pull(interacter, this);
		case DISPLACE:
			return displace(interacter, this);
		default:
			return false;
		}
	}
	
	@Override
	public void step(){
		fruit();
	}
}