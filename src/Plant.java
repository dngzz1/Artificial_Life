import java.awt.Color;

class Plant extends WorldObject implements Stepable {
	static Color color = new Color(0, 100, 0);
	
	int growth = 0;
	int growthThreshold = M.randInt(25, 100);
	
	private void fruit() {
		int x = M.randInt(location.x - 1, location.x + 1);
		int y = M.randInt(location.y - 1, location.y + 1);
		Display.place(new Food(), x, y);
	}
	
	@Override
	public Color getColor() {
		return color;
	}
	@Override
	public boolean interact(WorldObject interacter, Interaction interactionType) {
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
		if(growth >= growthThreshold){
			fruit();
			growth = 0;
		} else {
			growth ++;
		}
	}
}